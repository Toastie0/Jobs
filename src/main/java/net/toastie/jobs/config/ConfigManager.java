package net.toastie.jobs.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.toastie.jobs.objects.Job;
import net.toastie.jobs.objects.Reward;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Manages loading and access to all JSON configuration files.
 * Loads jobs, rewards, and main settings from the config directory.
 */
public class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private final Path configDir;
    private final Path jobsDir;
    private final Path rewardsFile;
    private final Path settingsFile;
    
    private final Map<String, Job> jobs = new HashMap<>();
    private final Map<String, List<Reward>> rewards = new HashMap<>();
    private JsonObject settings;
    
    public ConfigManager() {
        this.configDir = FabricLoader.getInstance().getConfigDir().resolve("jobs");
        this.jobsDir = configDir.resolve("jobs");
        this.rewardsFile = configDir.resolve("rewards.json");
        this.settingsFile = configDir.resolve("config.json");
    }
    
    /**
     * Loads all configuration files. Creates default files if they don't exist.
     */
    public void loadConfigs() {
        try {
            createDirectories();
            loadSettings();
            loadRewards();
            loadJobs();
            
            LOGGER.info("Loaded {} jobs and {} reward categories", jobs.size(), rewards.size());
        } catch (Exception e) {
            LOGGER.error("Failed to load configurations", e);
        }
    }
    
    private void createDirectories() throws Exception {
        Files.createDirectories(configDir);
        Files.createDirectories(jobsDir);
    }
    
    private void loadSettings() throws Exception {
        if (!Files.exists(settingsFile)) {
            createDefaultSettings();
        }
        
        try (FileReader reader = new FileReader(settingsFile.toFile())) {
            settings = JsonParser.parseReader(reader).getAsJsonObject();
            LOGGER.info("Loaded main settings");
        }
    }
    
    private void createDefaultSettings() throws Exception {
        JsonObject defaultSettings = new JsonObject();
        
        // Job settings
        JsonObject jobSettings = new JsonObject();
        jobSettings.addProperty("maxActiveJobs", 3);
        jobSettings.addProperty("leaveCooldownMinutes", 15);
        jobSettings.addProperty("saveProgressOnLeave", true);
        jobSettings.addProperty("allowRejoinWithProgress", true);
        defaultSettings.add("jobs", jobSettings);
        
        // Storage settings
        JsonObject storageSettings = new JsonObject();
        storageSettings.addProperty("autoSaveIntervalSeconds", 180);
        storageSettings.addProperty("saveOnQuit", true);
        defaultSettings.add("storage", storageSettings);
        
        // GUI settings
        JsonObject guiSettings = new JsonObject();
        guiSettings.addProperty("refreshIntervalTicks", 20);
        guiSettings.addProperty("enableSounds", true);
        defaultSettings.add("gui", guiSettings);
        
        // Anti-exploit settings
        JsonObject antiExploitSettings = new JsonObject();
        antiExploitSettings.addProperty("trackPlacedBlocks", true);
        antiExploitSettings.addProperty("trackBrewingTime", true);
        antiExploitSettings.addProperty("minBrewingTimeSeconds", 20);
        defaultSettings.add("antiExploit", antiExploitSettings);
        
        try (FileWriter writer = new FileWriter(settingsFile.toFile())) {
            GSON.toJson(defaultSettings, writer);
            LOGGER.info("Created default settings file");
        }
    }
    
    private void loadRewards() throws Exception {
        if (!Files.exists(rewardsFile)) {
            createDefaultRewards();
        }
        
        try (FileReader reader = new FileReader(rewardsFile.toFile())) {
            JsonObject rewardsJson = JsonParser.parseReader(reader).getAsJsonObject();
            
            for (String key : rewardsJson.keySet()) {
                List<Reward> rewardList = GSON.fromJson(
                    rewardsJson.get(key), 
                    com.google.gson.reflect.TypeToken.getParameterized(List.class, Reward.class).getType()
                );
                rewards.put(key, rewardList);
            }
            
            LOGGER.info("Loaded rewards for {} categories", rewards.size());
        }
    }
    
    private void createDefaultRewards() throws Exception {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("config/jobs/rewards.json")) {
            if (inputStream == null) {
                LOGGER.error("Could not find default rewards.json in resources");
                // Create empty structure as fallback
                JsonObject defaultRewards = new JsonObject();
                defaultRewards.add("default", GSON.toJsonTree(List.of()));
                try (FileWriter writer = new FileWriter(rewardsFile.toFile())) {
                    GSON.toJson(defaultRewards, writer);
                }
                return;
            }
            
            Files.copy(inputStream, rewardsFile);
            LOGGER.info("Created default rewards file from resources");
        }
    }
    
    private void loadJobs() throws Exception {
        File[] jobFiles = jobsDir.toFile().listFiles((dir, name) -> name.endsWith(".json"));
        
        if (jobFiles == null || jobFiles.length == 0) {
            LOGGER.warn("No job files found in {}", jobsDir);
            LOGGER.info("Creating default job files...");
            createDefaultJobFiles();
            // Reload after creating defaults
            jobFiles = jobsDir.toFile().listFiles((dir, name) -> name.endsWith(".json"));
        }
        
        if (jobFiles == null || jobFiles.length == 0) {
            LOGGER.error("Failed to create default job files!");
            return;
        }
        
        for (File jobFile : jobFiles) {
            try (FileReader reader = new FileReader(jobFile)) {
                // Read raw JSON first for debugging
                JsonObject jobJson = JsonParser.parseReader(reader).getAsJsonObject();
                LOGGER.info("Raw JSON for {}: id={}, name={}, requiredProgressFormula={}", 
                    jobFile.getName(), 
                    jobJson.get("id"), 
                    jobJson.get("name"),
                    jobJson.get("requiredProgressFormula"));
                
                Job job = GSON.fromJson(jobJson, Job.class);
                if (job == null) {
                    LOGGER.error("Failed to parse job file: {} - returned null", jobFile.getName());
                    continue;
                }
                if (job.getId() == null) {
                    LOGGER.error("Job file {} has null ID", jobFile.getName());
                    continue;
                }
                jobs.put(job.getId(), job);
                LOGGER.info("Loaded job: {} (name: {}, progressFormula: {})", job.getId(), job.getName(), job.getProgressFormula());
            } catch (Exception e) {
                LOGGER.error("Failed to load job file: {}", jobFile.getName(), e);
            }
        }
    }
    
    private void createDefaultJobFiles() throws Exception {
        // Copy default job files from JAR resources to config directory
        String[] defaultJobs = {"miner.json", "farmer.json", "builder.json"};
        
        for (String jobFileName : defaultJobs) {
            Path targetPath = jobsDir.resolve(jobFileName);
            if (Files.exists(targetPath)) {
                continue; // Skip if already exists
            }
            
            try (var inputStream = getClass().getClassLoader().getResourceAsStream("config/jobs/jobs/" + jobFileName)) {
                if (inputStream == null) {
                    LOGGER.error("Could not find default job file: {}", jobFileName);
                    continue;
                }
                
                Files.copy(inputStream, targetPath);
                LOGGER.info("Created default job file: {}", jobFileName);
            } catch (Exception e) {
                LOGGER.error("Failed to create default job file: {}", jobFileName, e);
            }
        }
    }
    
    /**
     * Gets a job by its ID.
     */
    public Optional<Job> getJob(String jobId) {
        return Optional.ofNullable(jobs.get(jobId));
    }
    
    /**
     * Gets all loaded jobs.
     */
    public Collection<Job> getAllJobs() {
        return Collections.unmodifiableCollection(jobs.values());
    }
    
    /**
     * Gets rewards for a specific job level.
     */
    public List<Reward> getRewards(String rewardId) {
        return rewards.getOrDefault(rewardId, Collections.emptyList());
    }
    
    /**
     * Gets a single reward by ID from the rewards map.
     */
    public Optional<Reward> getReward(String rewardId) {
        List<Reward> rewardList = rewards.get(rewardId);
        if (rewardList != null && !rewardList.isEmpty()) {
            return Optional.of(rewardList.get(0));
        }
        return Optional.empty();
    }
    
    /**
     * Gets a setting value as integer.
     */
    public int getInt(String path) {
        return getSettingValue(path).getAsInt();
    }
    
    /**
     * Gets a setting value as boolean.
     */
    public boolean getBoolean(String path) {
        return getSettingValue(path).getAsBoolean();
    }
    
    /**
     * Gets a setting value as string.
     */
    public String getString(String path) {
        return getSettingValue(path).getAsString();
    }
    
    private com.google.gson.JsonElement getSettingValue(String path) {
        String[] parts = path.split("\\.");
        JsonObject current = settings;
        
        for (int i = 0; i < parts.length - 1; i++) {
            current = current.getAsJsonObject(parts[i]);
        }
        
        return current.get(parts[parts.length - 1]);
    }
    
    /**
     * Reloads all configuration files.
     */
    public void reload() {
        jobs.clear();
        rewards.clear();
        loadConfigs();
    }
}
