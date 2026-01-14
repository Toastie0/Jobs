package net.advancedplugins.jobs.jobs.steps;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.cache.RewardCache;
import net.advancedplugins.jobs.controller.JobController;
import net.advancedplugins.jobs.events.UserTaskCompleteEvent;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.jobs.locale.LocaleHandler;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.objects.users.User;
import net.advancedplugins.jobs.objects.users.UserJobInfo;
import net.advancedplugins.jobs.util.Services;
import net.advancedplugins.jobs.util.bossbar.BossBar;
import net.advancedplugins.simplespigot.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NotificationStep {
   private final Core plugin;
   private final LocaleHandler lang;
   private final RewardStep rewardStep;
   private final JobController controller;
   private final RewardCache rewardCache;
   private final String notificationMethod;
   private final boolean useNotifyPercentages;
   private final boolean bossBarEnabled;
   private final Map<UUID, BossBar> bossBars = Maps.newConcurrentMap();
   private final int bossBarLength;
   private final Set<String> disabledBossQuests;

   public NotificationStep(Core var1) {
      Config var2 = var1.getConfig("config");
      this.notificationMethod = var2.string("notification-method");
      this.rewardCache = var1.getRewardCache();
      this.bossBarEnabled = var2.bool("boss-bar.enabled");
      this.useNotifyPercentages = var2.bool("boss-bar.use-notify-percentages");
      this.bossBarLength = this.bossBarEnabled ? (var2.bool("boss-bar.persistent") ? 1073741823 : var2.integer("boss-bar.hide-after")) : -1;
      this.disabledBossQuests = Sets.newHashSet(var2.stringList("boss-bar.disabled-quest-types"));
      this.plugin = var1;
      this.lang = var1.getLocale();
      this.rewardStep = new RewardStep(var1);
      this.controller = var1.getJobController();
      if (this.bossBarEnabled) {
         var1.getBossBars().add(this.bossBars);
      }
   }

   public void process(Player var1, User var2, Job var3, BigDecimal var4, BigDecimal var5, String var6) {
      if (var4.compareTo(var5) != 0) {
         BigDecimal var7 = this.controller.getRequiredProgress(var2, var3);
         if (var5.compareTo(var7) > -1) {
            UserJobInfo var17 = this.controller.getJobInfo(var2, var3);
            if (var17.getLevel() < this.controller.getMaxLevel(var1) || this.controller.isCanWorkAtMax()) {
               UserTaskCompleteEvent var19 = new UserTaskCompleteEvent(var2, var3);
               this.plugin.runSync(() -> {
                  Bukkit.getServer().getPluginManager().callEvent(var19);
                  var19.ifNotCancelled(var7xx -> {
                     this.sendBossBarIfEnabled(var1, var3, 100.0, var5, true, var7, var6);
                     this.notify(var1, this.lang.questCompleteMessage(var3, var6));
                     this.rewardStep.process(var2, var3);
                  });
               });
            }
         } else {
            if (!this.useNotifyPercentages) {
               double var8 = Services.getPercentage(var5, var7).doubleValue();
               this.sendBossBarIfEnabled(var1, var3, var8, var5, false, var7, var6);
            }

            HashMap var16 = Maps.newHashMap();

            for (int var10 : var3.getNotifyAt()) {
               var16.put(var7.multiply(BigDecimal.valueOf((long)var10)).divide(BigDecimal.valueOf(100L)), var10);
            }

            boolean var18 = var3.getNotifyAt().contains(-1);

            for (BigDecimal var11 : var16.keySet()) {
               int var12 = var5.compareTo(var11);
               if (var18 || var12 == 0 || var11.compareTo(var4) > 0 && var12 > -1) {
                  String[] var13 = new String[]{this.lang.questProgressedMessage(var3, var5, var7, var6)};
                  if (this.useNotifyPercentages) {
                     this.sendBossBarIfEnabled(var1, var3, ((Integer)var16.get(var11)).intValue(), var5, false, var7, var6);
                  }

                  if (var3.getOverrideMessage() != null) {
                     var13[0] = Text.modify(
                        var3.getOverrideMessage(), var3x -> var3x.set("job_name", var3.getName()).set("progress", var5).set("required_progress", var7)
                     );
                     double var14 = var5.subtract(var4).doubleValue();
                     var3.getProgressRewards()
                        .stream()
                        .map(this.rewardCache::get)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(
                           var7x -> var13[0] = var7x.fillVariables(
                              var13[0], "reward." + var7x.getId() + ".", this.controller.getFixedJobInfo(var2, var3).getLevel(), var1, var3.getId(), var14
                           )
                        );
                  }

                  this.notify(var1, var13[0]);
                  break;
               }
            }
         }
      }
   }

   private void notify(Player var1, String var2) {
      if (this.notificationMethod.contains("chat")) {
         Text.sendMessage(var1, var2);
      }

      if (this.notificationMethod.contains("action bar")) {
         Services.sendActionBar(var1, var2);
      }
   }

   private void sendBossBarIfEnabled(Player var1, Job var2, double var3, BigDecimal var5, boolean var6, BigDecimal var7, String var8) {
      if (this.bossBarEnabled && !this.disabledBossQuests.contains(var8)) {
         String var9 = var6 ? this.lang.questBossCompleteMessage(var2) : this.lang.questBossProgressedMessage(var2, var5, var7);
         BossBar var10 = this.bossBars.get(var1.getUniqueId());
         if (var10 == null) {
            var10 = BossBar.Builder.create(this.plugin, var1, var9);
            this.bossBars.put(var1.getUniqueId(), var10);
         } else {
            var10.updatePlayer(var1);
            var10.setTitle(var9);
         }

         var10.setProgress(var3);
         var10.schedule(this.bossBarLength);
      }
   }
}
