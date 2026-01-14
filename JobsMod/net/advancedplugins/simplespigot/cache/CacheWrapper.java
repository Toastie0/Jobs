package net.advancedplugins.simplespigot.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Collection;
import java.util.Set;
import java.util.function.UnaryOperator;

public class CacheWrapper<K, V> {
   protected final Cache<K, V> subCache;

   public CacheWrapper() {
      this.subCache = CacheBuilder.newBuilder().build();
   }

   public CacheWrapper(UnaryOperator<CacheBuilder<Object, Object>> var1) {
      this.subCache = var1.apply(CacheBuilder.newBuilder()).build();
   }

   public Cache<K, V> getSubCache() {
      return this.subCache;
   }

   public V set(K var1, V var2) {
      this.subCache.put(var1, var2);
      return (V)var2;
   }

   public void invalidate(K var1) {
      this.subCache.invalidate(var1);
   }

   public Set<K> keySet() {
      return this.subCache.asMap().keySet();
   }

   public Collection<V> values() {
      return this.subCache.asMap().values();
   }

   public boolean hasKey(K var1) {
      return this.subCache.asMap().containsKey(var1);
   }

   public boolean hasValue(V var1) {
      return this.subCache.asMap().containsValue(var1);
   }
}
