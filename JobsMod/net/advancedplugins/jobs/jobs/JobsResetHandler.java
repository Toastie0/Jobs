package net.advancedplugins.jobs.jobs;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.cache.JobCache;
import net.advancedplugins.jobs.cache.UserCache;
import net.advancedplugins.jobs.events.JobsResetEvent;
import net.advancedplugins.jobs.locale.LocaleHandler;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.validator.Validator;
import net.advancedplugins.simplespigot.config.Config;
import net.advancedplugins.simplespigot.service.simple.Simple;
import net.advancedplugins.simplespigot.service.simple.services.TimeService;
import net.advancedplugins.simplespigot.storage.storage.Storage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class JobsResetHandler {
   private final Core plugin;
   private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
   private final int amount;
   private final TimeService.SimpleTimeFormat timeFormat;
   private final ZoneId timeZone;
   private BukkitRunnable resetTask;
   private ZonedDateTime resetTime;
   protected final JobCache jobCache;
   protected final UserCache userCache;
   protected List<Job> currentFreeJobs;
   protected List<Job> currentPremiumJobs;
   protected List<Job> availableFreeJobs;
   protected List<Job> availablePremiumJobs;
   protected boolean resetEnabled;
   protected Storage<JobsResetHandler> storage;
   protected boolean useBungee;
   protected boolean cancel = false;

   public JobsResetHandler(Core var1, List<Job> var2, List<Job> var3) {
      Config var4 = var1.getConfig("config");
      this.plugin = var1;
      this.timeFormat = this.createTimeFormat(var1.getLocale());
      this.jobCache = var1.getJobCache();
      this.userCache = var1.getUserCache();
      this.amount = var4.integer("jobs-reset.amount");
      this.resetEnabled = var4.bool("jobs-reset.enabled");
      this.timeZone = ZoneId.of(var4.string("jobs-reset.reset-time.time-zone"));
      this.resetTime = this.parseTime(this.now().withSecond(0), var4.string("jobs-reset.reset-time.time"));
      this.currentFreeJobs = var2;
      this.currentPremiumJobs = var3;
      this.storage = var1.getResetStorage();
      this.useBungee = var4.bool("current-jobs-use-bungee");
      this.updateAvailableJobs();
      if (!var2.isEmpty()) {
         this.currentFreeJobs = var2.stream().filter(Objects::nonNull).filter(var0 -> Validator.validateJob(var0.getId())).collect(Collectors.toList());
      }

      if (!var3.isEmpty()) {
         this.currentPremiumJobs = var3.stream().filter(Objects::nonNull).filter(var0 -> Validator.validateJob(var0.getId())).collect(Collectors.toList());
      }
   }

   private void updateAvailableJobs() {
      this.availableFreeJobs = new ArrayList<>(this.jobCache.getAllJobs().stream().filter(var0 -> !var0.isPremiumJob()).collect(Collectors.toList()));
      this.availablePremiumJobs = new ArrayList<>(this.jobCache.getAllJobs().stream().filter(Job::isPremiumJob).collect(Collectors.toList()));
   }

   public void start() {
      if (!this.resetEnabled) {
         this.currentFreeJobs = new LinkedList<>(this.availableFreeJobs);
         this.currentPremiumJobs = new LinkedList<>(this.availablePremiumJobs);
      } else {
         if (this.currentFreeJobs.isEmpty() || this.currentPremiumJobs.isEmpty()) {
            if (this.useBungee) {
               System.out.println("Waiting for current jobs...");
               Bukkit.getScheduler().runTaskLater(this.plugin, this.plugin::reloadJobs, 100L);
               return;
            }

            this.reset();
         }

         if (this.useBungee) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, var1 -> {
               if (this.cancel) {
                  var1.cancel();
               } else {
                  this.refreshBungee();
               }
            }, 600L, 600L);
         } else {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
               this.reset();
               this.start();
               Bukkit.getOnlinePlayers().forEach(var1 -> this.tryResetForUser(var1.getUniqueId()));
            }, this.timeToResetSeconds() * 20L);
         }
      }
   }

   private void tryResetForUser(UUID var1) {
      Player var2 = Bukkit.getPlayer(var1);
      if (var2 != null && var2.isOnline()) {
         var2.sendMessage(this.plugin.getLocale().getString("jobs-reset"));
      }
   }

   public void stop() {
      if (this.resetTask != null) {
         this.resetTask.cancel();
      }
   }

   public boolean reset() {
      if (this.useBungee) {
         System.out.println("Tried to reset jobs on bungee server. It is only possible on main server");
         return false;
      } else {
         this.updateAvailableJobs();
         this.userCache.asyncModifyAll(var1 -> var1.getJobStore().asMap().forEach((var1x, var2) -> {
            var2.setActive(false);
            if (this.plugin.getConfig("config").boolOr("jobs-reset.reset-progress", true)) {
               var2.setProgress(BigDecimal.ZERO);
            }
         }));
         this.currentFreeJobs = new LinkedList<>();
         this.currentPremiumJobs = new LinkedList<>();
         this.shuffleJobs(this.availableFreeJobs, this.currentFreeJobs);
         this.shuffleJobs(this.availablePremiumJobs, this.currentPremiumJobs);
         this.storage.save("daily-data", this);
         this.plugin.runSync(() -> Bukkit.getPluginManager().callEvent(new JobsResetEvent()));
         return true;
      }
   }

   private void shuffleJobs(List<Job> var1, List<Job> var2) {
      int var3 = Math.min(var1.size(), this.amount);
      Collections.shuffle(var1);
      int var4 = 0;

      for (Job var6 : var1) {
         if (var4 >= var3) {
            break;
         }

         var2.add(var6);
         var4++;
      }
   }

   public long timeToResetSeconds() {
      if (this.now().until(this.resetTime, ChronoUnit.SECONDS) <= 0L) {
         this.resetTime = this.resetTime.plusDays(1L);
      }

      return this.now().until(this.resetTime, ChronoUnit.SECONDS);
   }

   private ZonedDateTime now() {
      return ZonedDateTime.now().withZoneSameInstant(this.timeZone);
   }

   private ZonedDateTime parseTime(ZonedDateTime var1, String var2) {
      String[] var3 = var2.split(":");
      return var1.withHour(StringUtils.isNumeric(var3[0]) ? Integer.parseInt(var3[0]) : 0)
         .withMinute(var3.length > 1 ? (StringUtils.isNumeric(var3[1]) ? Integer.parseInt(var3[1]) : 0) : 0);
   }

   private TimeService.SimpleTimeFormat createTimeFormat(LocaleHandler var1) {
      return new TimeService.SimpleTimeFormat(
         null,
         var1.getString("jobs-time-format.with-months-weeks"),
         var1.getString("jobs-time-format.with-weeks-days"),
         var1.getString("jobs-time-format.with-days-hours"),
         var1.getString("jobs-time-format.with-hours-minutes"),
         var1.getString("jobs-time-format.with-minutes-seconds"),
         var1.getString("jobs-time-format.with-seconds")
      );
   }

   public String asString() {
      TimeService var1 = Simple.time();
      long var2 = this.timeToResetSeconds();
      return this.timeFormat == null ? var1.format(TimeUnit.SECONDS, var2) : var1.format(this.timeFormat, TimeUnit.SECONDS, var2);
   }

   public boolean refreshBungee() {
      if (!this.useBungee) {
         return false;
      } else {
         JobsResetHandler var1 = this.storage.load("daily-data");
         if (var1.equals(this)) {
            return false;
         } else {
            this.plugin.reloadJobs();
            this.userCache.asyncModifyMultiple(var1x -> var1x.getJobStore().asMap().forEach((var1xx, var2) -> {
               var2.setActive(false);
               if (this.plugin.getConfig("config").boolOr("jobs-reset.reset-progress", true)) {
                  var2.setProgress(BigDecimal.ZERO);
               }
            }), Bukkit.getOnlinePlayers().stream().<UUID>map(OfflinePlayer::getUniqueId).collect(Collectors.toSet()));
            Bukkit.getOnlinePlayers().forEach(var1x -> this.tryResetForUser(var1x.getUniqueId()));
            this.cancel = true;
            return true;
         }
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         JobsResetHandler var2 = (JobsResetHandler)var1;
         return Objects.equals(this.currentFreeJobs, var2.currentFreeJobs) && Objects.equals(this.currentPremiumJobs, var2.currentPremiumJobs);
      } else {
         return false;
      }
   }

   public Core getPlugin() {
      return this.plugin;
   }

   public ScheduledExecutorService getExecutorService() {
      return this.executorService;
   }

   public int getAmount() {
      return this.amount;
   }

   public TimeService.SimpleTimeFormat getTimeFormat() {
      return this.timeFormat;
   }

   public ZoneId getTimeZone() {
      return this.timeZone;
   }

   public BukkitRunnable getResetTask() {
      return this.resetTask;
   }

   public ZonedDateTime getResetTime() {
      return this.resetTime;
   }

   public JobCache getJobCache() {
      return this.jobCache;
   }

   public UserCache getUserCache() {
      return this.userCache;
   }

   public List<Job> getAvailableFreeJobs() {
      return this.availableFreeJobs;
   }

   public List<Job> getAvailablePremiumJobs() {
      return this.availablePremiumJobs;
   }

   public boolean isResetEnabled() {
      return this.resetEnabled;
   }

   public Storage<JobsResetHandler> getStorage() {
      return this.storage;
   }

   public boolean isUseBungee() {
      return this.useBungee;
   }

   public boolean isCancel() {
      return this.cancel;
   }

   public void setResetTask(BukkitRunnable var1) {
      this.resetTask = var1;
   }

   public void setResetTime(ZonedDateTime var1) {
      this.resetTime = var1;
   }

   public void setCurrentFreeJobs(List<Job> var1) {
      this.currentFreeJobs = var1;
   }

   public void setCurrentPremiumJobs(List<Job> var1) {
      this.currentPremiumJobs = var1;
   }

   public void setAvailableFreeJobs(List<Job> var1) {
      this.availableFreeJobs = var1;
   }

   public void setAvailablePremiumJobs(List<Job> var1) {
      this.availablePremiumJobs = var1;
   }

   public void setResetEnabled(boolean var1) {
      this.resetEnabled = var1;
   }

   public void setStorage(Storage<JobsResetHandler> var1) {
      this.storage = var1;
   }

   public void setUseBungee(boolean var1) {
      this.useBungee = var1;
   }

   public void setCancel(boolean var1) {
      this.cancel = var1;
   }

   public List<Job> getCurrentFreeJobs() {
      return this.currentFreeJobs;
   }

   public List<Job> getCurrentPremiumJobs() {
      return this.currentPremiumJobs;
   }
}
