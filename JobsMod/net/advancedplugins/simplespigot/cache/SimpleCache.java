package net.advancedplugins.simplespigot.cache;

import java.util.Optional;
import java.util.function.Function;

public class SimpleCache<K, V> extends CacheWrapper<K, V> {
   public Optional<V> get(K var1) {
      return Optional.ofNullable((V)this.subCache.getIfPresent(var1));
   }

   public V get(K var1, Function<K, V> var2) {
      Object var3 = this.subCache.getIfPresent(var1);
      if (var3 == null) {
         this.subCache.put(var1, var2.apply(var1));
      }

      return (V)var3;
   }
}
