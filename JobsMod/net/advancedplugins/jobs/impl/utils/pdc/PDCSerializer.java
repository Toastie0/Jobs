package net.advancedplugins.jobs.impl.utils.pdc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class PDCSerializer {
   private static final String ENTRY_SEPARATOR = ";;";
   private static final String KEY_VALUE_SEPARATOR = "::";
   private static final String ESCAPE_CHAR = "\\";

   public static String mapToString(Map<String, String> var0) {
      StringBuilder var1 = new StringBuilder();
      boolean var2 = true;

      for (Entry var4 : var0.entrySet()) {
         if (!var2) {
            var1.append(";;");
         }

         var2 = false;
         String var5 = escapePDCString((String)var4.getKey());
         String var6 = escapePDCString((String)var4.getValue());
         var1.append(var5).append("::").append(var6);
      }

      return var1.toString();
   }

   public static Map<String, String> stringToMap(String var0) {
      HashMap var1 = new HashMap();
      if (var0 != null && !var0.isEmpty()) {
         String[] var2 = var0.split(Pattern.quote(";;"));

         for (String var6 : var2) {
            String[] var7 = var6.split(Pattern.quote("::"), 2);
            if (var7.length != 2) {
               throw new IllegalArgumentException("Malformed PDC string: Invalid key-value pair in entry: " + var6);
            }

            String var8 = unescapePDCString(var7[0]);
            String var9 = unescapePDCString(var7[1]);
            var1.put(var8, var9);
         }

         return var1;
      } else {
         return var1;
      }
   }

   public static String escapePDCString(String var0) {
      if (var0 == null) {
         return null;
      } else if (var0.isEmpty()) {
         return "";
      } else {
         StringBuilder var1 = new StringBuilder();

         for (int var2 = 0; var2 < var0.length(); var2++) {
            char var3 = var0.charAt(var2);
            if (String.valueOf(var3).equals("\\") || String.valueOf(var3).equals(";;") || String.valueOf(var3).equals("::")) {
               var1.append("\\");
            }

            var1.append(var3);
         }

         return var1.toString();
      }
   }

   public static String unescapePDCString(String var0) {
      if (var0 == null) {
         return null;
      } else if (var0.isEmpty()) {
         return "";
      } else {
         StringBuilder var1 = new StringBuilder();
         boolean var2 = false;

         for (int var3 = 0; var3 < var0.length(); var3++) {
            char var4 = var0.charAt(var3);
            if (var2) {
               var1.append(var4);
               var2 = false;
            } else if (String.valueOf(var4).equals("\\")) {
               var2 = true;
            } else {
               var1.append(var4);
            }
         }

         if (var2) {
            throw new IllegalArgumentException("Malformed PDC string: Trailing escape character.");
         } else {
            return var1.toString();
         }
      }
   }

   public static boolean isValidPDCString(String var0) {
      return var0 != null && !var0.isEmpty() ? !var0.contains(";;") && !var0.contains("::") : true;
   }

   public static String createAndEncodeKey(String var0, String var1) {
      return escapePDCString((var1 != null ? var1 + "_._" : "") + var0);
   }

   public static String[] splitPDCKey(String var0) {
      String var1 = unescapePDCString(var0);
      String[] var2 = var1.split("_\\._");
      if (var2.length == 1) {
         return new String[]{var2[0], null};
      } else if (var2.length == 2) {
         return new String[]{var2[1], var2[0]};
      } else {
         throw new IllegalArgumentException("Malformed PDC key: " + var0);
      }
   }
}
