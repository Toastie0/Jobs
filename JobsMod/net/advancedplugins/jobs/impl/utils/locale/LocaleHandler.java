package net.advancedplugins.jobs.impl.utils.locale;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.impl.utils.files.ResourceFileManager;
import net.advancedplugins.jobs.impl.utils.locale.subclass.LocaleFile;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.bukkit.plugin.java.JavaPlugin;

public class LocaleHandler {
   private String locale;
   private String langFolder;
   private final JavaPlugin instance;
   private ImmutableMap<String, LocaleFile> localeMap;
   private static LocaleHandler handler = null;
   private String prefix;

   public LocaleHandler(JavaPlugin var1) {
      this.instance = var1;
      handler = this;
      this.localeMap = ImmutableMap.builder().build();
   }

   public void setPrefix(String var1) {
      this.prefix = this.color(this.getString(var1));
   }

   public ImmutableSet<String> getAvailableLocales() {
      return this.localeMap.keySet();
   }

   public void setLocale(String var1) {
      this.locale = var1;
      if (!this.localeMap.containsKey(var1)) {
         try {
            this.instance.saveResource(this.langFolder + "/" + var1 + ".yml", true);
         } catch (Exception var3) {
         }

         this.localeMap = ImmutableMap.builder().putAll(this.localeMap).put(var1, new LocaleFile(var1, this.instance)).build();
      }
   }

   public void saveAllLocaleFiles(JavaPlugin var1) {
      ResourceFileManager.saveAllResources(var1, "lang", null);
   }

   public LocaleHandler readLocaleFiles(JavaPlugin var1, String var2) {
      this.langFolder = var2;

      try {
         for (File var6 : new File(var1.getDataFolder(), var2).listFiles()) {
            if (var6.getName().endsWith(".yml")) {
               String var7 = var6.getName().replace(".yml", "");
               this.localeMap = ImmutableMap.builder().putAll(this.localeMap).put(var7, new LocaleFile(var7, this.instance)).build();
            }
         }

         return this;
      } catch (Exception var8) {
         return this;
      }
   }

   private String color(String var1) {
      return Text.modify(var1.replace("%prefix%", this.getPrefix() != null ? this.getPrefix() : ""));
   }

   public LocaleFile getFile() {
      return (LocaleFile)this.localeMap.get(this.locale);
   }

   public String getString(String var1, String var2) {
      return this.color(
         ((LocaleFile)this.localeMap.get(this.locale))
            .getLocaleConfig()
            .getString(var1, var2)
            .replace("%prefix%", this.getPrefix() != null ? this.getPrefix() : "")
      );
   }

   public String getString(String var1) {
      return this.color(
         ((LocaleFile)this.localeMap.get(this.locale)).getLocaleConfig().getString(var1).replace("%prefix%", this.getPrefix() != null ? this.getPrefix() : "")
      );
   }

   public List<String> getStringList(String var1) {
      return ((LocaleFile)this.localeMap.get(this.locale)).getLocaleConfig().getStringList(var1).stream().map(this::color).collect(Collectors.toList());
   }

   public String getLocale() {
      return this.locale;
   }

   public JavaPlugin getInstance() {
      return this.instance;
   }

   public static LocaleHandler getHandler() {
      return handler;
   }

   public String getPrefix() {
      return this.prefix;
   }
}
