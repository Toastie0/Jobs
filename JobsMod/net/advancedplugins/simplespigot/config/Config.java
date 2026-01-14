package net.advancedplugins.simplespigot.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import net.advancedplugins.jobs.impl.utils.FileUtil;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {
   private final Plugin plugin;
   private final File file;
   private final boolean reloadable;
   private YamlConfiguration configuration;
   private Map<String, Object> valueMap;
   private Set<String> enduringKeys;

   public Config(Plugin var1, UnaryOperator<Path> var2, boolean var3, boolean var4, String... var5) {
      this.plugin = var1;
      this.file = var2.apply(var1.getDataFolder().toPath()).toFile();
      this.reloadable = var3;
      this.enduringKeys = Sets.newHashSet(var5);
      String var6 = var2.apply(Paths.get("")).toString();
      this.createIfAbsent(var6);
      if (var4) {
         this.validate(var6);
      }

      this.reload();
      this.load();
   }

   public Config(Plugin var1, UnaryOperator<Path> var2, boolean var3, String... var4) {
      this(var1, var2, var3, false, var4);
   }

   public Config(Plugin var1, File var2, boolean var3, String... var4) {
      this.plugin = var1;
      this.file = var2;
      this.reloadable = var3;
      this.enduringKeys = Sets.newHashSet(var4);
      this.reload();
      this.load();
   }

   public YamlConfiguration getConfiguration() {
      return this.configuration;
   }

   public boolean isReloadable() {
      return this.reloadable;
   }

   public boolean has(String var1) {
      return this.configuration.contains(var1);
   }

   public String string(String var1) {
      return Text.modify((String)this.get(var1));
   }

   public String string(String var1, String var2) {
      String var3 = (String)this.get(var1);
      if (var3 == null) {
         var3 = var2;
      }

      return Text.modify(var3);
   }

   public String forcedString(String var1) {
      return Text.modify(String.valueOf(this.get(var1)));
   }

   public boolean bool(String var1) {
      Object var2 = this.get(var1);
      return var2 instanceof Boolean ? (Boolean)var2 : false;
   }

   public boolean boolOr(String var1, boolean var2) {
      Object var3 = this.get(var1);
      if (var3 == null) {
         return var2;
      } else {
         return var3 instanceof Boolean ? (Boolean)var3 : false;
      }
   }

   public int integer(String var1) {
      Object var2 = this.get(var1);
      return var2 instanceof Number ? ((Number)var2).intValue() : -1;
   }

   public double doubl(String var1) {
      Object var2 = this.get(var1);
      return var2 instanceof Number ? ((Number)var2).doubleValue() : -1.0;
   }

   public List<String> stringList(String var1) {
      Object var2 = this.get(var1);
      return (List<String>)(var2 instanceof List ? (List)var2 : Lists.newArrayList());
   }

   public <T> List<T> list(String var1) {
      Object var2 = this.get(var1);
      return (List<T>)(var2 instanceof List ? (List)var2 : Lists.newArrayList());
   }

   public Set<String> keys(String var1, boolean var2) {
      ConfigurationSection var3 = this.configuration.getConfigurationSection(var1);
      return (Set<String>)(var3 == null ? new HashSet<>() : var3.getKeys(var2));
   }

   public Object get(String var1) {
      return this.valueMap.getOrDefault(var1, null);
   }

   public synchronized void load() {
      boolean var1 = true;
      if (this.valueMap == null) {
         this.valueMap = Maps.newHashMap();
         var1 = false;
      }

      for (String var3 : this.configuration.getKeys(true)) {
         if (!var1 || !this.enduringKeys.contains(var3)) {
            this.valueMap.put(var3, this.configuration.get(var3));
         }
      }
   }

   public void reload() {
      this.configuration = YamlConfiguration.loadConfiguration(this.file);
      this.load();
   }

   private void createIfAbsent(String var1) {
      if (!this.file.exists()) {
         this.plugin.getDataFolder().mkdirs();
         this.plugin.saveResource(var1, false);
      }
   }

   private void validate(String var1) {
      try {
         FileUtil.validateFile(var1, var1, this.plugin);
      } catch (IOException var3) {
         var3.printStackTrace();
      }
   }

   public File getFile() {
      return this.file;
   }
}
