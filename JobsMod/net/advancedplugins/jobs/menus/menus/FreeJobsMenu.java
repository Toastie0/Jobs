package net.advancedplugins.jobs.menus.menus;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.controller.JobController;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.jobs.impl.utils.tuple.ImmutablePair;
import net.advancedplugins.jobs.jobs.JobsResetHandler;
import net.advancedplugins.jobs.locale.LocaleHandler;
import net.advancedplugins.jobs.menus.UserDependent;
import net.advancedplugins.jobs.menus.service.extensions.PageableConfigMenu;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.objects.users.User;
import net.advancedplugins.jobs.objects.users.UserJobInfo;
import net.advancedplugins.jobs.util.Services;
import net.advancedplugins.simplespigot.config.Config;
import net.advancedplugins.simplespigot.item.SpigotItem;
import net.advancedplugins.simplespigot.menu.item.MenuItem;
import net.advancedplugins.simplespigot.menu.item.click.ClickType;
import net.advancedplugins.simplespigot.menu.service.MenuService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FreeJobsMenu extends PageableConfigMenu<Job> implements UserDependent {
   private final JobsResetHandler jobsResetHandler;
   private final JobController jobController;
   private final LocaleHandler lang;
   private final User user;
   private final Core plugin;
   private final boolean autoBooster;

   public FreeJobsMenu(Core var1, Config var2, Player var3) {
      super(var1, var2, var3, var1x -> var1x.tryAddPapi(var3));
      this.jobsResetHandler = var1.getJobsResetHandler();
      this.jobController = var1.getJobController();
      this.lang = var1.getLocale();
      this.user = var1.getUserCache().getOrThrow(var3.getUniqueId());
      this.plugin = var1;
      this.autoBooster = var1.getConfig("config").boolOr("menu-auto-add-booster-lore", true);
      this.addUpdater(var1, 20);
   }

   @Override
   public void redraw() {
      this.drawPageableItems(
         () -> this.drawConfigItems(var1 -> var1.set("time_left", this.jobsResetHandler.asString()).set("page", this.getPage()).tryAddPapi(this.player))
      );
   }

   public MenuItem pageableItem(Job var1) {
      try {
         ItemStack var2 = var1.getGuiItem().clone();
         UserJobInfo var3 = this.user.getJobStore().asMap().containsKey(var1.getId())
            ? this.user.getJobStore().asMap().get(var1.getId())
            : new UserJobInfo(var1.getId(), false);
         if (var3.isActive()) {
            SpigotItem.Builder var4 = new SpigotItem.Builder().itemStack(var2);
            var4.glow();
            var2 = var4.build();
         }

         String var13 = this.plugin.getBoostersController().getMenuLore(this.player, var1);
         if (this.autoBooster && var2.hasItemMeta() && !var13.isEmpty()) {
            ItemMeta var5 = var2.getItemMeta();
            List var6 = var5.getLore();
            var6.add(" ");
            var6.add(var13);
            var6.add(" ");
            var5.setLore(var6);
            var2.setItemMeta(var5);
         }

         BigDecimal var14 = this.jobController.getJobProgress(this.user, var1);
         BigDecimal var15 = new BigDecimal(String.valueOf(var1.getRequiredProgress(var3.getLevel()).doubleValue()));
         String var7 = this.jobController.getMenuActionMessage(this.user, var1);
         boolean var8 = this.jobController.getActiveJobs(this.user).containsKey(var1.getId());
         long var9 = this.jobController.getRemainingProgressCooldown(this.player.getUniqueId(), var1);
         String var11 = var9 > 0L ? var9 / 1000L + "s" : "-";
         return MenuItem.builderOf(
               Text.modify(
                  var2,
                  var8x -> var8x.set("active", var3.isActive() ? this.lang.getString("job-active.active") : this.lang.getString("job-active.not-active"))
                     .set("level", var3.getLevel())
                     .set("total_progress", var14.min(var15))
                     .set("required_progress", var15)
                     .set("percentage_progress", Services.getPercentageString(var14, var15).concat("%"))
                     .set("progress_bar", Services.getProgressBar(var14, var15, this.lang))
                     .set(
                        "rewards",
                        this.jobController
                           .getRewards(var1, var3.getLevel())
                           .stream()
                           .map(var3xx -> var3xx.getName(var3.getLevel(), this.player, var1.getId(), 0.0))
                           .collect(Collectors.joining(","))
                     )
                     .set("action", var7)
                     .set("boosters", var13)
                     .set("progress_cooldown", var11)
                     .tryAddPapi(this.player)
               )
            )
            .onClick((var3x, var4x) -> {
               if (var4x == ClickType.RIGHT) {
                  ProgressMenu var5x = new ProgressMenu(this.plugin, this.plugin.getConfig("progress-menu"), this.player, var1);
                  this.plugin.getMenuFactory().initiateMenu(this.player, () -> var5x).show();
               } else {
                  if (var8) {
                     this.jobController.leaveJob(var1, this.user);
                  } else {
                     this.jobController.joinToJob(var1, this.user);
                  }
               }
            })
            .build();
      } catch (Exception var12) {
         var12.printStackTrace();
         return MenuItem.builderOf(new ItemStack(Material.BARRIER)).build();
      }
   }

   @Override
   public ImmutablePair<Collection<Job>, Collection<Integer>> elementalValues() {
      return ImmutablePair.of(this.jobsResetHandler.getCurrentFreeJobs(), MenuService.parseSlots(this, this.config, "jobs-slots"));
   }

   @Override
   public boolean isUserViable() {
      return this.user != null;
   }
}
