package net.toastie.jobs.objects;

import com.google.gson.annotations.SerializedName;
import net.toastie.jobs.config.ConfigManager;
import net.toastie.jobs.util.BlockRegistryScanner;
import net.toastie.jobs.util.FormulaEvaluator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a job definition with all its properties
 */
public class Job {
    private final String id;
    private final String name;
    private final String actionType;
    
    @SerializedName("requiredProgressFormula")
    private final String progressFormula;
    
    @SerializedName("pointsRewardedFormula")
    private final String pointsFormula;
    
    private final Map<String, Double> specialProgress;
    private final List<String> defaultRewards;
    private final Map<Integer, List<String>> levelRewards;
    private final boolean bothRewards;
    private final double requiredPoints;
    private final String requiredPermission;
    private final GuiItemConfig guiItem;
    private final Set<Integer> notifyAtPercentages;
    
    // Shared formula evaluator for all jobs
    private static final FormulaEvaluator formulaEvaluator = new FormulaEvaluator();
    
    public Job(String id, String name, String actionType, String progressFormula, 
               String pointsFormula, Map<String, Double> specialProgress,
               List<String> defaultRewards, Map<Integer, List<String>> levelRewards,
               boolean bothRewards, double requiredPoints, String requiredPermission,
               GuiItemConfig guiItem, Set<Integer> notifyAtPercentages) {
        this.id = id;
        this.name = name;
        this.actionType = actionType;
        this.progressFormula = progressFormula;
        this.pointsFormula = pointsFormula;
        this.specialProgress = specialProgress != null ? specialProgress : new HashMap<>();
        this.defaultRewards = defaultRewards != null ? defaultRewards : new ArrayList<>();
        this.levelRewards = levelRewards != null ? levelRewards : new HashMap<>();
        this.bothRewards = bothRewards;
        this.requiredPoints = requiredPoints;
        this.requiredPermission = requiredPermission;
        this.guiItem = guiItem;
        this.notifyAtPercentages = notifyAtPercentages != null ? notifyAtPercentages : Set.of(10, 25, 50, 75, 100);
    }
    
    // Progress Calculation
    public BigDecimal calculateRequiredProgress(int level) {
        try {
            String formula = progressFormula.replace("%level%", String.valueOf(level));
            double result = formulaEvaluator.evaluate(formula);
            return BigDecimal.valueOf(result);
        } catch (Exception e) {
            return BigDecimal.valueOf(100 * (level * level)); // Fallback: 100 * level^2
        }
    }
    
    // Points Calculation
    public double calculatePoints(int level) {
        try {
            String formula = pointsFormula.replace("%level%", String.valueOf(level));
            return formulaEvaluator.evaluate(formula);
        } catch (Exception e) {
            return level; // Fallback: 1 point per level
        }
    }
    
    // Get Progress Multiplier for Material
    public double getProgressMultiplier(String material) {
        return specialProgress.getOrDefault(material.toUpperCase(), 1.0);
    }
    
    // Apply auto-detection based on job type
    public void applyAutoDetection(ConfigManager config) {
        double defaultProgress = 1.0;
        Map<String, Double> autoDetected = new HashMap<>();
        
        switch (actionType.toLowerCase()) {
            case "break":
                // Assume this is a miner job - scan for ores
                defaultProgress = config.getDouble("autoDetection.defaultOreProgress");
                autoDetected = BlockRegistryScanner.scanOres(defaultProgress);
                break;
                
            case "harvest":
                // Assume this is a farmer job - scan for crops
                defaultProgress = config.getDouble("autoDetection.defaultCropProgress");
                autoDetected = BlockRegistryScanner.scanCrops(defaultProgress);
                break;
                
            case "place":
                // Builder job - accept ALL placeable blocks without validation
                // Leave specialProgress empty so isValidAction() returns true for any block
                break;
        }
        
        // Merge auto-detected with manual config (manual overrides auto)
        for (Map.Entry<String, Double> entry : autoDetected.entrySet()) {
            String key = entry.getKey().toUpperCase();
            if (!specialProgress.containsKey(key)) {
                specialProgress.put(key, entry.getValue());
            }
        }
    }
    
    // Get Rewards for Level
    public List<String> getRewardsForLevel(int level) {
        List<String> rewards = new ArrayList<>();
        
        // Add default rewards
        if (bothRewards || !levelRewards.containsKey(level)) {
            rewards.addAll(defaultRewards);
        }
        
        // Add level-specific rewards
        if (levelRewards.containsKey(level)) {
            rewards.addAll(levelRewards.get(level));
        }
        
        return rewards;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDisplayName() { return name; }
    public String getActionType() { return actionType; }
    public String getProgressFormula() { return progressFormula; }
    public String getPointsFormula() { return pointsFormula; }
    public Map<String, Double> getSpecialProgress() { return specialProgress; }
    public List<String> getDefaultRewards() { return defaultRewards; }
    public Map<Integer, List<String>> getLevelRewards() { return levelRewards; }
    public boolean isBothRewards() { return bothRewards; }
    public double getRequiredPoints() { return requiredPoints; }
    public String getRequiredPermission() { return requiredPermission; }
    public GuiItemConfig getGuiItem() { return guiItem; }
    public Set<Integer> getNotifyAtPercentages() { return notifyAtPercentages; }
}
