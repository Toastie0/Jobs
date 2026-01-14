package net.advancedplugins.jobs.impl.utils.plugin;

import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class UpdateChecker {
   private static final String UPDATE_URL = "https://advancedplugins.net/api/v1/getVersion.php?plugin=";

   public static void checkUpdate(JavaPlugin var0) {
      FoliaScheduler.runTaskLaterAsynchronously(
         var0,
         () -> {
            String var1 = var0.getDescription().getVersion();
            String var2 = var0.getDescription().getName();

            String var3;
            try {
               var3 = ASManager.fetchJsonFromUrl("https://advancedplugins.net/api/v1/getVersion.php?plugin=" + var2);
            } catch (Exception var5) {
               var5.printStackTrace();
               return;
            }

            if (!var3.isEmpty() && !isLatest(var1, var3)) {
               Bukkit.getConsoleSender()
                  .sendMessage(
                     ChatColor.translateAlternateColorCodes(
                        '&', "&7[" + var2 + "] &eYou're using an outdated version of " + var2 + ". A new version is available: &f" + var3
                     )
                  );
               Bukkit.getConsoleSender()
                  .sendMessage(
                     ChatColor.translateAlternateColorCodes(
                        '&',
                        "&7["
                           + var2
                           + "] &7Keep your Advanced plugins up to date automatically with &bMintServers&7 Unlimited Hosting: &bhttps://mintservers.com/"
                     )
                  );
            }
         },
         20L
      );
   }

   private static boolean isLatest(String var0, String var1) {
      String[] var2 = var0.split("\\.");
      String[] var3 = var1.split("\\.");

      for (int var4 = 0; var4 < Math.min(var2.length, var3.length); var4++) {
         int var5 = Integer.parseInt(var2[var4]);
         int var6 = Integer.parseInt(var3[var4]);
         if (var5 < var6) {
            return false;
         }

         if (var5 > var6) {
            return true;
         }
      }

      return var2.length >= var3.length;
   }
}
