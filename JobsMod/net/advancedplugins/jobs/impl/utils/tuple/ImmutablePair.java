package net.advancedplugins.jobs.impl.utils.tuple;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

@Immutable
@ThreadSafe
public class ImmutablePair<K, V> {
   private final K key;
   private final V value;

   public ImmutablePair(K var1, V var2) {
      this.key = (K)var1;
      this.value = (V)var2;
   }

   public static <S, U> ImmutablePair<S, U> of(S var0, U var1) {
      return (ImmutablePair<S, U>)(new ImmutablePair<>(var0, var1));
   }

   public K getKey() {
      return this.key;
   }

   public V getValue() {
      return this.value;
   }
}
