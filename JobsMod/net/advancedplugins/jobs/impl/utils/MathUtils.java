package net.advancedplugins.jobs.impl.utils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtils {
   public static int clamp(int var0, int var1, int var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   public static long clamp(long var0, long var2, long var4) {
      return var2 > var4 ? var4 : Math.max(var2, Math.min(var4, var0));
   }

   public static double clamp(double var0, double var2, double var4) {
      return var2 > var4 ? var4 : Math.max(var2, Math.min(var4, var0));
   }

   public static int randomBetween(int var0, int var1) {
      if (var0 > var1) {
         int var2 = var0;
         var0 = var1;
         var1 = var2;
      }

      return var0 == var1 ? var0 : ThreadLocalRandom.current().nextInt(var1 - var0) + var0;
   }

   public static int getClosestInt(int var0, List<Integer> var1) {
      int var2 = Integer.MAX_VALUE;
      int var3 = var0;

      for (int var5 : var1) {
         int var6 = Math.abs(var5 - var0);
         if (var6 < var2) {
            var2 = var6;
            var3 = var5;
         }
      }

      return var3;
   }

   public static boolean isByte(String var0) {
      try {
         Byte.parseByte(var0);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public static boolean isShort(String var0) {
      try {
         Short.parseShort(var0);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public static boolean isInteger(String var0) {
      try {
         Integer.parseInt(var0);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public static boolean isLong(String var0) {
      try {
         Long.parseLong(var0);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public static boolean isFloat(String var0) {
      try {
         Float.parseFloat(var0);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public static boolean isDouble(String var0) {
      try {
         Double.parseDouble(var0);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }
}
