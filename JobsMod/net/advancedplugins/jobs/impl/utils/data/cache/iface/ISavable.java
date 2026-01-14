package net.advancedplugins.jobs.impl.utils.data.cache.iface;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public interface ISavable<K, V extends ICached<K>> {
   V load(K var1);

   Collection<V> loadAll();

   Collection<V> loadAll(boolean var1);

   void modify(K var1, Consumer<V> var2);

   void modifyMultiple(Set<K> var1, Consumer<V> var2);

   void modifyAll(Consumer<V> var1);

   void loopAll(Consumer<V> var1);

   Collection<V> loopAll();

   void save(K var1);

   void saveValue(V var1);

   void saveAll();

   V create(K var1, V var2);

   V create(V var1);

   void remove(K var1);

   void removeIf(BiPredicate<K, V> var1);

   void removeAll();

   boolean exists(K var1);
}
