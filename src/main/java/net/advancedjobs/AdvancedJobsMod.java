package net.advancedjobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.advancedjobs.commands.JobsAdminCommand;
import net.advancedjobs.commands.JobsCommand;
import net.advancedjobs.config.ConfigManager;
import net.advancedjobs.controller.JobController;
import net.advancedjobs.economy.ImpactorEconomyHandler;
import net.advancedjobs.handlers.BlockBreakHandler;
import net.advancedjobs.handlers.BlockPlaceHandler;
import net.advancedjobs.handlers.CropHarvestHandler;
import net.advancedjobs.storage.JsonStorageManager;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;

/**
 * Main mod class for Jobs Fabric mod.
 * Initializes all systems and registers event handlers.
 */
public class AdvancedJobsMod implements DedicatedServerModInitializer {
    public static final String MOD_ID = "advancedjobs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static AdvancedJobsMod instance;
    
    private ConfigManager configManager;
    private JsonStorageManager storageManager;
    private ImpactorEconomyHandler economyHandler;
    private JobController jobController;
    
    private BlockBreakHandler blockBreakHandler;
    private CropHarvestHandler cropHarvestHandler;
    private BlockPlaceHandler blockPlaceHandler;
    
    private int tickCounter = 0;
    private int autoSaveInterval;
    
    public static AdvancedJobsMod getInstance() {
        return instance;
    }
    
    public JobController getJobController() {
        return jobController;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public JsonStorageManager getStorageManager() {
        return storageManager;
    }
    
    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing Jobs mod...");
        
        instance = this;
        
        // Initialize configuration
        configManager = new ConfigManager();
        configManager.loadConfigs();
        
        // Initialize storage
        storageManager = new JsonStorageManager();
        storageManager.initialize();
        
        // Initialize economy handler
        economyHandler = new ImpactorEconomyHandler();
        
        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            new JobsCommand().register(dispatcher, registryAccess, environment);
            new JobsAdminCommand().register(dispatcher, registryAccess, environment);
            LOGGER.info("Registered /jobs and /jobsadmin commands");
        });
        
        // Register server lifecycle events
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        
        // Register player events
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            storageManager.loadUser(handler.player.getUuid());
        });
        
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            storageManager.unloadUser(handler.player.getUuid());
        });
        
        // Register tick event for auto-save
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        
        LOGGER.info("Jobs mod initialized!");
    }
    
    private void onServerStarting(MinecraftServer server) {
        LOGGER.info("Server starting, initializing job controller...");
        
        // Initialize job controller (will be used by already-registered commands)
        jobController = new JobController(configManager, storageManager, economyHandler, server);
        LOGGER.info("Job controller initialized successfully");
    }
    
    private void onServerStarted(MinecraftServer server) {
        LOGGER.info("Server started, finalizing Jobs systems...");
        
        // Initialize economy
        economyHandler.initialize();
        
        if (!economyHandler.isAvailable()) {
            LOGGER.warn("Impactor economy not available! Make sure Impactor is installed.");
        }
        
        // Initialize and register event handlers
        blockBreakHandler = new BlockBreakHandler(jobController);
        blockBreakHandler.setTrackPlacedBlocks(configManager.getBoolean("antiExploit.trackPlacedBlocks"));
        blockBreakHandler.register();
        
        cropHarvestHandler = new CropHarvestHandler(jobController);
        cropHarvestHandler.register();
        
        blockPlaceHandler = new BlockPlaceHandler(jobController, blockBreakHandler);
        blockPlaceHandler.register();
        
        // Get auto-save interval
        autoSaveInterval = configManager.getInt("storage.autoSaveIntervalSeconds") * 20; // Convert to ticks
        
        LOGGER.info("Jobs fully initialized and ready!");
    }
    
    private void onServerTick(MinecraftServer server) {
        tickCounter++;
        
        // Auto-save at configured interval
        if (tickCounter >= autoSaveInterval) {
            storageManager.saveAllDirty();
            tickCounter = 0;
        }
        
        // Clear old block tracking entries every 5 minutes
        if (tickCounter % 6000 == 0) {
            blockBreakHandler.clearOldEntries();
            cropHarvestHandler.clearExpiredBonemealEntries();
        }
    }
    
    private void onServerStopping(MinecraftServer server) {
        LOGGER.info("Server stopping, saving all data...");
        
        if (storageManager != null) {
            storageManager.saveAll();
        }
        
        LOGGER.info("Jobs shutdown complete.");
    }
}
