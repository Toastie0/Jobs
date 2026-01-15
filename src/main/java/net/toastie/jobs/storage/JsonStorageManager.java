package net.toastie.jobs.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.toastie.jobs.objects.User;
import net.toastie.jobs.objects.UserJobInfo;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages JSON storage for player data.
 * Auto-saves player data at configured intervals.
 */
public class JsonStorageManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonStorageManager.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private final Path storageDir;
    private final Map<UUID, User> userCache = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> dirtyUsers = new ConcurrentHashMap<>();
    
    public JsonStorageManager() {
        this.storageDir = FabricLoader.getInstance().getConfigDir()
            .resolve("jobs")
            .resolve("users");
    }
    
    /**
     * Initializes the storage system.
     */
    public void initialize() {
        try {
            Files.createDirectories(storageDir);
            LOGGER.info("Initialized storage directory at {}", storageDir);
        } catch (Exception e) {
            LOGGER.error("Failed to initialize storage directory", e);
        }
    }
    
    /**
     * Loads a user's data from disk or creates new if not exists.
     */
    public User loadUser(UUID uuid) {
        if (userCache.containsKey(uuid)) {
            return userCache.get(uuid);
        }
        
        Path userFile = getUserFile(uuid);
        User user;
        
        if (Files.exists(userFile)) {
            user = loadUserFromFile(userFile, uuid);
        } else {
            user = new User(uuid);
            LOGGER.debug("Created new user data for {}", uuid);
        }
        
        userCache.put(uuid, user);
        return user;
    }
    
    private User loadUserFromFile(Path file, UUID uuid) {
        try (FileReader reader = new FileReader(file.toFile())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            
            User user = new User(uuid);
            
            // Load points
            if (json.has("points")) {
                user.setPoints(json.get("points").getAsInt());
            }
            
            // Load jobs
            if (json.has("jobs")) {
                JsonObject jobsJson = json.getAsJsonObject("jobs");
                for (String jobId : jobsJson.keySet()) {
                    JsonObject jobData = jobsJson.getAsJsonObject(jobId);
                    
                    UserJobInfo jobInfo = new UserJobInfo(jobId);
                    jobInfo.setLevel(jobData.get("level").getAsInt());
                    jobInfo.setProgress(jobData.get("progress").getAsBigDecimal());
                    jobInfo.setRequiredProgress(jobData.get("requiredProgress").getAsBigDecimal());
                    jobInfo.setActive(jobData.get("active").getAsBoolean());
                    
                    if (jobData.has("lastLeaveTime")) {
                        jobInfo.setLastLeaveTime(jobData.get("lastLeaveTime").getAsLong());
                    }
                    
                    user.setJobInfo(jobId, jobInfo);
                }
            }
            
            LOGGER.debug("Loaded user data for {} with {} jobs", uuid, user.getAllJobs().size());
            return user;
            
        } catch (Exception e) {
            LOGGER.error("Failed to load user file: {}", file, e);
            return new User(uuid);
        }
    }
    
    /**
     * Saves a user's data to disk.
     */
    public void saveUser(UUID uuid) {
        User user = userCache.get(uuid);
        if (user == null) {
            return;
        }
        
        Path userFile = getUserFile(uuid);
        
        try {
            JsonObject json = new JsonObject();
            json.addProperty("uuid", uuid.toString());
            json.addProperty("points", user.getPoints());
            
            // Save jobs
            JsonObject jobsJson = new JsonObject();
            for (Map.Entry<String, UserJobInfo> entry : user.getAllJobs().entrySet()) {
                UserJobInfo jobInfo = entry.getValue();
                JsonObject jobData = new JsonObject();
                
                jobData.addProperty("level", jobInfo.getLevel());
                jobData.addProperty("progress", jobInfo.getProgress());
                jobData.addProperty("requiredProgress", jobInfo.getRequiredProgress());
                jobData.addProperty("active", jobInfo.isActive());
                
                if (jobInfo.getLastLeaveTime() > 0) {
                    jobData.addProperty("lastLeaveTime", jobInfo.getLastLeaveTime());
                }
                
                jobsJson.add(entry.getKey(), jobData);
            }
            json.add("jobs", jobsJson);
            
            // Write to file
            try (FileWriter writer = new FileWriter(userFile.toFile())) {
                GSON.toJson(json, writer);
            }
            
            dirtyUsers.remove(uuid);
            LOGGER.debug("Saved user data for {}", uuid);
            
        } catch (Exception e) {
            LOGGER.error("Failed to save user file: {}", userFile, e);
        }
    }
    
    /**
     * Marks a user as needing to be saved.
     */
    public void markDirty(UUID uuid) {
        dirtyUsers.put(uuid, true);
    }
    
    /**
     * Saves all dirty users (modified since last save).
     */
    public void saveAllDirty() {
        int saved = 0;
        for (UUID uuid : dirtyUsers.keySet()) {
            saveUser(uuid);
            saved++;
        }
        
        if (saved > 0) {
            LOGGER.info("Auto-saved {} player data files", saved);
        }
    }
    
    /**
     * Saves all cached users.
     */
    public void saveAll() {
        int saved = 0;
        for (UUID uuid : userCache.keySet()) {
            saveUser(uuid);
            saved++;
        }
        
        if (saved > 0) {
            LOGGER.info("Saved {} player data files", saved);
        }
    }
    
    /**
     * Removes a user from cache (call on player quit).
     */
    public void unloadUser(UUID uuid) {
        saveUser(uuid);
        userCache.remove(uuid);
        dirtyUsers.remove(uuid);
    }
    
    /**
     * Gets a user from cache (must be loaded first).
     */
    public User getUser(UUID uuid) {
        return userCache.get(uuid);
    }
    
    private Path getUserFile(UUID uuid) {
        return storageDir.resolve(uuid.toString() + ".json");
    }
}
