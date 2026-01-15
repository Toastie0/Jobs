package net.toastie.jobs.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.toastie.jobs.JobsFabricMod;
import net.toastie.jobs.config.ConfigManager;
import net.toastie.jobs.controller.JobController;
import net.toastie.jobs.storage.JsonStorageManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * /jobsadmin command - Administrative commands for managing jobs.
 */
public class JobsAdminCommand {
    
    public JobsAdminCommand() {
    }
    
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, 
                        CommandRegistryAccess registryAccess,
                        CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("jobsadmin")
            .requires(source -> source.hasPermissionLevel(2))
            
            // /jobsadmin reload
            .then(CommandManager.literal("reload")
                .executes(this::reloadConfigs))
            
            // /jobsadmin save
            .then(CommandManager.literal("save")
                .executes(this::saveAll))
            
            // /jobsadmin reset <player> <job>
            .then(CommandManager.literal("reset")
                .then(CommandManager.argument("player", EntityArgumentType.player())
                    .then(CommandManager.argument("job", StringArgumentType.word())
                        .executes(this::resetJob))))
            
            // /jobsadmin resetall <player>
            .then(CommandManager.literal("resetall")
                .then(CommandManager.argument("player", EntityArgumentType.player())
                    .executes(this::resetAllJobs)))
            
            // /jobsadmin info <player>
            .then(CommandManager.literal("info")
                .then(CommandManager.argument("player", EntityArgumentType.player())
                    .executes(this::showPlayerInfo)))
        );
    }
    
    private int reloadConfigs(CommandContext<ServerCommandSource> context) {
        ConfigManager configManager = JobsFabricMod.getInstance().getConfigManager();
        
        context.getSource().sendFeedback(
            () -> Text.literal("§eReloading configurations..."), 
            true
        );
        
        configManager.reload();
        
        context.getSource().sendFeedback(
            () -> Text.literal("§aConfigurations reloaded successfully!"), 
            true
        );
        
        return 1;
    }
    
    private int saveAll(CommandContext<ServerCommandSource> context) {
        JsonStorageManager storageManager = JobsFabricMod.getInstance().getStorageManager();
        
        context.getSource().sendFeedback(
            () -> Text.literal("§eSaving all player data..."), 
            true
        );
        
        storageManager.saveAll();
        
        context.getSource().sendFeedback(
            () -> Text.literal("§aAll player data saved successfully!"), 
            true
        );
        
        return 1;
    }
    
    private int showPlayerInfo(CommandContext<ServerCommandSource> context) {
        try {
            JobController jobController = JobsFabricMod.getInstance().getJobController();
            
            ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
            var jobs = jobController.getAllJobs(target.getUuid());
            
            context.getSource().sendFeedback(
                () -> Text.literal("§6=== Jobs Info for " + target.getName().getString() + " ==="), 
                false
            );
            
            long activeJobs = jobs.values().stream().filter(j -> j.isActive()).count();
            context.getSource().sendFeedback(
                () -> Text.literal("§eActive Jobs: §a" + activeJobs), 
                false
            );
            
            jobs.forEach((jobId, jobInfo) -> {
                String status = jobInfo.isActive() ? "§a✓" : "§7○";
                context.getSource().sendFeedback(
                    () -> Text.literal(status + " §e" + jobId + " §7(Level " + jobInfo.getLevel() + 
                        ", " + jobInfo.getProgressPercent() + "%)"), 
                    false
                );
            });
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("§cError: " + e.getMessage()));
            return 0;
        }
    }
    
    private int resetJob(CommandContext<ServerCommandSource> context) {
        try {
            JobController jobController = JobsFabricMod.getInstance().getJobController();
            JsonStorageManager storageManager = JobsFabricMod.getInstance().getStorageManager();
            ConfigManager configManager = JobsFabricMod.getInstance().getConfigManager();
            
            ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
            String jobId = StringArgumentType.getString(context, "job");
            
            // Verify job exists
            if (configManager.getJob(jobId).isEmpty()) {
                context.getSource().sendError(Text.literal("§cJob not found: " + jobId));
                return 0;
            }
            
            // Load user and remove job info
            var user = storageManager.loadUser(target.getUuid());
            var jobInfo = user.getJobInfo(jobId);
            
            if (jobInfo == null) {
                context.getSource().sendError(Text.literal("§c" + target.getName().getString() + " has no progress in job: " + jobId));
                return 0;
            }
            
            // Remove the job completely
            user.removeJobInfo(jobId);
            storageManager.markDirty(target.getUuid());
            storageManager.saveUser(target.getUuid());
            
            context.getSource().sendFeedback(
                () -> Text.literal("§aReset job §e" + jobId + "§a for player §e" + target.getName().getString()), 
                true
            );
            
            target.sendMessage(Text.literal("§eYour progress in §a" + jobId + "§e has been reset by an admin."), false);
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("§cError: " + e.getMessage()));
            return 0;
        }
    }
    
    private int resetAllJobs(CommandContext<ServerCommandSource> context) {
        try {
            JsonStorageManager storageManager = JobsFabricMod.getInstance().getStorageManager();
            
            ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
            
            // Load user
            var user = storageManager.loadUser(target.getUuid());
            int jobCount = user.getAllJobs().size();
            
            if (jobCount == 0) {
                context.getSource().sendError(Text.literal("§c" + target.getName().getString() + " has no job progress to reset."));
                return 0;
            }
            
            // Clear all jobs
            user.clearAllJobs();
            storageManager.markDirty(target.getUuid());
            storageManager.saveUser(target.getUuid());
            
            context.getSource().sendFeedback(
                () -> Text.literal("§aReset all §e" + jobCount + "§a jobs for player §e" + target.getName().getString()), 
                true
            );
            
            target.sendMessage(Text.literal("§eAll your job progress has been reset by an admin."), false);
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("§cError: " + e.getMessage()));
            return 0;
        }
    }
}
