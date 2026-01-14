package net.advancedplugins.jobs.impl.utils;

public class SchedulerUtils {
   public static int runTaskLater(Runnable var0, long var1) {
      return FoliaScheduler.runTaskLater(ASManager.getInstance(), var0, var1).getTaskId();
   }

   public static int runTaskLater(Runnable var0) {
      return runTaskLater(var0, 1L);
   }

   public static int runTaskTimer(Runnable var0, long var1, long var3) {
      return FoliaScheduler.runTaskTimer(ASManager.getInstance(), var0, var1, var3).getTaskId();
   }

   public static int runTaskTimerAsync(Runnable var0, long var1, long var3) {
      return FoliaScheduler.runTaskTimerAsynchronously(ASManager.getInstance(), var0, var1, var3).getTaskId();
   }

   public static int runTask(Runnable var0) {
      return FoliaScheduler.runTask(ASManager.getInstance(), var0).getTaskId();
   }

   public static int runTaskAsync(Runnable var0) {
      return FoliaScheduler.runTaskAsynchronously(ASManager.getInstance(), var0).getTaskId();
   }
}
