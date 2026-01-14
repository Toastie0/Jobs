package net.advancedjobs.gui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.advancedjobs.config.ConfigManager;
import net.advancedjobs.controller.JobController;
import net.advancedjobs.objects.Job;
import net.advancedjobs.objects.UserJobInfo;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * GUI menu showing detailed progress for a specific job.
 * Allows players to view stats and leave the job.
 */
public class JobProgressMenu extends SimpleGui {
    
    private final ConfigManager configManager;
    private final JobController jobController;
    private final ServerPlayerEntity player;
    private final String jobId;
    
    public JobProgressMenu(ServerPlayerEntity player, ConfigManager configManager, 
                          JobController jobController, String jobId) {
        super(ScreenHandlerType.GENERIC_9X3, player, false);
        this.configManager = configManager;
        this.jobController = jobController;
        this.player = player;
        this.jobId = jobId;
        
        this.setTitle(Text.literal("§6§lJob Progress"));
        buildMenu();
    }
    
    private void buildMenu() {
        Optional<Job> jobOpt = configManager.getJob(jobId);
        if (jobOpt.isEmpty()) {
            this.close();
            return;
        }
        
        Job job = jobOpt.get();
        UserJobInfo jobInfo = jobController.getJobInfo(player.getUuid(), jobId);
        
        if (jobInfo == null) {
            this.close();
            return;
        }
        
        // Job icon
        Identifier materialId = Identifier.tryParse(job.getGuiItem().getMaterial());
        var item = materialId != null ? Registries.ITEM.get(materialId) : Items.PAPER;
        
        // Replace placeholders in job name
        String jobName = job.getGuiItem().getName()
            .replace("%level%", String.valueOf(jobInfo.getLevel()))
            .replace("&", "§");
        
        GuiElementBuilder jobIcon = new GuiElementBuilder(item)
            .setName(Text.literal(jobName))
            .hideFlags() // Hide vanilla item tooltips
            .glow();
        this.setSlot(4, jobIcon);
        
        // Level info
        GuiElementBuilder levelItem = new GuiElementBuilder(Items.EXPERIENCE_BOTTLE)
            .setName(Text.literal("§6§lLevel"))
            .addLoreLine(Text.literal("§7Current Level: §e" + jobInfo.getLevel()))
            .addLoreLine(Text.literal("§7Progress: §e" + formatBigDecimal(jobInfo.getProgress()) + 
                " / " + formatBigDecimal(jobInfo.getRequiredProgress())))
            .addLoreLine(Text.literal("§7Completion: §e" + jobInfo.getProgressPercent() + "%"));
        this.setSlot(10, levelItem);
        
        // Statistics
        GuiElementBuilder statsItem = new GuiElementBuilder(Items.WRITABLE_BOOK)
            .setName(Text.literal("§6§lStatistics"))
            .addLoreLine(Text.literal("§7Level: §e" + jobInfo.getLevel()))
            .addLoreLine(Text.literal("§7Status: " + (jobInfo.isActive() ? "§aActive" : "§7Inactive")));
        this.setSlot(13, statsItem);
        
        // Leave job button (if active)
        if (jobInfo.isActive()) {
            GuiElementBuilder leaveButton = new GuiElementBuilder(Items.RED_WOOL)
                .setName(Text.literal("§c§lLeave Job"))
                .addLoreLine(Text.literal("§7Click to leave this job."))
                .addLoreLine(Text.literal("§7Your progress will be saved!"))
                .setCallback((index, type, action) -> {
                    jobController.leaveJob(player.getUuid(), jobId);
                    this.close();
                    player.sendMessage(Text.literal("§eYou have left the job."), false);
                });
            this.setSlot(16, leaveButton);
        }
        
        // Back button
        GuiElementBuilder backButton = new GuiElementBuilder(Items.ARROW)
            .setName(Text.literal("§e§lBack"))
            .setCallback((index, type, action) -> {
                new JobListMenu(player, configManager, jobController).open();
            });
        this.setSlot(22, backButton);
    }
    
    private String formatBigDecimal(BigDecimal value) {
        return value.setScale(1, RoundingMode.HALF_UP).toPlainString();
    }
}
