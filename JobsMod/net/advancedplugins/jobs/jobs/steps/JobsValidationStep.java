package net.advancedplugins.jobs.jobs.steps;

import com.google.common.collect.Sets;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.controller.BoostersController;
import net.advancedplugins.jobs.controller.JobController;
import net.advancedplugins.jobs.impl.actions.ActionExecution;
import net.advancedplugins.jobs.impl.actions.objects.variable.ExecutableActionResult;
import net.advancedplugins.jobs.impl.actions.objects.variable.Variable;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.objects.users.User;
import net.advancedplugins.simplespigot.config.Config;
import net.advancedplugins.simplespigot.service.Locks;
import org.bukkit.Bukkit;

public class JobsValidationStep {
   private final JobsCompleteStep completionStep;
   private final JobController controller;
   private final ReentrantLock jobsLock = Locks.newReentrantLock();
   private final Set<String> whitelistedWorlds;
   private final Set<String> blacklistedWorlds;
   private final Core plugin;

   public JobsValidationStep(Core var1) {
      this.controller = var1.getJobController();
      this.plugin = var1;
      this.completionStep = new JobsCompleteStep(var1);
      Config var2 = var1.getConfig("config");
      this.whitelistedWorlds = Sets.newHashSet(var2.stringList("whitelisted-worlds"));
      this.blacklistedWorlds = Sets.newHashSet(var2.stringList("blacklisted-worlds"));
   }

   public void processCompletion(ActionExecution var1, Collection<Job> var2) {
      if (!this.isWorldServerBlocked(var1.getPlayer().getWorld().getName())) {
         for (Job var4 : var2) {
            if (this.controller.isJobActive((User)var1.getUser(), var4) && var4.getTypes().containsKey(var1.getQuestType().toLowerCase())) {
               this.jobsLock.lock();

               try {
                  this.proceed(var1, var4);
               } finally {
                  this.jobsLock.unlock();
               }
            }
         }
      }
   }

   public void proceed(ActionExecution var1, Job var2) {
      BigDecimal var3 = this.controller.getJobProgress((User)var1.getUser(), var2);
      if (this.isQuestValid(var1, var2)) {
         if (!this.isRegionQuestBlocked(var1, var2)) {
            if (!this.controller.hasProgressCooldown(((User)var1.getUser()).getUuid(), var2)) {
               Job.JobTypeInfo var4 = var2.getTypes().get(var1.getQuestType().toLowerCase());
               Variable var5 = var4.getVariable();
               ExecutableActionResult var6 = var1.getQuestResult();
               double var7 = var6.isEligible(var1.getPlayer(), var5);
               if (var7 > 0.0) {
                  this.controller.startProgressCooldown(((User)var1.getUser()).getUuid(), var2);
                  this.completionStep
                     .process(
                        var1,
                        var2,
                        var3,
                        var1.getDecimalProgress()
                           .multiply(BigDecimal.valueOf(var4.getMultiplier()))
                           .multiply(BigDecimal.valueOf(var4.getSpecialProgress().getOrDefault(var6.getEffectiveRoot(), 1.0)))
                           .multiply(BigDecimal.valueOf(var7))
                           .multiply(
                              BigDecimal.valueOf(
                                 this.plugin
                                          .getBoostersController()
                                          .calculateFinalBooster(var1.getPlayer().getUniqueId(), BoostersController.BoosterType.PROGRESS, var2)
                                       / 100.0
                                    + 1.0
                              )
                           )
                     );
               }
            }
         }
      }
   }

   public boolean isQuestPrimarilyValid(User var1, Job var2, BigDecimal var3, boolean var4) {
      return (!var4 || !this.isProgressIdentical(var2, var1, var3)) && !this.isPassTypeQuestBlocked(var2, var1);
   }

   public boolean isQuestValid(ActionExecution var1, Job var2) {
      return !this.isQuestPrimarilyValid((User)var1.getUser(), var2, var1.getDecimalProgress(), var1.shouldOverrideUpdate())
         ? false
         : !this.isWorldQuestBlocked(var2, var1.getPlayer().getWorld().getName());
   }

   private boolean isPassTypeQuestBlocked(Job var1, User var2) {
      return var1.isPremiumJob() && !var2.isPremium();
   }

   private boolean isProgressIdentical(Job var1, User var2, BigDecimal var3) {
      return this.controller.getJobProgress(var2, var1).compareTo(var3) == 0;
   }

   private boolean isWorldServerBlocked(String var1) {
      return !this.whitelistedWorlds.isEmpty() && !this.whitelistedWorlds.contains(var1) || this.blacklistedWorlds.contains(var1);
   }

   private boolean isWorldQuestBlocked(Job var1, String var2) {
      return var1.getBlacklistedWorlds().contains(var2) || !var1.getWhitelistedWorlds().isEmpty() && !var1.getWhitelistedWorlds().contains(var2);
   }

   private boolean isRegionQuestBlocked(ActionExecution var1, Job var2) {
      User var3 = (User)var1.getUser();
      if (!Bukkit.getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
         return false;
      } else if (var2.getWhitelistedRegions().isEmpty() && var2.getBlacklistedRegions().isEmpty()) {
         return false;
      } else {
         RegionContainer var4 = WorldGuard.getInstance().getPlatform().getRegionContainer();
         RegionManager var5 = var4.get(BukkitAdapter.adapt(var3.getPlayer().getWorld()));
         ApplicableRegionSet var6 = var4.createQuery().getApplicableRegions(BukkitAdapter.adapt(var3.getPlayer().getLocation()));
         if (var5 == null) {
            return false;
         } else if (!var2.getWhitelistedRegions().isEmpty()) {
            for (String var11 : var2.getWhitelistedRegions()) {
               ProtectedRegion var12 = var5.getRegion(var11);
               if (var12 != null && var6.getRegions().contains(var12)) {
                  return false;
               }
            }

            return true;
         } else {
            if (!var2.getBlacklistedRegions().isEmpty()) {
               for (String var8 : var2.getBlacklistedRegions()) {
                  ProtectedRegion var9 = var5.getRegion(var8);
                  if (var9 != null && var6.getRegions().contains(var9)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }
}
