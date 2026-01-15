package net.toastie.jobs.gui;

import java.util.Map;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.toastie.jobs.config.ConfigManager;
import net.toastie.jobs.controller.JobController;
import net.toastie.jobs.objects.GuiItemConfig;
import net.toastie.jobs.objects.Job;
import net.toastie.jobs.objects.UserJobInfo;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * GUI menu showing all available jobs.
 * Players can click on jobs to join them.
 */
public class JobListMenu extends SimpleGui {
    
    private final ConfigManager configManager;
    private final JobController jobController;
    private final ServerPlayerEntity player;
    
    public JobListMenu(ServerPlayerEntity player, ConfigManager configManager, JobController jobController) {
        super(ScreenHandlerType.GENERIC_9X5, player, false);
        this.configManager = configManager;
        this.jobController = jobController;
        this.player = player;
        
        this.setTitle(Text.literal("§6§lJobs Menu"));
        buildMenu();
    }
    
    private void buildMenu() {
        // Get player's active jobs
        Map<String, UserJobInfo> playerJobs = jobController.getAllJobs(player.getUuid());
        int maxActiveJobs = configManager.getInt("jobs.maxActiveJobs");
        long activeCount = playerJobs.values().stream().filter(UserJobInfo::isActive).count();
        
        // Add info item
        GuiElementBuilder infoItem = new GuiElementBuilder(Items.BOOK)
            .setName(Text.literal("§6§lActive Jobs: §e" + activeCount + "/" + maxActiveJobs))
            .addLoreLine(Text.literal("§7Click on a job below to join!"))
            .addLoreLine(Text.literal("§7You can have up to " + maxActiveJobs + " active jobs."));
        this.setSlot(4, infoItem);
        
        // Add jobs
        int slot = 10;
        for (Job job : configManager.getAllJobs()) {
            if (slot >= 35) break; // Stay within menu bounds
            
            GuiElementBuilder jobItem = createJobItem(job, playerJobs.get(job.getId()));
            this.setSlot(slot, jobItem);
            
            slot++;
            if (slot % 9 == 8) slot += 2; // Skip edges
        }
        
        // Add close button
        GuiElementBuilder closeButton = new GuiElementBuilder(Items.BARRIER)
            .setName(Text.literal("§c§lClose"))
            .setCallback((index, type, action) -> {
                this.close();
            });
        this.setSlot(40, closeButton);
    }
    
    private GuiElementBuilder createJobItem(Job job, UserJobInfo jobInfo) {
        GuiItemConfig guiConfig = job.getGuiItem();
        
        // Parse material from config
        Identifier materialId = Identifier.tryParse(guiConfig.getMaterial());
        var item = materialId != null ? Registries.ITEM.get(materialId) : Items.PAPER;
        
        // Replace placeholders in name
        String itemName = replacePlaceholders(guiConfig.getName(), job, jobInfo);
        
        GuiElementBuilder builder = new GuiElementBuilder(item)
            .setName(Text.literal(itemName.replace("&", "§")))
            .hideFlags(); // Hide vanilla item tooltips
        
        // Add lore from config with placeholder replacement
        for (String loreLine : guiConfig.getLore()) {
            String processed = replacePlaceholders(loreLine, job, jobInfo);
            builder.addLoreLine(Text.literal(processed.replace("&", "§")));
        }
        
        // Add status info
        builder.addLoreLine(Text.empty());
        if (jobInfo != null && jobInfo.isActive()) {
            builder.addLoreLine(Text.literal("§a✓ Currently Active").formatted(Formatting.GREEN));
        } else if (jobInfo != null) {
            builder.addLoreLine(Text.literal("§7○ Not Active").formatted(Formatting.GRAY));
        } else {
            builder.addLoreLine(Text.literal("§7○ Not Joined").formatted(Formatting.GRAY));
            builder.addLoreLine(Text.empty());
            builder.addLoreLine(Text.literal("§aClick to join!"));
        }
        
        // Add glow effect if configured
        if (guiConfig.isGlow()) {
            builder.glow();
        }
        
        // Set click callback
        builder.setCallback((index, type, action) -> {
            if (jobInfo != null && jobInfo.isActive()) {
                // Open progress menu
                new JobProgressMenu(player, configManager, jobController, job.getId()).open();
            } else {
                // Attempt to join
                jobController.joinJob(player.getUuid(), job.getId());
                this.close();
            }
        });
        
        return builder;
    }
    
    private String replacePlaceholders(String text, Job job, UserJobInfo jobInfo) {
        if (jobInfo == null) {
            return text
                .replace("%level%", "1")
                .replace("%required_progress%", String.valueOf(job.calculateRequiredProgress(1).intValue()))
                .replace("%total_progress%", "0")
                .replace("%progress_percent%", "0.0")
                .replace("%progress_bar%", "§8▯▯▯▯▯▯▯▯▯▯")
                .replace("%active%", "§7✗ Inactive");
        }
        
        int level = jobInfo.getLevel();
        int totalProgress = jobInfo.getProgress().intValue();
        int requiredProgress = jobInfo.getRequiredProgress().intValue();
        double progressPercent = jobInfo.getProgressPercent();
        
        // Generate progress bar
        String progressBar = generateProgressBar(progressPercent);
        
        // Format percentage to one decimal place
        String percentStr = String.format("%.1f", progressPercent);
        
        return text
            .replace("%level%", String.valueOf(level))
            .replace("%required_progress%", String.valueOf(requiredProgress))
            .replace("%total_progress%", String.valueOf(totalProgress))
            .replace("%progress_percent%", percentStr)
            .replace("%progress_bar%", progressBar)
            .replace("%active%", jobInfo.isActive() ? "§a✓ Active" : "§7✗ Inactive");
    }
    
    private String generateProgressBar(double percent) {
        int filled = (int) (percent / 10.0);
        StringBuilder bar = new StringBuilder("§a");
        for (int i = 0; i < 10; i++) {
            if (i < filled) {
                bar.append("▮");
            } else {
                if (i == filled) bar.append("§8");
                bar.append("▯");
            }
        }
        return bar.toString();
    }
}
