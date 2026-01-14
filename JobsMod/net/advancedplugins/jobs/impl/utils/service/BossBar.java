package net.advancedplugins.jobs.impl.utils.service;

import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BossBar {
   private final org.bukkit.boss.BossBar bossBar;
   private final JavaPlugin plugin;
   private FoliaScheduler.Task bukkitTask;

   public BossBar(JavaPlugin var1, Player var2, String var3, BarColor var4, BarStyle var5) {
      this.plugin = var1;
      this.bossBar = Bukkit.createBossBar(var3, var4, var5, new BarFlag[0]);
      this.bossBar.setVisible(false);
      this.bossBar.addPlayer(var2);
   }

   public void show() {
      this.bossBar.setVisible(true);
   }

   public void hide() {
      this.bossBar.setVisible(false);
   }

   public void setTitle(String var1) {
      this.bossBar.setTitle(var1);
   }

   public void setProgress(double var1) {
      this.bossBar.setProgress(var1 / 100.0);
   }

   public void schedule(int var1) {
      this.endDisplay();
      this.show();
      this.bukkitTask = FoliaScheduler.runTaskLater(this.plugin, this::endDisplay, var1 * 20);
   }

   public void endDisplay() {
      if (this.bukkitTask != null) {
         this.bukkitTask.cancel();
         this.bukkitTask = null;
         this.hide();
      }
   }
}
