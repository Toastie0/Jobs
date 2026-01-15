package net.toastie.jobs.controller;

import net.toastie.jobs.config.ConfigManager;
import net.toastie.jobs.economy.ImpactorEconomyHandler;
import net.toastie.jobs.objects.Job;
import net.toastie.jobs.objects.Reward;
import net.toastie.jobs.objects.User;
import net.toastie.jobs.objects.UserJobInfo;
import net.toastie.jobs.storage.JsonStorageManager;
import net.toastie.jobs.util.FormulaEvaluator;
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
    
    // Formula evaluator for variable processing
    private final FormulaEvaluator formulaEvaluator = new FormulaEvaluator();
    
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
        
        // Check if economy is available
        if (!economyHandler.isAvailable()) {
            LOGGER.error("Cannot grant rewards - Impactor economy is not available!");
            sendMessage(playerUuid, "§c⚠ Economy system unavailable - rewards cannot be granted!");
            sendMessage(playerUuid, "§cPlease contact an administrator.");
            return;
        }
        
        for (String rewardId : rewardIds) {
            Optional<Reward> rewardOpt = configManager.getReward(rewardId);
            if (rewardOpt.isEmpty()) {
                LOGGER.warn("Reward {} not found in rewards.json", rewardId);
                continue;
            }
            
            Reward reward = rewardOpt.get();
            LOGGER.info("Processing reward type: {} for player {}", reward.getType(), playerUuid);
            
            if (reward.getType().equals("command")) {
                // Execute economy commands
                for (String command : reward.getCommands()) {
                    String processed = processCommand(command, reward, playerUuid, level);
                    LOGGER.info("Executing economy command: '{}' -> '{}'", command, processed);
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
                    double result = formulaEvaluator.evaluate(formula);
                    int intResult = (int) result;
                    LOGGER.debug("Evaluated variable {} with formula '{}' = {}", varName, formula, intResult);
                    command = command.replace(varName, String.valueOf(intResult));
                } catch (Exception e) {
                    LOGGER.error("Failed to evaluate variable {} with formula '{}' - Error: {}", varName, formula, e.getMessage(), e);
                    // Use a default value of 0 if formula fails
                    command = command.replace(varName, "0");
                }
            }
        }
        
        return command;
    }
    
    /**
     * Executes an economy command (deposit or withdraw).
     */
    private void executeEconomyCommand(UUID playerUuid, String command) {
        // Parse command: "deposit 100" or "withdraw 50"
        String[] parts = command.split(" ");
        if (parts.length < 2) {
            LOGGER.error("Invalid economy command format: '{}' (expected 'action amount')", command);
            return;
        }
        
        String action = parts[0].toLowerCase();
        double amount;
        try {
            amount = Double.parseDouble(parts[1]);
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid amount in command '{}': '{}' is not a valid number", command, parts[1]);
            return;
        }
        
        if (amount <= 0) {
            LOGGER.error("Invalid amount in command '{}': amount must be positive", command);
            return;
        }
        
        LOGGER.info("Executing economy {} of ${} for player {}", action, amount, playerUuid);
        
        if (action.equals("deposit")) {
            economyHandler.depositMoney(playerUuid, amount).thenAccept(success -> {
                if (success) {
                    sendMessage(playerUuid, "§a+$" + String.format("%.2f", amount));
                    LOGGER.info("Successfully deposited ${} to player {}", amount, playerUuid);
                } else {
                    sendMessage(playerUuid, "§cFailed to receive reward! Contact an admin.");
                    LOGGER.error("Failed to deposit ${} to player {}", amount, playerUuid);
                }
            });
        } else if (action.equals("withdraw")) {
            economyHandler.withdrawMoney(playerUuid, amount).thenAccept(success -> {
                if (success) {
                    sendMessage(playerUuid, "§c-$" + String.format("%.2f", amount));
                    LOGGER.info("Successfully withdrew ${} from player {}", amount, playerUuid);
                } else {
                    sendMessage(playerUuid, "§cInsufficient funds!");
                    LOGGER.warn("Failed to withdraw ${} from player {} - insufficient funds", amount, playerUuid);
                }
            });
        } else {
            LOGGER.error("Unknown economy action '{}' in command '{}'", action, command);
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
