package net.advancedplugins.jobs.locale.subclass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.advancedplugins.jobs.impl.utils.FileUtil;
import net.advancedplugins.jobs.locale.LocaleHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LocaleFile {
   private File file;
   private final String locale;
   private FileConfiguration configuration = null;

   public LocaleFile(String var1, JavaPlugin var2, String var3) {
      this.locale = var1;
      this.saveFile(var2, var3);
   }

   public String getLocale() {
      return this.locale;
   }

   public FileConfiguration getLocaleConfig() {
      if (this.configuration == null) {
         try {
            FileInputStream var1 = new FileInputStream(this.file);
            InputStreamReader var2 = new InputStreamReader(var1, StandardCharsets.UTF_8);
            BufferedReader var3 = new BufferedReader(var2);
            this.configuration = YamlConfiguration.loadConfiguration(var3);
         } catch (Exception var4) {
            LocaleHandler.getHandler().getInstance().getLogger().warning("Failed to load locale " + this.locale);
            var4.printStackTrace();
            return null;
         }
      }

      return this.configuration;
   }

   public FileConfiguration getConfiguration() {
      return this.configuration;
   }

   private void saveFile(JavaPlugin var1, String var2) {
      File var3 = new File(var1.getDataFolder(), "lang/");
      if (!var3.isDirectory()) {
         var3.mkdirs();
      }

      String var4 = "lang/" + this.locale + ".yml";
      this.file = new File(var1.getDataFolder(), var4);
      if (!this.file.exists()) {
         InputStream var5 = var1.getResource("lang/" + this.locale + ".yml");

         try {
            this.file.createNewFile();
            byte[] var6 = new byte[var5.available()];
            var5.read(var6);
            FileOutputStream var7 = new FileOutputStream(this.file);
            var7.write(var6);
         } catch (Exception var9) {
            var9.printStackTrace();
         }
      } else if (!var2.isEmpty()) {
         try {
            FileUtil.validateFile(var4, var2, var1);
         } catch (IOException var8) {
            var8.printStackTrace();
         }
      }
   }
}
