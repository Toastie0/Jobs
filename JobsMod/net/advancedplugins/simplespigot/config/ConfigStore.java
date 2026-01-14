package net.advancedplugins.simplespigot.config;

import com.google.common.collect.Maps;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.bukkit.plugin.Plugin;

public class ConfigStore {
   private final Plugin plugin;
   private Map<String, Config> configMap = Maps.newHashMap();
   private Map<String, String> commons = Maps.newHashMap();

   public ConfigStore(Plugin var1) {
      this.plugin = var1;
   }

   public Map<String, String> commons() {
      return this.commons;
   }

   public Config getConfig(String var1) {
      return this.configMap.get(var1);
   }

   public ConfigStore config(String var1, BiFunction<Path, String, Path> var2, boolean var3) {
      this.configMap.put(var1, new Config(this.plugin, var2x -> Paths.get(((Path)var2.apply(var2x, var1)).toString().concat(".yml")), var3));
      return this;
   }

   public ConfigStore config(String var1, BiFunction<Path, String, Path> var2, boolean var3, boolean var4) {
      this.configMap.put(var1, new Config(this.plugin, var2x -> Paths.get(((Path)var2.apply(var2x, var1)).toString().concat(".yml")), var3, var4));
      return this;
   }

   public ConfigStore common(String var1, String var2, Function<Config, String> var3) {
      this.commons.put(var1, (String)var3.apply(this.getConfig(var2)));
      return this;
   }

   public void forceReload(String var1) {
      Config var2 = this.getConfig(var1);
      if (var2 != null) {
         var2.reload();
      }
   }

   public void forceReload(String... var1) {
      for (String var5 : var1) {
         this.forceReload(var5);
      }
   }

   public void reloadReloadableConfigs() {
      for (Config var2 : this.configMap.values()) {
         if (var2.isReloadable()) {
            var2.reload();
         }
      }
   }
}
