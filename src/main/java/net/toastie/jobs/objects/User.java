package net.toastie.jobs.objects;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a player's job data and progress
 */
public class User {
    private final UUID uuid;
    private final Map<String, UserJobInfo> activeJobs;
    private double points;
    
    public User(UUID uuid) {
        this(uuid, new ConcurrentHashMap<>(), 0.0);
    }
    
    public User(UUID uuid, Map<String, UserJobInfo> activeJobs, double points) {
        this.uuid = uuid;
        this.activeJobs = new ConcurrentHashMap<>(activeJobs);
        this.points = points;
    }
    
    // Job Management
    public boolean hasJob(String jobId) {
        return activeJobs.containsKey(jobId);
    }
    
    public boolean hasActiveJob(String jobId) {
        UserJobInfo info = activeJobs.get(jobId);
        return info != null && info.isActive();
    }
    
    public UserJobInfo getJob(String jobId) {
        return activeJobs.get(jobId);
    }
    
    public void addJob(UserJobInfo jobInfo) {
        activeJobs.put(jobInfo.getJobId(), jobInfo);
    }
    
    public void removeJob(String jobId) {
        activeJobs.remove(jobId);
    }
    
    public int getActiveJobCount() {
        return (int) activeJobs.values().stream()
            .filter(UserJobInfo::isActive)
            .count();
    }
    
    public Map<String, UserJobInfo> getAllJobs() {
        return new ConcurrentHashMap<>(activeJobs);
    }
    
    public Map<String, UserJobInfo> getActiveJobs() {
        Map<String, UserJobInfo> active = new ConcurrentHashMap<>();
        activeJobs.forEach((id, info) -> {
            if (info.isActive()) {
                active.put(id, info);
            }
        });
        return active;
    }
    
    // Points Management
    public void addPoints(double amount) {
        this.points += amount;
    }
    
    public void removePoints(double amount) {
        this.points = Math.max(0, this.points - amount);
    }
    
    public void setPoints(double points) {
        this.points = Math.max(0, points);
    }
    
    public void clearPoints() {
        this.points = 0.0;
    }
    
    // Getters
    public UUID getUuid() {
        return uuid;
    }
    
    public double getPoints() {
        return points;
    }
    
    public int getJobLevel(String jobId) {
        UserJobInfo info = activeJobs.get(jobId);
        return info != null ? info.getLevel() : 1;
    }
    
    public UserJobInfo getJobInfo(String jobId) {
        return activeJobs.get(jobId);
    }
    
    public void setJobInfo(String jobId, UserJobInfo jobInfo) {
        activeJobs.put(jobId, jobInfo);
    }
    
    public void removeJobInfo(String jobId) {
        activeJobs.remove(jobId);
    }
    
    public void clearAllJobs() {
        activeJobs.clear();
    }
}
