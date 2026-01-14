package net.advancedplugins.jobs.impl.utils;

import java.io.File;
import java.io.InputStreamReader;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class FileUtil {
   private static final String UPDATED_KEY = "last-config-update";

   public static void validateFile(String var0, String var1, Plugin var2) {
      File var3 = new File(var2.getDataFolder(), var0);
      if (!var3.exists()) {
         var2.saveResource(var1, false);
      } else {
         YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
         YamlConfiguration var5 = YamlConfiguration.loadConfiguration(new InputStreamReader(var2.getResource(var1)));
         String var6 = var2.getDescription().getVersion();
         String var7 = var4.getString("last-config-update", "");
         if (!var6.equalsIgnoreCase(var7) && !var7.equalsIgnoreCase("OFF")) {
            var5.getKeys(true).forEach(var2x -> {
               if (!var4.contains(var2x)) {
                  var4.set(var2x, var5.get(var2x));
               }
            });
            var4.set("last-config-update", var6);
            var4.save(var3);
            Bukkit.getLogger().info("[" + var2.getName() + "] Updated " + var0 + " to latest version");
         }
      }
   }
}
