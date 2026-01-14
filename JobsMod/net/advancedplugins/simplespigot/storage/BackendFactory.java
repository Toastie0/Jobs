package net.advancedplugins.simplespigot.storage;

import com.google.common.collect.Maps;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.advancedplugins.simplespigot.plugin.SimplePlugin;
import net.advancedplugins.simplespigot.storage.backends.FlatBackend;
import net.advancedplugins.simplespigot.storage.backends.MongoBackend;
import net.advancedplugins.simplespigot.storage.backends.mysql.MySqlBackend;

public class BackendFactory {
   private final SimplePlugin plugin;
   private Map<String, BiFunction<UnaryOperator<Path>, String, Backend>> backendMap = Maps.newConcurrentMap();

   public BackendFactory(SimplePlugin var1) {
      this.plugin = var1;
      this.addBackend("mysql", var1x -> new MySqlBackend(this.plugin, var1x)).addBackend("mongodb", var0 -> new MongoBackend());
   }

   public Backend create(String var1, UnaryOperator<Path> var2, String var3, String var4) {
      for (Entry var6 : this.backendMap.entrySet()) {
         if (((String)var6.getKey()).toLowerCase().equalsIgnoreCase(var1)) {
            return (Backend)((BiFunction)var6.getValue()).apply(var2, var4);
         }
      }

      return new FlatBackend(var2.apply(this.plugin.getDataFolder().toPath().toAbsolutePath()).resolve(var3));
   }

   public Backend create(String var1, String var2) {
      return this.create(var1, var0 -> var0, var2, "");
   }

   public Backend create(String var1, UnaryOperator<Path> var2) {
      return this.create(var1, var2, "", "");
   }

   public BackendFactory addBackend(String var1, BiFunction<UnaryOperator<Path>, String, Backend> var2) {
      this.backendMap.put(var1, var2);
      return this;
   }

   public BackendFactory addBackend(String var1, Function<String, Backend> var2) {
      this.addBackend(var1, (var1x, var2x) -> (Backend)var2.apply(var2x));
      return this;
   }

   public BackendFactory addBackendAsPath(String var1, Function<UnaryOperator<Path>, Backend> var2) {
      this.addBackend(var1, (var1x, var2x) -> (Backend)var2.apply(var1x));
      return this;
   }
}
