package net.advancedplugins.jobs.util.bossbar;

import net.advancedplugins.jobs.Core;
import net.advancedplugins.simplespigot.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class NewBossBar implements BossBar {
   private final org.bukkit.boss.BossBar bossBar;
   private final Core plugin;
   private BukkitTask bukkitTask;

   public NewBossBar(Core var1, Player var2, String var3) {
      this.plugin = var1;
      Config var4 = var1.getConfig("config");

      BarColor var5;
      try {
         var5 = BarColor.valueOf(var4.string("boss-bar.color"));
      } catch (Exception var9) {
         var5 = BarColor.PINK;
         System.err.println("Wrong boss bar color in AdvancedJobs!");
      }

      BarStyle var6;
      try {
         var6 = BarStyle.valueOf(var4.string("boss-bar.style"));
      } catch (Exception var8) {
         var6 = BarStyle.SOLID;
         System.err.println("Wrong boss bar style in AdvancedJobs!");
      }

      this.bossBar = Bukkit.createBossBar(var3, var5, var6, new BarFlag[0]);
      this.bossBar.setVisible(false);
      this.bossBar.addPlayer(var2);
   }

   @Override
   public void show() {
      this.bossBar.setVisible(true);
   }

   @Override
   public void hide() {
      this.bossBar.setVisible(false);
   }

   @Override
   public void setTitle(String var1) {
      this.bossBar.setTitle(var1);
   }

   @Override
   public void setProgress(double var1) {
      this.bossBar.setProgress(var1 / 100.0);
   }

   @Override
   public void schedule(int var1) {
      this.endDisplay();
      this.show();
      this.bukkitTask = Bukkit.getScheduler().runTaskLater(this.plugin, this::endDisplay, var1 * 20);
   }

   @Override
   public void endDisplay() {
      if (this.bukkitTask != null) {
         this.bukkitTask.cancel();
         this.bukkitTask = null;
         this.hide();
      }
   }

   @Override
   public void updatePlayer(Player var1) {
      this.bossBar.removeAll();
      this.bossBar.addPlayer(var1);
   }
}
