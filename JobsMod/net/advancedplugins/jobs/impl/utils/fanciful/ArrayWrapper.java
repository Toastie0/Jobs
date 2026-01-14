package net.advancedplugins.jobs.impl.utils.fanciful;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.Validate;

public final class ArrayWrapper<E> {
   private E[] _array;

   public ArrayWrapper(E... var1) {
      this.setArray((E[])var1);
   }

   public static <T> T[] toArray(Iterable<? extends T> var0, Class<T> var1) {
      int var2 = -1;
      if (var0 instanceof Collection var3) {
         var2 = var3.size();
      }

      if (var2 < 0) {
         var2 = 0;

         for (Object var4 : var0) {
            var2++;
         }
      }

      Object[] var8 = (Object[])Array.newInstance(var1, var2);
      int var9 = 0;

      for (Object var6 : var0) {
         var8[var9++] = var6;
      }

      return (T[])var8;
   }

   public E[] getArray() {
      return this._array;
   }

   private void setArray(E[] var1) {
      Validate.notNull(var1, "The array must not be null.", new Object[0]);
      this._array = (E[])var1;
   }

   @Override
   public boolean equals(Object var1) {
      return !(var1 instanceof ArrayWrapper) ? false : Arrays.equals(this._array, ((ArrayWrapper)var1)._array);
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this._array);
   }
}
