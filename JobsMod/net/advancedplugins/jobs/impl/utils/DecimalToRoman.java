package net.advancedplugins.jobs.impl.utils;

import java.util.TreeMap;
import net.advancedplugins.ae.Values;

public class DecimalToRoman {
   private static final TreeMap<Integer, String> map = new TreeMap<>();

   public static String toRoman(int var0) {
      return Values.m_useNumbers ? var0 + "" : getString(var0);
   }

   public static String parseInteger(int var0) {
      return getString(var0);
   }

   private static String getString(int var0) {
      Integer var1 = map.floorKey(var0);
      if (var0 == 0) {
         return "0";
      } else if (var1 == null) {
         ASManager.getInstance().getLogger().warning("Invalid number for roman numerals: " + var0);
         return "?";
      } else {
         return var0 == var1 ? map.get(var0) : map.get(var1) + parseInteger(var0 - var1);
      }
   }

   static {
      map.put(1000, "M");
      map.put(900, "CM");
      map.put(500, "D");
      map.put(400, "CD");
      map.put(100, "C");
      map.put(90, "XC");
      map.put(50, "L");
      map.put(40, "XL");
      map.put(10, "X");
      map.put(9, "IX");
      map.put(5, "V");
      map.put(4, "IV");
      map.put(1, "I");
   }
}
