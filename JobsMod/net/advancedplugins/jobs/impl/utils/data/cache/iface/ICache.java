package net.advancedplugins.jobs.impl.utils.data.cache.iface;

import java.util.Collection;
import java.util.Set;
import java.util.Map.Entry;

public interface ICache<K, V extends ICached<K>> {
   V get(K var1);

   V getOrNull(K var1);

   Set<K> keySet();

   Collection<V> values();

   Set<Entry<K, V>> entrySet();

   void set(K var1, V var2);

   void invalidate(K var1);

   void invalidateAll();

   boolean contains(K var1);
}
