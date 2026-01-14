package net.advancedplugins.jobs.impl.utils.locale.subclass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.advancedplugins.jobs.impl.utils.locale.LocaleHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LocaleFile {
   private File file;
   private final String locale;
   private FileConfiguration configuration = null;

   public LocaleFile(String var1, JavaPlugin var2) {
      this.locale = var1;
      this.saveFile(var2);
   }

   public void reloadLocaleConfig() {
      this.configuration = null;
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

   private void saveFile(JavaPlugin var1) {
      File var2 = new File(var1.getDataFolder(), "lang/");
      if (!var2.isDirectory()) {
         var2.mkdirs();
      }

      this.file = new File(var1.getDataFolder(), "lang/" + this.locale + ".yml");
      if (!this.file.exists()) {
         InputStream var3 = var1.getResource("lang/" + this.locale + ".yml");

         try {
            this.file.createNewFile();
            byte[] var4 = new byte[var3.available()];
            var3.read(var4);
            FileOutputStream var5 = new FileOutputStream(this.file);
            var5.write(var4);
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }
   }

   public String getLocale() {
      return this.locale;
   }

   public FileConfiguration getConfiguration() {
      return this.configuration;
   }

   public void setConfiguration(FileConfiguration var1) {
      this.configuration = var1;
   }
}
