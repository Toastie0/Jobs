package net.advancedplugins.jobs.impl.utils.text;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.clip.placeholderapi.PlaceholderAPI;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.HooksHandler;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Text {
   private static final char SECTION_CHAR = '§';
   private static final char AMPERSAND_CHAR = '&';
   private static final String COLOR_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
   private static final String FORMAT_CODES = "KkLlMmMnOo";
   private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
   public static boolean RENDER_GRADIENT = true;
   private static Pattern HEX_PATTERN = Pattern.compile("\\{#[a-fA-F0-9]{6}}");

   public static void useProperHexPattern() {
      HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
   }

   public static void sendMessage(CommandSender var0, String var1) {
      Supplier var2 = () -> var0 instanceof OfflinePlayer && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")
         ? PlaceholderAPI.setPlaceholders((OfflinePlayer)var0, var1)
         : var1;
      var0.sendMessage(modify((String)var2.get()));
   }

   public static String parsePapi(String var0, OfflinePlayer var1) {
      return HooksHandler.getHook(HookPlugin.PLACEHOLDERAPI) != null ? PlaceholderAPI.setPlaceholders(var1, var0) : var0;
   }

   public static List<String> parsePapi(List<String> var0, OfflinePlayer var1) {
      return HooksHandler.getHook(HookPlugin.PLACEHOLDERAPI) != null
         ? var0.stream().map(var1x -> PlaceholderAPI.setPlaceholders(var1, var1x)).collect(Collectors.toList())
         : var0;
   }

   public static void sendMessage(Collection<CommandSender> var0, String var1) {
      for (CommandSender var3 : var0) {
         String[] var4 = var1.split("\n");

         for (String var8 : var4) {
            sendMessage(var3, var8);
         }
      }
   }

   public static String modify(String var0) {
      return modify(var0, null);
   }

   public static String modify(String var0, Replace var1) {
      return modify(var0, var1, true);
   }

   public static String modify(String var0, Replace var1, boolean var2) {
      if (HooksHandler.getHook(HookPlugin.PLACEHOLDERAPI) != null && var0 != null && var2) {
         var0 = var0.replace("%player", "%playertemp").replace("%luckperms", "%luckpermstemp");
         var0 = parsePapi(var0, null);
         var0 = var0.replace("%playertemp", "%player").replace("%luckpermstemp", "%luckperms");
      }

      return var0 == null ? null : renderColorCodes(var1 == null ? var0 : var1.apply(new Replacer()).applyTo(var0));
   }

   public static List<String> modify(List<String> var0) {
      return modify(var0, null);
   }

   public static List<String> modify(List<String> var0, Replace var1) {
      return modify(var0, var1, true);
   }

   public static List<String> modify(List<String> var0, Replace var1, boolean var2) {
      if (var0 == null) {
         return null;
      } else {
         ArrayList var3 = Lists.newArrayList();

         for (String var5 : var0) {
            String var6 = modify(var5, var1, var2);
            var3.addAll(Arrays.stream(var6.split("<new>")).collect(Collectors.toList()));
         }

         return var3;
      }
   }

   public static ItemStack modify(ItemStack var0, Replace var1) {
      ItemStack var2 = var0.clone();
      ItemMeta var3 = var2.getItemMeta();
      if (var3 == null) {
         return var2;
      } else {
         var3.setDisplayName(modify(var3.getDisplayName(), var1));
         var3.setLore(modify(var3.getLore(), var1));
         var2.setItemMeta(var3);
         return var2;
      }
   }

   public static String decolorize(String var0) {
      return var0 == null ? null : unrenderColorCodes(var0);
   }

   private static String unrenderColorCodes(String var0) {
      return var0 == null ? null : STRIP_COLOR_PATTERN.matcher(var0).replaceAll("");
   }

   public static String renderColorCodes(String var0) {
      if (MinecraftVersion.getVersion().getVersionId() >= 1160) {
         var0 = RENDER_GRADIENT ? gradient(var0) : var0;
      }

      for (Matcher var1 = HEX_PATTERN.matcher(var0); var1.find(); var1 = HEX_PATTERN.matcher(var0)) {
         String var2 = var0.substring(var1.start(), var1.end());
         var0 = StringUtils.replace(var0, var2, ChatColor.of(var2.replace("{", "").replace("}", "")) + "");
      }

      char[] var3 = var0.toCharArray();

      for (int var4 = 0; var4 < var3.length - 1; var4++) {
         if (var3[var4] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(var3[var4 + 1]) > -1) {
            var3[var4] = 167;
            var3[var4 + 1] = Character.toLowerCase(var3[var4 + 1]);
         }
      }

      return new String(var3);
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
