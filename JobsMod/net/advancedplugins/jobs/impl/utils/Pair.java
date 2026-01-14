package net.advancedplugins.jobs.impl.utils;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class Pair<T, V> {
   private T key;
   private V value;

   public static <S, U> Pair<S, U> of(S var0, U var1) {
      return (Pair<S, U>)(new Pair<>(var0, var1));
   }

   public Pair(T var1, V var2) {
      this.key = (T)var1;
      this.value = (V)var2;
   }

   public T getKey() {
      return this.key;
   }

   public V getValue() {
      return this.value;
   }

   public void setKey(T var1) {
      this.key = (T)var1;
   }

   public void setValue(V var1) {
      this.value = (V)var1;
   }
}
