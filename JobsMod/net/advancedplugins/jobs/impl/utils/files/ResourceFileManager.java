package net.advancedplugins.jobs.impl.utils.files;

import java.io.File;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.bukkit.plugin.java.JavaPlugin;

public class ResourceFileManager {
   public static void saveAllResources(JavaPlugin var0, String var1, String var2) {
      try {
         File var3 = new File(var0.getDataFolder(), var1);
         if (!var3.exists()) {
            var3.mkdirs();
         }

         CodeSource var4 = var0.getClass().getProtectionDomain().getCodeSource();
         if (var4 == null) {
            var0.getLogger().warning("Could not get CodeSource for plugin");
            return;
         }

         try (ZipInputStream var5 = new ZipInputStream(var4.getLocation().openStream())) {
            int var7 = 0;

            ZipEntry var6;
            while ((var6 = var5.getNextEntry()) != null) {
               String var8 = var6.getName();
               if (var8.startsWith(var1 + "/")) {
                  String var9 = var8.substring(var1.length() + 1);
                  if (var2 == null || var8.endsWith(var2)) {
                     File var10 = new File(var3, var9);
                     if (!var10.exists() && !var10.isDirectory()) {
                        if (!var8.endsWith("/")) {
                           var0.saveResource(var8, false);
                        }

                        var10.getParentFile().mkdirs();
                        var7++;
                     }
                  }
               }
            }

            if (var7 == 0) {
            }
         }
      } catch (Exception var13) {
         var0.getLogger().severe("Failed to save resources from " + var1 + ": " + var13.getMessage());
      }
   }

   public static void saveAllResourceFolders(JavaPlugin var0, String[] var1, String var2) {
      for (String var6 : var1) {
         try {
            saveAllResources(var0, var6, var2);
         } catch (Exception var8) {
            var0.getLogger().warning("Skipping folder '" + var6 + "' due to error: " + var8.getMessage());
         }
      }
   }
}
