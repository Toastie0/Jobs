package net.advancedplugins.jobs.impl.utils;

import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class ColorUtils {
   private static final Pattern hexPattern = Pattern.compile("\\{#[a-fA-F0-9]{6}}");
   private static final Pattern normalPattern = Pattern.compile("([§&])[0-9a-fA-Fk-orK-OR]");
   private static final char AMPERSAND_CHAR = '&';
   private static final String COLOR_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
   private static final String FORMAT_CODES = "KkLlMmMnOo";

   public static String format(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         if (MinecraftVersion.getVersionNumber() >= 1160) {
            var0 = gradient(var0);

            for (Matcher var1 = hexPattern.matcher(var0); var1.find(); var1 = hexPattern.matcher(var0)) {
               String var2 = var0.substring(var1.start(), var1.end());
               var0 = StringUtils.replace(var0, var2, ChatColor.of(var2.replace("{", "").replace("}", "")) + "");
            }
         }

         return ChatColor.translateAlternateColorCodes('&', var0);
      } else {
         return var0;
      }
   }

   public static List<String> format(List<String> var0) {
      var0.replaceAll(ColorUtils::format);
      return var0;
   }

   public static String stripColor(@NotNull String var0) {
      return ChatColor.stripColor(var0);
   }

   public static List<String> stripColor(List<String> var0) {
      var0.replaceAll(ColorUtils::stripColor);
      return var0;
   }

   public static String getLastColor(String var0) {
      String var1 = "";
      Matcher var2 = hexPattern.matcher(var0);

      while (var2.find()) {
         var1 = var0.substring(var2.start(), var2.end());
      }

      Matcher var3 = normalPattern.matcher(var0);

      while (var3.find()) {
         var1 = var0.substring(var3.start(), var3.end());
      }

      return var1;
   }

   private static String gradient(String var0) {
      while (var0.contains("<gradient") && var0.contains("</gradient>")) {
         int var1 = var0.indexOf("<gradient");
         char[] var2 = var0.toCharArray();
         StringBuilder var3 = new StringBuilder();

         for (int var4 = 0; var4 < var1 - 1; var4++) {
            if (var2[var4] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(var2[var4 + 1]) > -1) {
               if ("KkLlMmMnOo".indexOf(var2[var4 + 1]) > -1) {
                  var3.append('&').append(var2[var4 + 1]);
               } else {
                  var3 = new StringBuilder();
               }
            }

            if (var2[var4] == '{' && var2[var4 + 1] == '#') {
               var3 = new StringBuilder();
            }
         }

         int var14 = 10;
         if (var3.length() > 0) {
            var14 += var3.length();
         }

         int var5 = var0.indexOf("#", var1);
         int var6 = var0.indexOf("#", var5 + 1);
         String var7 = var0.substring(var5, var5 + 7);
         String var8 = var0.substring(var6, var6 + 7);
         String var9 = var0.substring(var0.indexOf(">", var1) + 1, var0.indexOf("</gradient>"));
         String var10 = var0.substring(var1, var0.indexOf(">", var1) + 1);
         String var11 = "</gradient>";
         String var12 = var9;

         for (int var13 = 0; var13 < var9.length(); var13++) {
            var12 = var12.substring(0, var13 * var14) + gradient(var8, var7, (float)var13 / (var9.length() - 1)) + var3 + var12.substring(var13 * var14);
         }

         String var15 = var10 + var9 + var11;
         var15 = var15.replace("?", "\\?");
         var0 = var0.replaceFirst(var15, var12);
      }

      return var0;
   }

   private static String gradient(String var0, String var1, float var2) {
      try {
         Color var3 = Color.decode(var0);
         Color var4 = Color.decode(var1);
         float var5 = 1.0F - var2;
         Color var6 = new Color(
            var3.getRed() / 255.0F * var2 + var4.getRed() / 255.0F * var5,
            var3.getGreen() / 255.0F * var2 + var4.getGreen() / 255.0F * var5,
            var3.getBlue() / 255.0F * var2 + var4.getBlue() / 255.0F * var5
         );
         return String.format("{#%02x%02x%02x}", var6.getRed(), var6.getGreen(), var6.getBlue());
      } catch (Exception var7) {
         return "{#FFFFFF}";
      }
   }
}
