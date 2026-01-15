package net.toastie.jobs.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.toastie.jobs.JobsFabricMod;
import net.toastie.jobs.config.ConfigManager;
import net.toastie.jobs.controller.JobController;
import net.toastie.jobs.gui.JobListMenu;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * /jobs command - Opens the jobs GUI menu.
 */
public class JobsCommand {
    
    public JobsCommand() {
    }
    
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, 
                        CommandRegistryAccess registryAccess,
                        CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("jobs")
            .executes(this::openJobsMenu));
    }
    
    private int openJobsMenu(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        if (!source.isExecutedByPlayer()) {
            source.sendError(Text.literal("§cThis command can only be used by players!"));
            return 0;
        }
        
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            return 0;
        }
        
        // Get instances from mod
        JobsFabricMod mod = JobsFabricMod.getInstance();
        ConfigManager configManager = mod.getConfigManager();
        JobController jobController = mod.getJobController();
        
        // Open the jobs GUI
        new JobListMenu(player, configManager, jobController).open();
        
        return 1;
    }
}
