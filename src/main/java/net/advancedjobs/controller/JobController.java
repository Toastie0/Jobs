package net.advancedjobs.controller;

import net.advancedjobs.config.ConfigManager;
import net.advancedjobs.economy.ImpactorEconomyHandler;
import net.advancedjobs.objects.Job;
import net.advancedjobs.objects.Reward;
import net.advancedjobs.objects.User;
import net.advancedjobs.objects.UserJobInfo;
import net.advancedjobs.storage.JsonStorageManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core controller for job management.
 * Handles joining, leaving, progress tracking, and leveling up.
 */
public class JobController {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobController.class);
    
    private final ConfigManager configManager;
    private final JsonStorageManager storageManager;
    private final ImpactorEconomyHandler economyHandler;
    private final MinecraftServer server;
    
    private final Map<UUID, Long> leaveCooldowns = new ConcurrentHashMap<>();
    
    // Cache for formula evaluation results: "formula:level" -> result
    private final Map<String, BigDecimal> formulaCache = new ConcurrentHashMap<>();
    
    public JobController(ConfigManager configManager, JsonStorageManager storageManager, 
                         ImpactorEconomyHandler economyHandler, MinecraftServer server) {
        this.configManager = configManager;
        this.storageManager = storageManager;
        this.economyHandler = economyHandler;
        this.server = server;
    }
    
    /**
     * Attempts to join a job.
     * 
     * @return true if successful, false if failed (full, already joined, etc)
     */
    public boolean joinJob(UUID playerUuid, String jobId) {
        User user = storageManager.loadUser(playerUuid);
        Optional<Job> jobOpt = configManager.getJob(jobId);
        
        if (jobOpt.isEmpty()) {
            sendMessage(playerUuid, "§cJob not found: " + jobId);
            return false;
        }
        
        Job job = jobOpt.get();
        
        // Check if already in this job and active
        UserJobInfo existingInfo = user.getJobInfo(jobId);
        if (existingInfo != null && existingInfo.isActive()) {
            sendMessage(playerUuid, "§cYou are already in the " + job.getDisplayName() + " job!");
            return false;
        }
        
        // Check max active jobs limit
        int maxJobs = configManager.getInt("jobs.maxActiveJobs");
        if (user.getActiveJobCount() >= maxJobs) {
            sendMessage(playerUuid, "§cYou already have " + maxJobs + " active jobs! Leave one first.");
            return false;
        }
        
        // Check cooldown if rejoining
        if (existingInfo != null && !existingInfo.isActive()) {
            long cooldownMinutes = configManager.getInt("jobs.leaveCooldownMinutes");
            long timeSinceLeave = System.currentTimeMillis() - existingInfo.getLastLeaveTime();
            long remainingCooldown = (cooldownMinutes * 60 * 1000) - timeSinceLeave;
            
            if (remainingCooldown > 0) {
                long minutesLeft = remainingCooldown / 60000;
                sendMessage(playerUuid, "§cYou must wait " + minutesLeft + " more minutes before rejoining " + job.getDisplayName());
                return false;
            }
        }
        
        // Join or rejoin the job
        if (existingInfo != null) {
            // Rejoining with saved progress
            existingInfo.setActive(true);
            sendMessage(playerUuid, "§aRejoined " + job.getDisplayName() + " at level " + existingInfo.getLevel() + "!");
        } else {
            // New job
            UserJobInfo newInfo = new UserJobInfo(jobId);
            newInfo.setRequiredProgress(job.calculateRequiredProgress(1));
            newInfo.setActive(true);
            user.setJobInfo(jobId, newInfo);
            sendMessage(playerUuid, "§aJoined " + job.getDisplayName() + "!");
        }
        
        storageManager.markDirty(playerUuid);
        return true;
    }
    
    /**
     * Leaves a job while preserving progress.
     * 
     * @return true if successful, false if not in job
     */
    public boolean leaveJob(UUID playerUuid, String jobId) {
        User user = storageManager.loadUser(playerUuid);
        UserJobInfo jobInfo = user.getJobInfo(jobId);
        
        if (jobInfo == null || !jobInfo.isActive()) {
            sendMessage(playerUuid, "§cYou are not in this job!");
            return false;
        }
        
        Optional<Job> jobOpt = configManager.getJob(jobId);
        if (jobOpt.isEmpty()) {
            return false;
        }
        
        Job job = jobOpt.get();
        
        // Mark as inactive and save leave time
        jobInfo.setActive(false);
        jobInfo.setLastLeaveTime(System.currentTimeMillis());
        
        storageManager.markDirty(playerUuid);
        sendMessage(playerUuid, "§eLeft " + job.getDisplayName() + ". Your progress has been saved.");
        
        return true;
    }
    
    /**
     * Awards progress for a job action.
     * 
     * @param actionType Type of action (break, place, harvest, etc)
     * @param material Material involved (diamond_ore, wheat, stone, etc)
     */
    public void awardProgress(UUID playerUuid, String actionType, String material) {
        User user = storageManager.getUser(playerUuid);
        if (user == null) {
            user = storageManager.loadUser(playerUuid);
        }
        
        // Check each active job to see if this action counts
        for (Map.Entry<String, UserJobInfo> entry : user.getActiveJobs().entrySet()) {
            String jobId = entry.getKey();
            UserJobInfo jobInfo = entry.getValue();
            
            Optional<Job> jobOpt = configManager.getJob(jobId);
            if (jobOpt.isEmpty()) {
                continue;
            }
            
            Job job = jobOpt.get();
            
            // Check if this action is valid for this job
            if (!isValidAction(job, actionType, material)) {
                continue;
            }
            
            // Calculate progress amount
            BigDecimal baseProgress = BigDecimal.ONE;
            BigDecimal multiplier = BigDecimal.valueOf(job.getProgressMultiplier(material));
            BigDecimal progressAmount = baseProgress.multiply(multiplier);
            
            // Add progress
            jobInfo.addProgress(progressAmount);
            
            // Check for level up
            while (jobInfo.isComplete()) {
                levelUp(playerUuid, jobId, job, jobInfo);
            }
            
            storageManager.markDirty(playerUuid);
        }
    }
    
    /**
     * Checks if an action is valid for a job.
     */
    private boolean isValidAction(Job job, String actionType, String material) {
        // Check if the action type matches the job's action type
        if (!job.getActionType().equalsIgnoreCase(actionType)) {
            return false;
        }
        
        // If job has specialProgress defined, only allow those materials
        // This prevents Miner from progressing on non-ore blocks
        if (!job.getSpecialProgress().isEmpty()) {
            // Check exact match first
            if (job.getSpecialProgress().containsKey(material)) {
                return true;
            }
            
            // Check if material ends with valid suffix (for mod support)
            // e.g., "modname:copper_ore" matches if "_ore" is in specialProgress
            for (String validMaterial : job.getSpecialProgress().keySet()) {
                if (material.endsWith(validMaterial) || material.contains(":" + validMaterial)) {
                    return true;
                }
            }
            
            return false; // Material not in whitelist
        }
        
        // If no specialProgress, allow all materials for this action type
        return true;
    }
    
    /**
     * Levels up a player's job and grants rewards.
     */
    private void levelUp(UUID playerUuid, String jobId, Job job, UserJobInfo jobInfo) {
        int newLevel = jobInfo.getLevel() + 1;
        jobInfo.levelUp();
        
        // Calculate new required progress for next level
        BigDecimal newRequired = job.calculateRequiredProgress(newLevel);
        jobInfo.setRequiredProgress(newRequired);
        
        // Grant rewards
        List<String> rewardIds = job.getRewardsForLevel(newLevel);
        grantRewards(playerUuid, rewardIds, newLevel);
        
        // Send message with colored job name
        String jobName = job.getName() != null ? job.getName().replace("&", "§") : job.getId();
        sendMessage(playerUuid, "§6§l✦ §aLeveled up " + jobName + "§a to level " + newLevel + "! §6§l✦");
        
        LOGGER.info("Player {} leveled up {} to level {}", playerUuid, jobId, newLevel);
    }
    
    /**
     * Grants rewards to a player.
     */
    private void grantRewards(UUID playerUuid, List<String> rewardIds, int level) {
        LOGGER.info("Granting rewards {} to player {} at level {}", rewardIds, playerUuid, level);
        
        for (String rewardId : rewardIds) {
            Optional<Reward> rewardOpt = configManager.getReward(rewardId);
            if (rewardOpt.isEmpty()) {
                LOGGER.warn("Reward {} not found", rewardId);
                continue;
            }
            
            Reward reward = rewardOpt.get();
            LOGGER.info("Processing reward type: {}", reward.getType());
            
            if (reward.getType().equals("command")) {
                // Execute economy commands
                for (String command : reward.getCommands()) {
                    String processed = processCommand(command, reward, playerUuid, level);
                    LOGGER.info("Executing command: {} -> {}", command, processed);
                    executeEconomyCommand(playerUuid, processed);
                }
            } else if (reward.getType().equals("item")) {
                // Grant items (to be implemented)
                LOGGER.debug("Item rewards not yet implemented");
            }
        }
    }
    
    /**
     * Processes a command string with variables.
     */
    private String processCommand(String command, Reward reward, UUID playerUuid, int level) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
        String playerName = player != null ? player.getName().getString() : playerUuid.toString();
        
        // First replace %level% and %player% in command
        command = command
            .replace("%player%", playerName)
            .replace("%level%", String.valueOf(level));
        
        // Then evaluate and replace variables from reward
        if (reward.getVariables() != null) {
            for (Map.Entry<String, String> variable : reward.getVariables().entrySet()) {
                String varName = "%" + variable.getKey() + "%";
                String formula = variable.getValue();
                
                // Replace %level% in the formula
                formula = formula.replace("%level%", String.valueOf(level));
                
                // Evaluate the formula
                try {
                    double result = evaluateFormula(formula);
                    command = command.replace(varName, String.valueOf((int)result));
                } catch (Exception e) {
                    LOGGER.error("Failed to evaluate variable {} with formula {}", varName, formula, e);
                }
            }
        }
        
        return command;
    }
    
    /**
     * Simple formula evaluator with caching (supports basic math: +, -, *, /, ^, parentheses).
     * Caches results for frequently evaluated formulas.
     */
    private double evaluateFormula(String formula) {
        // Check cache first
        String cacheKey = formula;
        BigDecimal cached = formulaCache.get(cacheKey);
        if (cached != null) {
            return cached.doubleValue();
        }
        
        try {
            formula = formula.replaceAll("\\s+", "");
            
            // Handle parentheses first
            while (formula.contains("(")) {
                int start = formula.lastIndexOf('(');
                int end = formula.indexOf(')', start);
                if (end == -1) break;
                
                String subExpr = formula.substring(start + 1, end);
                double subResult = evaluateSimple(subExpr);
                formula = formula.substring(0, start) + subResult + formula.substring(end + 1);
            }
            
            double result = evaluateSimple(formula);
            
            // Cache the result (limit cache size to 1000 entries)
            if (formulaCache.size() < 1000) {
                formulaCache.put(cacheKey, BigDecimal.valueOf(result));
            }
            
            return result;
        } catch (Exception e) {
            LOGGER.error("Failed to evaluate formula: {}", formula, e);
            return 0.0;
        }
    }
    
    /**
     * Evaluate simple expression without parentheses.
     */
    private double evaluateSimple(String expr) {
        // Handle exponentiation (^)
        for (int i = 0; i < expr.length(); i++) {
            if (expr.charAt(i) == '^') {
                double left = evaluateSimple(expr.substring(0, i));
                double right = evaluateSimple(expr.substring(i + 1));
                return Math.pow(left, right);
            }
        }
        
        // Handle multiplication and division
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '*' || c == '/') {
                double left = evaluateSimple(expr.substring(0, i));
                double right = evaluateSimple(expr.substring(i + 1));
                return c == '*' ? left * right : left / right;
            }
        }
        
        // Handle addition and subtraction
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == '+' || (c == '-' && i > 0)) {
                double left = evaluateSimple(expr.substring(0, i));
                double right = evaluateSimple(expr.substring(i + 1));
                return c == '+' ? left + right : left - right;
            }
        }
        
        return Double.parseDouble(expr);
    }
    
    /**
     * Executes an economy command (deposit or withdraw).
     */
    private void executeEconomyCommand(UUID playerUuid, String command) {
        // Parse command: "deposit 100" or "withdraw 50"
        String[] parts = command.split(" ");
        if (parts.length < 2) {
            return;
        }
        
        String action = parts[0].toLowerCase();
        double amount;
        try {
            amount = Double.parseDouble(parts[1]);
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid amount in command: {}", command);
            return;
        }
        
        if (action.equals("deposit")) {
            economyHandler.depositMoney(playerUuid, amount).thenAccept(success -> {
                if (success) {
                    sendMessage(playerUuid, "§a+$" + amount);
                }
            });
        } else if (action.equals("withdraw")) {
            economyHandler.withdrawMoney(playerUuid, amount).thenAccept(success -> {
                if (success) {
                    sendMessage(playerUuid, "§c-$" + amount);
                } else {
                    sendMessage(playerUuid, "§cInsufficient funds!");
                }
            });
        }
    }
    
    /**
     * Gets a player's job info.
     */
    public UserJobInfo getJobInfo(UUID playerUuid, String jobId) {
        User user = storageManager.loadUser(playerUuid);
        return user.getJobInfo(jobId);
    }
    
    /**
     * Gets all of a player's jobs.
     */
    public Map<String, UserJobInfo> getAllJobs(UUID playerUuid) {
        User user = storageManager.loadUser(playerUuid);
        return user.getAllJobs();
    }
    
    /**
     * Sends a message to a player.
     */
    private void sendMessage(UUID playerUuid, String message) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
        if (player != null) {
            player.sendMessage(Text.literal(message), false);
        }
    }
}
