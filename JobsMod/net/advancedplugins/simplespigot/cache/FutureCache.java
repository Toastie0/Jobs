package net.advancedplugins.simplespigot.cache;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.advancedplugins.simplespigot.plugin.SimplePlugin;

public class FutureCache<K, V> extends CacheWrapper<K, V> {
   protected final SimplePlugin plugin;

   public FutureCache(SimplePlugin var1) {
      this.plugin = var1;
   }

   public CompletableFuture<Optional<V>> get(K var1) {
      return this.plugin.asyncCallback(() -> Optional.ofNullable((V)this.subCache.getIfPresent(var1)));
   }

   public CompletableFuture<V> get(K var1, Function<K, V> var2) {
      return this.plugin.asyncCallback(() -> {
         Object var3 = this.subCache.getIfPresent(var1);
         if (var3 == null) {
            this.subCache.put(var1, var2.apply(var1));
         }

         return (V)var3;
      });
   }

   public Optional<V> getSync(K var1) {
      return Optional.ofNullable((V)this.subCache.getIfPresent(var1));
   }
}
