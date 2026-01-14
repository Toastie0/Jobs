package net.advancedplugins.simplespigot.save;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SaveTask extends BukkitRunnable {
   private final Savable savable;
   private final BukkitTask bukkitTask;

   public SaveTask(Plugin var1, Savable var2, int var3) {
      this.savable = var2;
      this.bukkitTask = this.runTaskTimerAsynchronously(var1, var3, var3);
   }

   public void stop() {
      if (!this.bukkitTask.isCancelled()) {
         this.bukkitTask.cancel();
      }
   }

   public void run() {
      this.savable.save();
   }
}
