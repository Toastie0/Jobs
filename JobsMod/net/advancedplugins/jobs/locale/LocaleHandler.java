package net.advancedplugins.jobs.locale;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.impl.utils.text.Replace;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.jobs.locale.subclass.LocaleFile;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.simplespigot.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

   public String getPrefix() {
      return this.prefix;
   }

   public void setPrefix(String var1) {
      this.prefix = this.color(this.getString(var1));
   }

   public ImmutableSet<String> getAvailableLocales() {
      return this.localeMap.keySet();
   }

   public static LocaleHandler getHandler() {
      return handler;
   }

   public String getLocale() {
      return this.locale;
   }

   public void setLocale(String var1) {
      this.locale = var1;
      if (!this.localeMap.containsKey(var1)) {
         try {
            String var2 = this.langFolder + "/" + var1 + ".yml";
            if (!new File(this.instance.getDataFolder(), var2).exists()) {
               this.instance.saveResource(var2, false);
            }
         } catch (Exception var3) {
         }

         this.localeMap = ImmutableMap.builder().putAll(this.localeMap).put(var1, new LocaleFile(var1, this.instance, "")).build();
      }
   }

   public void reload() {
      this.localeMap = ImmutableMap.builder().build();
      this.setLocale(this.locale);
   }

   public LocaleHandler readLocaleFiles(JavaPlugin var1, String var2, String var3) {
      this.langFolder = var2;

      try {
         for (File var7 : new File(var1.getDataFolder(), var2).listFiles()) {
            if (var7.getName().endsWith(".yml")) {
               String var8 = var7.getName().replace(".yml", "");
               this.localeMap = ImmutableMap.builder().putAll(this.localeMap).put(var8, new LocaleFile(var8, this.instance, var3)).build();
            }
         }

         return this;
      } catch (Exception var9) {
         return this;
      }
   }

   public LocaleHandler readLocaleFiles(JavaPlugin var1, String var2) {
      return this.readLocaleFiles(var1, var2, "");
   }

   private String color(String var1) {
      return Text.modify(var1);
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

   public void to(String var1, Replace var2, Player var3) {
      var3.sendMessage(Text.modify(this.getString(var1), var2));
   }

   public void toAll(String var1, Replace var2) {
      Bukkit.getOnlinePlayers().forEach(var3 -> var3.sendMessage(Text.modify(this.getString(var1), var2)));
   }

   public String getString(String var1) {
      String var2 = ((LocaleFile)this.localeMap.get(this.locale)).getLocaleConfig().getString(var1);
      return var2 == null ? null : this.color(var2.replace("%prefix%", this.getPrefix() != null ? this.getPrefix() : ""));
   }

   public List<String> getStringList(String var1) {
      return ((LocaleFile)this.localeMap.get(this.locale)).getLocaleConfig().getStringList(var1).stream().map(this::color).collect(Collectors.toList());
   }

   public String questCompleteMessage(Job var1, String var2) {
      return Text.modify(this.getString(this.getCompletionPath(var2)), var1x -> var1x.set("job_name", var1.getName()));
   }

   public String questBossCompleteMessage(Job var1) {
      return Text.modify(this.getString(this.getBossBarCompletionPath(var1)), var1x -> var1x.set("job_name", var1.getName()));
   }

   public String questProgressedMessage(Job var1, BigDecimal var2, BigDecimal var3, String var4) {
      return Text.modify(
         this.getString(this.getProgressionPath(var4)), var3x -> var3x.set("job_name", var1.getName()).set("progress", var2).set("required_progress", var3)
      );
   }

   public String questBossProgressedMessage(Job var1, BigDecimal var2, BigDecimal var3) {
      return Text.modify(
         this.getString(this.getBossBarProgressionPath(var1)),
         var3x -> var3x.set("job_name", var1.getName()).set("progress", var2).set("required_progress", var3)
      );
   }

   private String getCompatibleString(Config var1, String var2) {
      Object var3 = var1.get(var2);
      if (var3 instanceof String) {
         return String.valueOf(var3);
      } else {
         StringBuilder var4 = new StringBuilder();

         for (String var6 : var1.stringList(var2)) {
            var4.append(var6).append("\n");
         }

         return var4.toString();
      }
   }

   private String getCompletionPath(String var1) {
      return this.getString("quests.".concat(var1)) != null ? "quests.".concat(var1) : "quests.base-message-completed";
   }

   private String getBossBarCompletionPath(Job var1) {
      return "quests.boss-bar-message-completed";
   }

   private String getProgressionPath(String var1) {
      return this.getString("quests.".concat(var1)) != null ? "quests.".concat(var1) : "quests.base-message-progressed";
   }

   private String getBossBarProgressionPath(Job var1) {
      return "quests.boss-bar-message-progressed";
   }

   public JavaPlugin getInstance() {
      return this.instance;
   }
}
