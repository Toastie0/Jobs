package net.advancedjobs.objects;

import java.math.BigDecimal;

/**
 * Represents a player's progress in a specific job
 */
public class UserJobInfo {
    private final String jobId;
    private int level;
    private BigDecimal progress;
    private BigDecimal requiredProgress;
    private boolean active;
    private long lastLeaveTime;
    
    public UserJobInfo(String jobId) {
        this(jobId, 1, BigDecimal.ZERO, BigDecimal.valueOf(2), true);
    }
    
    public UserJobInfo(String jobId, int level, BigDecimal progress, BigDecimal requiredProgress, boolean active) {
        this.jobId = jobId;
        this.level = level;
        this.progress = progress;
        this.requiredProgress = requiredProgress;
        this.active = active;
    }
    
    // Progress Management
    public void addProgress(BigDecimal amount) {
        this.progress = this.progress.add(amount);
    }
    
    public void addProgress(double amount) {
        addProgress(BigDecimal.valueOf(amount));
    }
    
    public void resetProgress() {
        this.progress = BigDecimal.ZERO;
    }
    
    public boolean isComplete() {
        return progress.compareTo(requiredProgress) >= 0;
    }
    
    public double getProgressPercent() {
        if (requiredProgress.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return progress.divide(requiredProgress, 4, java.math.RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .doubleValue();
    }
    
    // Level Management
    public void levelUp() {
        this.level++;
        resetProgress();
    }
    
    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }
    
    // Getters and Setters
    public String getJobId() {
        return jobId;
    }
    
    public int getLevel() {
        return level;
    }
    
    public BigDecimal getProgress() {
        return progress;
    }
    
    public void setProgress(BigDecimal progress) {
        this.progress = progress;
    }
    
    public BigDecimal getRequiredProgress() {
        return requiredProgress;
    }
    
    public void setRequiredProgress(BigDecimal requiredProgress) {
        this.requiredProgress = requiredProgress;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public long getLastLeaveTime() {
        return lastLeaveTime;
    }
    
    public void setLastLeaveTime(long lastLeaveTime) {
        this.lastLeaveTime = lastLeaveTime;
    }
    
    @Override
    public String toString() {
        return String.format("Job[%s] Level=%d Progress=%s/%s Active=%s", 
            jobId, level, progress, requiredProgress, active);
    }
}
