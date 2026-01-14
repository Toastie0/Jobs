package net.advancedplugins.jobs.impl.utils;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class FoliaScheduler {
   public static final boolean isFolia = checkFolia();

   private static boolean checkFolia() {
      try {
         Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
         return true;
      } catch (ClassNotFoundException var1) {
         return false;
      }
   }

   public static FoliaScheduler.Task runTask(Plugin var0, Runnable var1) {
      if (isFolia) {
         Bukkit.getGlobalRegionScheduler().execute(var0, var1);
         return new FoliaScheduler.Task(null);
      } else {
         return new FoliaScheduler.Task(Bukkit.getScheduler().runTask(var0, var1));
      }
   }

   public static FoliaScheduler.Task runTaskLater(Plugin var0, Runnable var1, long var2) {
      return isFolia
         ? new FoliaScheduler.Task(Bukkit.getGlobalRegionScheduler().runDelayed(var0, var1x -> var1.run(), var2 < 1L ? 1L : var2))
         : new FoliaScheduler.Task(Bukkit.getScheduler().runTaskLater(var0, var1, var2));
   }

   public static FoliaScheduler.Task runTaskTimer(Plugin var0, Runnable var1, long var2, long var4) {
      return isFolia
         ? new FoliaScheduler.Task(Bukkit.getGlobalRegionScheduler().runAtFixedRate(var0, var1x -> var1.run(), var2 < 1L ? 1L : var2, var4))
         : new FoliaScheduler.Task(Bukkit.getScheduler().runTaskTimer(var0, var1, var2, var4));
   }

   public static FoliaScheduler.Task runTaskAsynchronously(Plugin var0, Runnable var1) {
      if (isFolia) {
         Bukkit.getGlobalRegionScheduler().execute(var0, var1);
         return new FoliaScheduler.Task(null);
      } else {
         return new FoliaScheduler.Task(Bukkit.getScheduler().runTaskAsynchronously(var0, var1));
      }
   }

   public static FoliaScheduler.Task runTaskLaterAsynchronously(Plugin var0, Runnable var1, long var2) {
      return isFolia
         ? new FoliaScheduler.Task(Bukkit.getGlobalRegionScheduler().runDelayed(var0, var1x -> var1.run(), var2 < 1L ? 1L : var2))
         : new FoliaScheduler.Task(Bukkit.getScheduler().runTaskLaterAsynchronously(var0, var1, var2));
   }

   public static FoliaScheduler.Task runTaskTimerAsynchronously(Plugin var0, Runnable var1, long var2, long var4) {
      return isFolia
         ? new FoliaScheduler.Task(Bukkit.getGlobalRegionScheduler().runAtFixedRate(var0, var1x -> var1.run(), var2 < 1L ? 1L : var2, var4))
         : new FoliaScheduler.Task(Bukkit.getScheduler().runTaskTimerAsynchronously(var0, var1, var2, var4));
   }

   public static void runTaskTimerAsynchronously(Plugin var0, Consumer<FoliaScheduler.Task> var1, long var2, long var4) {
      if (isFolia) {
         Bukkit.getGlobalRegionScheduler().runAtFixedRate(var0, var1x -> var1.accept(new FoliaScheduler.Task(var1x)), var2 < 1L ? 1L : var2, var4);
      } else {
         Bukkit.getScheduler().runTaskTimerAsynchronously(var0, var1x -> var1.accept(new FoliaScheduler.Task(var1x)), var2, var4);
      }
   }

   public static boolean isFolia() {
      return isFolia;
   }

   public static void cancelAll(Plugin var0) {
      if (isFolia) {
         Bukkit.getGlobalRegionScheduler().cancelTasks(var0);
      } else {
         Bukkit.getScheduler().cancelTasks(var0);
      }
   }

   public static class Task {
      private Object foliaTask;
      private BukkitTask bukkitTask;

      public Task(Object var1) {
         this.foliaTask = var1;
      }

      public Task(BukkitTask var1) {
         this.bukkitTask = var1;
      }

      public void cancel() {
         if (this.foliaTask != null || this.bukkitTask != null) {
            if (this.foliaTask != null) {
               ((ScheduledTask)this.foliaTask).cancel();
            } else {
               this.bukkitTask.cancel();
            }
         }
      }

      public int getTaskId() {
         if (this.foliaTask == null && this.bukkitTask == null) {
            return -1;
         } else {
            return this.foliaTask != null ? -1 : this.bukkitTask.getTaskId();
         }
      }
   }
}
