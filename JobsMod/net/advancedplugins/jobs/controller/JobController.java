package net.advancedplugins.jobs.controller;

import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import me.clip.placeholderapi.PlaceholderAPI;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.actions.Action;
import net.advancedplugins.jobs.cache.RewardCache;
import net.advancedplugins.jobs.events.UserJobJoinEvent;
import net.advancedplugins.jobs.events.UserJobLeaveEvent;
import net.advancedplugins.jobs.events.UserLevelUpEvent;
import net.advancedplugins.jobs.events.UserPointsGetEvent;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import net.advancedplugins.jobs.impl.utils.text.Replacer;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.jobs.jobs.steps.NotificationStep;
import net.advancedplugins.jobs.locale.LocaleHandler;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.objects.reward.Reward;
import net.advancedplugins.jobs.objects.reward.StringVariable;
import net.advancedplugins.jobs.objects.users.User;
import net.advancedplugins.jobs.objects.users.UserJobInfo;
import net.advancedplugins.simplespigot.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class JobController {
   private final Core plugin;
   private final LocaleHandler lang;
   private final int defaultMaxJobs;
   private final int maxJobsInPerm;
   private final RewardCache rewardCache;
   private final List<Action> completionActions = Lists.newArrayList();
   private final Function<User, ConcurrentHashMap<String, UserJobInfo>> jobMap = var0 -> var0.getJobStore().asMap();
   private final Map<String, String> leaveVariables;
   private final Map<String, String> joinVariables;
   private final Map<UUID, Map<String, Long>> cooldowns;
   private final boolean isCooldownEnabled;
   private final long cooldownMinutes;
   private final Map<UUID, Map<String, Long>> progressCooldowns;
   private int maxLevels;
   private final int maxLevelPermission;
   private boolean canWorkAtMax;

   public JobController(Core var1) {
      this.plugin = var1;
      this.lang = var1.getLocale();
      this.defaultMaxJobs = var1.getConfig("config").integer("default-job-slots");
      this.maxJobsInPerm = var1.getConfig("config").integer("max-jobs-in-permission");
      this.cooldowns = new HashMap<>();
      this.isCooldownEnabled = var1.getConfig("config").boolOr("join-leave-cooldown.enabled", false);
      this.cooldownMinutes = var1.getConfig("config").integer("join-leave-cooldown.minutes");
      this.progressCooldowns = new HashMap<>();
      this.leaveVariables = new HashMap<>();
      var1.getConfig("config").keys("job-leave.variables", false).forEach(var2 -> {
         List var3 = var1.getConfig("config").list("job-leave.variables." + var2);
         if (var3.isEmpty()) {
            this.leaveVariables.put(var2, var1.getConfig("config").string("job-leave.variables." + var2));
         } else {
            this.leaveVariables.put(var2, String.join("", var3));
         }
      });
      this.joinVariables = new HashMap<>();
      var1.getConfig("config").keys("job-join.variables", false).forEach(var2 -> {
         List var3 = var1.getConfig("config").list("job-join.variables." + var2);
         if (var3.isEmpty()) {
            this.joinVariables.put(var2, var1.getConfig("config").string("job-join.variables." + var2));
         } else {
            this.joinVariables.put(var2, String.join("", var3));
         }
      });
      this.rewardCache = var1.getRewardCache();
      this.completionActions.addAll(var1.getConfig("config").stringList("level-up-actions").stream().map(Action::parse).collect(Collectors.toList()));
      this.maxLevels = var1.getConfig("config").getConfiguration().getInt("max-level", -1);
      this.maxLevelPermission = var1.getConfig("config").getConfiguration().getInt("max-level-in-permission", -1);
      if (this.maxLevels == -1) {
         this.maxLevels = Integer.MAX_VALUE;
      }

      this.canWorkAtMax = var1.getConfig("config").bool("can-work-when-max-level");
   }

   public Map<String, UserJobInfo> getJobs(User var1) {
      return this.jobMap.apply(var1);
   }

   public Map<String, UserJobInfo> getActiveJobs(User var1) {
      if (var1 == null) {
         return new HashMap<>();
      } else {
         HashMap var2 = new HashMap();
         this.jobMap.apply(var1).forEach((var1x, var2x) -> {
            if (var2x.isActive()) {
               var2.put(var1x, var2x);
            }
         });
         return var2;
      }
   }

   public UserJobInfo getJobInfo(User var1, Job var2) {
      return this.getJobs(var1).get(var2.getId());
   }

   public UserJobInfo getFixedJobInfo(User var1, Job var2) {
      UserJobInfo var3 = this.getJobInfo(var1, var2);
      return var3 != null ? var3 : new UserJobInfo(var2.getId(), false);
   }

   public boolean isJobActive(User var1, Job var2) {
      return this.getFixedJobInfo(var1, var2).isActive();
   }

   public BigDecimal getJobProgress(User var1, Job var2) {
      return this.failedIndex(var1, var2) == JobController.FailedIndex.NONE ? this.getJobInfo(var1, var2).getProgress() : BigDecimal.ZERO;
   }

   public BigDecimal setJobProgress(User var1, Job var2, BigDecimal var3) {
      BigDecimal var4 = this.getRequiredProgress(var1, var2);
      BigDecimal var5 = var4.min(var3);
      this.fillFailedIndexes(var1, var2);
      this.getJobInfo(var1, var2).setProgress(var5);
      return var5;
   }

   public BigDecimal addJobProgress(User var1, Job var2, BigDecimal var3, boolean var4) {
      BigDecimal var5 = this.getJobProgress(var1, var2);
      BigDecimal var6 = this.getRequiredProgress(var1, var2);
      if (var5.compareTo(var6) < 0) {
         BigDecimal var7 = this.setJobProgress(var1, var2, var5.add(var3));
         double var8 = var7.subtract(var5).doubleValue();
         var2.getProgressCommands()
            .forEach(
               var5x -> Bukkit.dispatchCommand(
                  Bukkit.getConsoleSender(),
                  Text.modify(
                     StringVariable.fillVariables(
                        var2.getProgressCommandsVariables(),
                        var5x,
                        var5xx -> var5xx.set("level", this.getFixedJobInfo(var1, var2).getLevel()).set("progress", var8)
                     ),
                     var5xx -> var5xx.set("player", var1.getPlayer().getName()).set("level", this.getFixedJobInfo(var1, var2).getLevel()).set("progress", var8)
                  )
               )
            );
         var2.getProgressRewards()
            .stream()
            .map(this.rewardCache::get)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(var5x -> var5x.reward(var1.getPlayer(), var2.getName(), this.getFixedJobInfo(var1, var2).getLevel(), var2.getId(), var8));
         if (var4 && var7.compareTo(var6) > -1) {
            new NotificationStep(this.plugin).process(var1.getPlayer(), var1, var2, var5, var7, "");
         }

         return var7;
      } else {
         return var5;
      }
   }

   public BigDecimal addJobProgress(User var1, Job var2, BigDecimal var3) {
      return this.addJobProgress(var1, var2, var3, false);
   }

   public void joinToJob(Job var1, User var2) {
      Player var3 = Bukkit.getPlayer(var2.getUuid());
      if (var1.isPremiumJob() && !var2.isPremium()) {
         var3.sendMessage(Text.modify(this.lang.getString("job-join.only-premium")));
      } else if (var1.getRequiredPermission() != null && !var3.hasPermission(var1.getRequiredPermission())) {
         var3.sendMessage(Text.modify(this.lang.getString("job-join.no-permission")));
      } else if (var1.getRequiredPoints() > var2.getPoints()) {
         var3.sendMessage(Text.modify(this.lang.getString("job-join.points"), var1x -> var1x.set("points", var1.getRequiredPoints())));
      } else {
         int var4 = this.getMaxJobs(var3);
         if (this.getActiveJobs(var2).size() >= var4) {
            var3.sendMessage(Text.modify(this.lang.getString("job-join.limit"), var1x -> var1x.set("limit", var4)));
         } else {
            UserJobInfo var5 = this.getFixedJobInfo(var2, var1);
            if (var5.isActive()) {
               var3.sendMessage(Text.modify(this.lang.getString("job-join.already-in")));
            } else {
               if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                  List var6 = PlaceholderAPI.setPlaceholders(var3, this.plugin.getConfig("config").list("job-join.conditions.or"));
                  List var7 = PlaceholderAPI.setPlaceholders(var3, this.plugin.getConfig("config").list("job-join.conditions.and"));
                  boolean var8 = var6.isEmpty();
                  boolean var9 = true;

                  for (String var11 : var6) {
                     boolean var12 = ASManager.parseCondition(var11);
                     if (var12) {
                        var8 = true;
                        break;
                     }
                  }

                  Iterator var13 = var7.iterator();

                  while (true) {
                     if (var13.hasNext()) {
                        String var14 = (String)var13.next();
                        boolean var15 = ASManager.parseCondition(var14);
                        if (var15) {
                           continue;
                        }

                        var9 = false;
                     }

                     if (!var8 || !var9) {
                        var3.sendMessage(Text.modify(this.lang.getString("job-join.required-conditions")));
                        return;
                     }

                     this.plugin
                        .getConfig("config")
                        .list("job-join.commands")
                        .forEach(
                           var2x -> Bukkit.dispatchCommand(
                              Bukkit.getConsoleSender(),
                              Replacer.to(
                                 StringVariable.fillVariables(this.joinVariables, (String)var2x, var1xx -> var1xx.tryAddPapi(var3)),
                                 var1xx -> var1xx.set("player", var3.getName())
                              )
                           )
                        );
                     break;
                  }
               }

               if (this.isCooldownEnabled) {
                  this.cooldowns.putIfAbsent(var3.getUniqueId(), new HashMap<>());
                  this.cooldowns.get(var3.getUniqueId()).put(var1.getId(), System.currentTimeMillis() + this.cooldownMinutes * 60000L);
                  FoliaScheduler.runTaskLater(this.plugin, () -> this.cooldowns.get(var3.getUniqueId()).remove(var1.getId()), this.cooldownMinutes * 1200L);
               }

               this.plugin.runSync(() -> {
                  UserJobJoinEvent var5x = new UserJobJoinEvent(var2, var1);
                  Bukkit.getServer().getPluginManager().callEvent(var5x);
                  var5x.ifNotCancelled(var5xx -> {
                     var5.setActive(true);
                     this.getJobs(var2).put(var1.getId(), var5);
                     var3.sendMessage(Text.modify(this.lang.getString("job-join.joined"), var1xxx -> var1xxx.set("job", var1.getName())));
                     this.updateMenu(var3);
                  });
               });
            }
         }
      }
   }

   public void leaveJob(Job var1, User var2) {
      Player var3 = Bukkit.getPlayer(var2.getUuid());
      UserJobInfo var4 = this.getFixedJobInfo(var2, var1);
      if (!var4.isActive()) {
         var3.sendMessage(Text.modify(this.lang.getString("job-leave.already-left")));
      } else {
         if (this.isCooldownEnabled && this.cooldowns.containsKey(var3.getUniqueId()) && this.cooldowns.get(var3.getUniqueId()).containsKey(var1.getId())) {
            long var5 = this.cooldowns.get(var3.getUniqueId()).get(var1.getId()) - System.currentTimeMillis();
            if (var5 > 0L) {
               var3.sendMessage(Text.modify(this.lang.getString("job-leave.cooldown"), var2x -> var2x.set("time", Math.floor(var5 / 600.0) / 100.0)));
               return;
            }
         }

         if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            List var12 = PlaceholderAPI.setPlaceholders(var3, this.plugin.getConfig("config").list("job-leave.conditions.or"));
            List var6 = PlaceholderAPI.setPlaceholders(var3, this.plugin.getConfig("config").list("job-leave.conditions.and"));
            boolean var7 = var12.isEmpty();
            boolean var8 = true;

            for (String var10 : var12) {
               boolean var11 = ASManager.parseCondition(var10);
               if (var11) {
                  var7 = true;
                  break;
               }
            }

            Iterator var13 = var6.iterator();

            while (true) {
               if (var13.hasNext()) {
                  String var14 = (String)var13.next();
                  boolean var15 = ASManager.parseCondition(var14);
                  if (var15) {
                     continue;
                  }

                  var8 = false;
               }

               if (!var7 || !var8) {
                  var3.sendMessage(Text.modify(this.lang.getString("job-leave.required-conditions")));
                  return;
               }

               this.plugin
                  .getConfig("config")
                  .list("job-leave.commands")
                  .forEach(
                     var3x -> Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        Replacer.to(
                           StringVariable.fillVariables(this.leaveVariables, (String)var3x, var1xx -> var1xx.tryAddPapi(var3)),
                           var2xx -> var2xx.set("player", var3.getName()).set("job", var1.getId())
                        )
                     )
                  );
               break;
            }
         }

         this.plugin.runSync(() -> {
            UserJobLeaveEvent var5x = new UserJobLeaveEvent(var2, var1);
            Bukkit.getServer().getPluginManager().callEvent(var5x);
            var5x.ifNotCancelled(var5xx -> {
               var4.setActive(false);
               if (this.plugin.getConfig("config").boolOr("jobs-reset.reset-progress", true)) {
                  var4.setProgress(BigDecimal.ZERO);
               }

               this.getJobs(var2).put(var1.getId(), var4);
               var3.sendMessage(Text.modify(this.lang.getString("job-leave.left"), var1xxx -> var1xxx.set("job", var1.getName())));
               this.updateMenu(var3);
            });
         });
      }
   }

   private void updateMenu(Player var1) {
      Menu var2 = this.plugin.getMenuFactory().getOpenMenus().get(var1.getUniqueId());
      if (var2 != null) {
         var2.redraw();
      }
   }

   public BigDecimal getRequiredProgress(User var1, Job var2) {
      int var3 = this.getFixedJobInfo(var1, var2).getLevel();
      return var2.getRequiredProgress(var3);
   }

   public int getMaxJobs(Player var1) {
      for (int var2 = this.maxJobsInPerm; var2 > -1; var2--) {
         if (var1.hasPermission("advancedjobs.slots." + var2)) {
            return var2;
         }
      }

      return this.defaultMaxJobs;
   }

   private void fillFailedIndexes(User var1, Job var2) {
      if (this.failedIndex(var1, var2) == JobController.FailedIndex.JOB_LAYER) {
         this.getJobs(var1).put(var2.getId(), new UserJobInfo(var2.getId(), false));
      }
   }

   private JobController.FailedIndex failedIndex(User var1, Job var2) {
      return !this.getJobs(var1).containsKey(var2.getId()) ? JobController.FailedIndex.JOB_LAYER : JobController.FailedIndex.NONE;
   }

   public void givePoints(User var1, double var2) {
      UserPointsGetEvent var4 = new UserPointsGetEvent(var1, var2);
      this.plugin.runSync(() -> {
         Bukkit.getServer().getPluginManager().callEvent(var4);
         var4.ifNotCancelled(var3 -> var1.addPoints(var2));
      });
   }

   public void setPoints(User var1, double var2) {
      var1.setPoints(var2);
   }

   public void levelUp(User var1, Job var2) {
      this.fillFailedIndexes(var1, var2);
      UserJobInfo var3 = this.getJobInfo(var1, var2);
      this.plugin
         .runSync(
            () -> {
               int var4 = var3.getLevel() + 1;
               if (var4 > this.getMaxLevel(var1.getPlayer())) {
                  if (this.canWorkAtMax) {
                     this.reward(var1, var2, var4 - 1);
                  }
               } else {
                  UserLevelUpEvent var5 = new UserLevelUpEvent(var1, var2, var4);
                  Bukkit.getServer().getPluginManager().callEvent(var5);
                  if (!var5.isCancelled()) {
                     var3.setLevel(var5.getLevel());
                     this.reward(var1, var2, var5.getLevel() - 1);
                     Player var6 = Bukkit.getPlayer(var1.getUuid());
                     if (var6 != null) {
                        Action.executeSimple(
                           var6,
                           this.completionActions,
                           this.plugin,
                           new Replacer().set("level", var5.getLevel()).set("job_name", var2.getName()).set("player", var6.getName())
                        );
                     }
                  }
               }
            }
         );
   }

   public void reward(User var1, Job var2, int var3) {
      Player var4 = Bukkit.getPlayer(var1.getUuid());
      Set var5 = this.getRewards(var2, var3);
      var5.forEach(var3x -> var3x.reward(var4, var2.getName(), var3, var2.getId(), 0.0));
   }

   public Set<Reward<?>> getRewards(Job var1, int var2) {
      Object var3 = new HashSet<>(var1.getDefaultRewards());
      if (var1.isBothRewards() && var1.getLevelRewards().containsKey(var2)) {
         var3.addAll(var1.getLevelRewards().get(var2));
      }

      if (!var1.isBothRewards() && var1.getLevelRewards().containsKey(var2)) {
         var3 = var1.getLevelRewards().get(var2);
      }

      return var3.stream().map(this.rewardCache::get).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
   }

   public int getMaxLevel(Player var1) {
      if (this.maxLevelPermission == -1) {
         return this.maxLevels;
      } else {
         for (int var2 = this.maxLevelPermission; var2 >= 0; var2--) {
            if (var1.hasPermission("advancedjobs.max-level." + var2)) {
               return var2;
            }
         }

         return this.maxLevels;
      }
   }

   public String getMenuActionMessage(User var1, Job var2) {
      Player var3 = var1.getPlayer();
      boolean var4 = this.getActiveJobs(var1).containsKey(var2.getId());
      boolean var5 = var1.getPoints() >= var2.getRequiredPoints();
      boolean var6 = var2.getRequiredPermission() == null || var3.hasPermission(var2.getRequiredPermission());
      if (var2.isPremiumJob() && !var1.isPremium()) {
         return this.lang.getString("job-action.premium");
      } else if (!var6) {
         return this.lang.getString("job-action.permission");
      } else if (!var5) {
         return Text.modify(this.lang.getString("job-action.points"), var1x -> var1x.set("points", var2.getRequiredPoints()));
      } else {
         return var4 ? this.lang.getString("job-action.leave") : this.lang.getString("job-action.join");
      }
   }

   public void startProgressCooldown(UUID var1, Job var2) {
      this.progressCooldowns.putIfAbsent(var1, new HashMap<>());
      this.progressCooldowns.get(var1).put(var2.getId(), System.currentTimeMillis() + var2.getCooldown());
   }

   public long getRemainingProgressCooldown(UUID var1, Job var2) {
      return !this.progressCooldowns.containsKey(var1)
         ? 0L
         : this.progressCooldowns.get(var1).getOrDefault(var2.getId(), System.currentTimeMillis()) - System.currentTimeMillis();
   }

   public boolean hasProgressCooldown(UUID var1, Job var2) {
      return this.getRemainingProgressCooldown(var1, var2) > 0L;
   }

   public boolean isCanWorkAtMax() {
      return this.canWorkAtMax;
   }

   private static enum FailedIndex {
      JOB_LAYER,
      NONE;
   }
}
