package net.advancedplugins.jobs.impl.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedTime {
   public static String DEFAULT_FORMAT = "<years:y ><months:mo ><weeks:w ><days:d ><hours:h ><minutes:m ><seconds:s>";

   public static String format(TimeUnit var0, long var1) {
      return format(DEFAULT_FORMAT, var0, var1);
   }

   public static String format(String var0, TimeUnit var1, long var2) {
      long var4 = var1.toSeconds(var2);
      HashMap var6 = new HashMap();
      var6.put("years", var4 / 31536000L);
      var6.put("months", var4 % 31536000L / 2592000L);
      var6.put("weeks", var4 % 2592000L / 604800L);
      var6.put("days", var4 % 604800L / 86400L);
      var6.put("hours", var4 % 86400L / 3600L);
      var6.put("minutes", var4 % 3600L / 60L);
      var6.put("seconds", var4 % 60L);
      String var7 = var0;
      HashMap var8 = new HashMap();
      var6.forEach((var2x, var3) -> {
         String var4x = "<" + var2x + ":([^>]+)>";
         Pattern var5 = Pattern.compile(var4x);
         Matcher var6x = var5.matcher(var0);

         while (var6x.find()) {
            Object var7x = var6x.group(1);
            String var8x = "<" + var2x + ":" + var7x + ">";
            String var9 = var3 + var7x;
            if (var3 <= 0L) {
               var8.put(var8x, "");
            } else {
               var8.put(var8x, var9);
            }
         }
      });

      for (Entry var10 : var8.entrySet()) {
         var7 = var7.replaceAll((String)var10.getKey(), (String)var10.getValue());
      }

      return var7;
   }

   public static long textToMillis(String var0) {
      String var1 = "";
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;

      for (String var9 : var0.split("")) {
         int var10 = -1;

         try {
            var10 = Integer.parseInt(var9);
         } catch (Exception var13) {
         }

         if (var10 >= 0) {
            var1 = var1 + var10;
         } else if (!var1.isEmpty()) {
            switch (var9) {
               case "d":
                  var2 = Integer.parseInt(var1);
                  break;
               case "h":
                  var3 = Integer.parseInt(var1);
                  break;
               case "m":
                  var4 = Integer.parseInt(var1);
                  break;
               case "s":
                  var5 = Integer.parseInt(var1);
            }

            var1 = "";
         }
      }

      var3 += var2 * 24;
      var4 += var3 * 60;
      var5 += var4 * 60;
      return var5 * 1000L;
   }

   public static String oldToNewPattern(String var0, List<String> var1) {
      StringBuilder var2 = new StringBuilder();

      while (var0.contains("%d") && !var1.isEmpty()) {
         int var3 = var0.indexOf("%d");
         int var4 = var3 + 2;
         int var5 = var0.indexOf("%d", var4);
         if (var5 == -1) {
            var5 = var0.length();
         }

         String var6 = var0.substring(var4, var5);
         var2.append("<").append((String)var1.get(0)).append(":").append(var6).append(">");
         var1.remove(0);
         var0 = var0.replaceFirst("%d" + var6, "");
      }

      return var2.toString();
   }
}
