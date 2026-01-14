package net.advancedplugins.jobs.bstats.bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;
import net.advancedplugins.jobs.bstats.MetricsBase;
import net.advancedplugins.jobs.bstats.charts.CustomChart;
import net.advancedplugins.jobs.bstats.json.JsonObjectBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Metrics {
   private final Plugin plugin;
   private final MetricsBase metricsBase;

   public Metrics(JavaPlugin var1, int var2) {
      this.plugin = var1;
      File var3 = new File(var1.getDataFolder().getParentFile(), "bStats");
      File var4 = new File(var3, "config.yml");
      YamlConfiguration var5 = YamlConfiguration.loadConfiguration(var4);
      if (!var5.isSet("serverUuid")) {
         var5.addDefault("enabled", true);
         var5.addDefault("serverUuid", UUID.randomUUID().toString());
         var5.addDefault("logFailedRequests", false);
         var5.addDefault("logSentData", false);
         var5.addDefault("logResponseStatusText", false);
         var5.options()
            .header(
               "bStats (https://bStats.org) collects some basic information for plugin authors, like how\nmany people use their plugin and their total player count. It's recommended to keep bStats\nenabled, but if you're not comfortable with this, you can turn this setting off. There is no\nperformance penalty associated with having metrics enabled, and data sent to bStats is fully\nanonymous."
            )
            .copyDefaults(true);

         try {
            var5.save(var4);
         } catch (IOException var11) {
         }
      }

      boolean var6 = var5.getBoolean("enabled", true);
      String var7 = var5.getString("serverUuid");
      boolean var8 = var5.getBoolean("logFailedRequests", false);
      boolean var9 = var5.getBoolean("logSentData", false);
      boolean var10 = var5.getBoolean("logResponseStatusText", false);
      this.metricsBase = new MetricsBase(
         "bukkit",
         var7,
         var2,
         var6,
         this::appendPlatformData,
         this::appendServiceData,
         var1x -> Bukkit.getScheduler().runTask(var1, var1x),
         var1::isEnabled,
         (var1x, var2x) -> this.plugin.getLogger().log(Level.WARNING, var1x, var2x),
         var1x -> this.plugin.getLogger().log(Level.INFO, var1x),
         var8,
         var9,
         var10
      );
   }

   public void addCustomChart(CustomChart var1) {
      this.metricsBase.addCustomChart(var1);
   }

   private void appendPlatformData(JsonObjectBuilder var1) {
      var1.appendField("playerAmount", this.getPlayerAmount());
      var1.appendField("onlineMode", Bukkit.getOnlineMode() ? 1 : 0);
      var1.appendField("bukkitVersion", Bukkit.getVersion());
      var1.appendField("bukkitName", Bukkit.getName());
      var1.appendField("javaVersion", System.getProperty("java.version"));
      var1.appendField("osName", System.getProperty("os.name"));
      var1.appendField("osArch", System.getProperty("os.arch"));
      var1.appendField("osVersion", System.getProperty("os.version"));
      var1.appendField("coreCount", Runtime.getRuntime().availableProcessors());
   }

   private void appendServiceData(JsonObjectBuilder var1) {
      var1.appendField("pluginVersion", this.plugin.getDescription().getVersion());
   }

   private int getPlayerAmount() {
      try {
         Method var1 = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers");
         return var1.getReturnType().equals(Collection.class)
            ? ((Collection)var1.invoke(Bukkit.getServer())).size()
            : ((Player[])var1.invoke(Bukkit.getServer())).length;
      } catch (Exception var2) {
         return Bukkit.getOnlinePlayers().size();
      }
   }
}
