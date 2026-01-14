package net.advancedplugins.jobs.menus.menus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.controller.BoostersController;
import net.advancedplugins.jobs.controller.JobController;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.jobs.impl.utils.tuple.ImmutablePair;
import net.advancedplugins.jobs.locale.LocaleHandler;
import net.advancedplugins.jobs.menus.UserDependent;
import net.advancedplugins.jobs.menus.service.extensions.PageableConfigMenu;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.objects.reward.JobReward;
import net.advancedplugins.jobs.objects.reward.Reward;
import net.advancedplugins.jobs.objects.users.User;
import net.advancedplugins.jobs.objects.users.UserJobInfo;
import net.advancedplugins.jobs.util.Services;
import net.advancedplugins.simplespigot.config.Config;
import net.advancedplugins.simplespigot.item.SpigotItem;
import net.advancedplugins.simplespigot.menu.item.MenuItem;
import net.advancedplugins.simplespigot.menu.service.MenuService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ProgressMenu extends PageableConfigMenu<Integer> implements UserDependent {
   private final JobController jobController;
   private final LocaleHandler lang;
   private final User user;
   private final Job job;
   private final boolean autoBooster;

   public ProgressMenu(Core var1, Config var2, Player var3, Job var4) {
      super(var1, var2, var3, var2x -> var2x.set("job", var4.getName()).set("job-menu-title", var4.getProgressMenuTitle()).tryAddPapi(var3));
      this.jobController = var1.getJobController();
      this.lang = var1.getLocale();
      this.user = var1.getUserCache().getOrThrow(var3.getUniqueId());
      this.job = var4;
      this.autoBooster = var1.getConfig("config").boolOr("menu-auto-add-booster-lore", true);
   }

   @Override
   public void redraw() {
      this.drawPageableItems(() -> this.drawConfigItems(var1 -> var1.set("job", this.job.getName()).tryAddPapi(this.player)));
   }

   public MenuItem pageableItem(Integer var1) {
      try {
         UserJobInfo var2 = this.jobController.getFixedJobInfo(this.user, this.job);
         int var3 = var2.getLevel();
         ItemStack var4 = SpigotItem.toItem(this.config, var1 > var3 ? "lockedLevel.item" : (var1 == var3 ? "currentLevel.item" : "unlockedLevel.item"))
            .clone();
         ItemMeta var5 = var4.getItemMeta();
         ArrayList var6 = new ArrayList();

         for (String var8 : var5.getLore()) {
            if (!var8.contains("%rewards value%")) {
               var6.add(var8);
            } else {
               this.jobController
                  .getRewards(this.job, var1)
                  .forEach(
                     var4x -> {
                        String var5x = this.plugin.getBoostersController().getMenuLore(this.user.getPlayer(), new JobReward(this.job, (Reward<?>)var4x));
                        var6.add(
                           Text.modify(
                              var8,
                              var4xx -> var4xx.set(
                                 "rewards value",
                                 Text.modify(var4x.getName(var1, this.player, this.job.getId(), 0.0), var1xxx -> var1xxx.set("booster", var5x))
                              )
                           )
                        );
                        if (this.autoBooster && !var5x.isEmpty()) {
                           var6.add(var5x);
                        }
                     }
                  );
            }
         }

         String var12 = this.plugin.getBoostersController().getMenuLore(this.player, this.job, BoostersController.BoosterType.POINTS);
         var5.setLore(var6);
         var4.setItemMeta(var5);
         BigDecimal var13 = new BigDecimal(String.valueOf(this.job.getRequiredProgress(var1).doubleValue()));
         BigDecimal var9 = var1 > var3 ? BigDecimal.ZERO : (var1 == var3 ? this.jobController.getJobProgress(this.user, this.job) : var13);
         BigDecimal var10 = this.job
            .getRewardedPoints(var1)
            .multiply(
               BigDecimal.valueOf(
                  1.0 + this.plugin.getBoostersController().calculateFinalBooster(this.user.getUuid(), BoostersController.BoosterType.POINTS, this.job) / 100.0
               )
            );
         return MenuItem.builderOf(
               Text.modify(
                  var4,
                  var6x -> var6x.set("job", this.job.getName())
                     .set("level", var1)
                     .set("points", var10)
                     .set("total_progress", var9)
                     .set("required_progress", var13)
                     .set("percentage_progress", Services.getPercentageString(var9, var13).concat("%"))
                     .set("progress_bar", Services.getProgressBar(var9, var13, this.lang))
                     .set("boosters", var12)
                     .tryAddPapi(this.player)
               )
            )
            .build();
      } catch (Exception var11) {
         var11.printStackTrace();
         return MenuItem.builderOf(new ItemStack(Material.BARRIER)).build();
      }
   }

   @Override
   public ImmutablePair<Collection<Integer>, Collection<Integer>> elementalValues() {
      int var1 = Math.floorDiv(this.jobController.getFixedJobInfo(this.user, this.job).getLevel(), 100);
      var1 = ++var1 * 100;
      var1 = Math.min(var1, this.jobController.getMaxLevel(this.player));
      ArrayList var2 = new ArrayList();

      for (int var3 = 1; var3 <= var1; var3++) {
         var2.add(var3);
      }

      return ImmutablePair.of(var2, MenuService.parseSlots(this, this.config, "levelSlots"));
   }

   @Override
   public boolean isUserViable() {
      return this.user != null;
   }
}
