package net.advancedplugins.jobs.impl.utils.nbt.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class ReflectionUtil {
   private static Field field_modifiers;

   public static Field makeNonFinal(Field var0) {
      int var1 = var0.getModifiers();
      if (Modifier.isFinal(var1)) {
         field_modifiers.set(var0, var1 & -17);
      }

      return var0;
   }

   public static void setFinal(Object var0, Field var1, Object var2) {
      var1.setAccessible(true);
      var1 = makeNonFinal(var1);
      var1.set(var0, var2);
   }

   static {
      try {
         field_modifiers = Field.class.getDeclaredField("modifiers");
         field_modifiers.setAccessible(true);
      } catch (NoSuchFieldException var8) {
         try {
            Method var1 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
            var1.setAccessible(true);
            Field[] var2 = (Field[])var1.invoke(Field.class, false);

            for (Field var6 : var2) {
               if (var6.getName().equals("modifiers")) {
                  field_modifiers = var6;
                  field_modifiers.setAccessible(true);
                  break;
               }
            }
         } catch (Exception var7) {
            throw new NbtApiException(var7);
         }
      }

      if (field_modifiers == null) {
         throw new NbtApiException("Unable to init the modifiers Field.");
      }
   }
}
