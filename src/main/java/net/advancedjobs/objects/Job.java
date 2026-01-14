package net.advancedjobs.objects;

import com.google.gson.annotations.SerializedName;

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
            double result = evaluateFormula(formula);
            return BigDecimal.valueOf(result);
        } catch (Exception e) {
            return BigDecimal.valueOf(2 * (level * level)); // Fallback: 2 * level^2
        }
    }
    
    // Points Calculation
    public double calculatePoints(int level) {
        try {
            String formula = pointsFormula.replace("%level%", String.valueOf(level));
            return evaluateFormula(formula);
        } catch (Exception e) {
            return level; // Fallback: 1 point per level
        }
    }
    
    // Get Progress Multiplier for Material
    public double getProgressMultiplier(String material) {
        return specialProgress.getOrDefault(material.toUpperCase(), 1.0);
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
    
    // Simple formula evaluator (supports basic math)
    private double evaluateFormula(String formula) {
        try {
            // Remove spaces
            formula = formula.replaceAll("\\s+", "");
            
            // Handle parentheses first
            while (formula.contains("(")) {
                int start = formula.lastIndexOf('(');
                int end = formula.indexOf(')', start);
                if (end == -1) break;
                
                String sub = formula.substring(start + 1, end);
                double result = evaluateSimple(sub);
                formula = formula.substring(0, start) + result + formula.substring(end + 1);
            }
            
            return evaluateSimple(formula);
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private double evaluateSimple(String expr) {
        // Handle exponentiation
        if (expr.contains("^")) {
            String[] parts = expr.split("\\^", 2);
            double base = evaluateSimple(parts[0]);
            double exp = evaluateSimple(parts[1]);
            return Math.pow(base, exp);
        }
        
        // Handle multiplication and division
        if (expr.contains("*") || expr.contains("/")) {
            for (int i = 0; i < expr.length(); i++) {
                char c = expr.charAt(i);
                if (c == '*' || c == '/') {
                    double left = evaluateSimple(expr.substring(0, i));
                    double right = evaluateSimple(expr.substring(i + 1));
                    return c == '*' ? left * right : left / right;
                }
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
        
        // Parse number
        return Double.parseDouble(expr);
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
