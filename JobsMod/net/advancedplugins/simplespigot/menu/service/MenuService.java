package net.advancedplugins.simplespigot.menu.service;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;
import net.advancedplugins.simplespigot.config.Config;
import net.advancedplugins.simplespigot.menu.Menu;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MenuService {
   public static Set<Integer> parseSlots(Menu var0, Config var1, String var2) {
      return parseSlots(var0, var1, "", var2);
   }

   public static Set<Integer> parseSlots(Menu var0, Config var1, String var2, String var3) {
      Stack var4 = new Stack();
      if (StringUtils.isNumeric(var3)) {
         return Sets.newHashSet(new Integer[]{Integer.parseInt(var3)});
      } else {
         String var5 = var1.string(String.format("%s.slots", var2.concat(var3))).replace(" ", "");
         Set var6 = parseSlots(var0, var5);
         if (var6 != null) {
            return var6;
         } else {
            List var7 = Splitter.on(",").omitEmptyStrings().splitToList(var5);
            if (var7.size() > 0) {
               for (String var9 : var7) {
                  if (StringUtils.isNumeric(var9)) {
                     var4.add(Integer.parseInt(var9));
                  }

                  Set var10 = parseSlots(var0, var9);
                  if (var10 != null) {
                     var4.addAll(var10);
                  }
               }

               return Sets.newLinkedHashSet(var4);
            } else {
               return Sets.newHashSet(var4);
            }
         }
      }
   }

   public static Set<Integer> parseSlots(Menu var0, String var1) {
      BiFunction var2 = (var1x, var2x) -> !var2x.equalsIgnoreCase("end") && !var2x.equalsIgnoreCase("start")
         ? var2x
         : Integer.toString(var2x.equalsIgnoreCase("end") ? var0.getRows() * 9 - 1 : 0);
      BiFunction var3 = (var1x, var2x) -> var2x < 0 ? 0 : Math.min(var2x, var0.getRows() * 9 - 1);
      LinkedHashSet var4 = Sets.newLinkedHashSet();
      String var5 = var1.replace(" ", "");
      if (var5.equalsIgnoreCase("fill")) {
         for (int var10 = 0; var10 < var0.getInventory().getSize(); var10++) {
            ItemStack var11 = var0.getInventory().getItem(var10);
            if (var11 == null || var11.getType().equals(Material.AIR)) {
               var4.add(var10);
            }
         }

         return var4;
      } else if (var5.equalsIgnoreCase("border")) {
         var4.addAll(getBorderSlots(var0));
         return var4;
      } else {
         List var6 = Splitter.on("...").omitEmptyStrings().splitToList(var5);
         if (var6.size() == 2) {
            String var7 = (String)var2.apply(var0, (String)var6.get(0));
            String var8 = (String)var2.apply(var0, (String)var6.get(1));
            if (StringUtils.isNumeric(var7) && StringUtils.isNumeric(var8)) {
               for (int var9 = (Integer)var3.apply(var0, Integer.parseInt(var7)); var9 <= var3.apply(var0, Integer.parseInt(var8)); var9++) {
                  var4.add(var9);
               }

               return var4;
            }
         }

         return null;
      }
   }

   public static Set<Integer> parseSlotsForActions(Menu var0, String var1) {
      BiFunction var2 = (var1x, var2x) -> !var2x.equalsIgnoreCase("end") && !var2x.equalsIgnoreCase("start")
         ? var2x
         : Integer.toString(var2x.equalsIgnoreCase("end") ? var0.getRows() * 9 - 1 : 0);
      BiFunction var3 = (var1x, var2x) -> var2x < 0 ? 0 : Math.min(var2x, var0.getRows() * 9 - 1);
      LinkedHashSet var4 = Sets.newLinkedHashSet();
      String var5 = var1.replace(" ", "");
      if (var5.equalsIgnoreCase("fill")) {
         for (int var11 = 0; var11 < var0.getInventory().getSize(); var11++) {
            ItemStack var13 = var0.getInventory().getItem(var11);
            if (var13 == null || var13.getType().equals(Material.AIR)) {
               var4.add(var11);
            }
         }

         return var4;
      } else if (var5.equalsIgnoreCase("border")) {
         var4.addAll(getBorderSlots(var0));
         return var4;
      } else {
         List var6 = Splitter.on("...").omitEmptyStrings().splitToList(var5);
         if (var6.size() == 2) {
            String var7 = (String)var2.apply(var0, (String)var6.get(0));
            String var8 = (String)var2.apply(var0, (String)var6.get(1));
            if (StringUtils.isNumeric(var7) && StringUtils.isNumeric(var8)) {
               for (int var15 = (Integer)var3.apply(var0, Integer.parseInt(var7)); var15 <= var3.apply(var0, Integer.parseInt(var8)); var15++) {
                  var4.add(var15);
               }

               return var4;
            }
         }

         if (var1.contains(",")) {
            for (String var10 : var1.split(",")) {
               if (StringUtils.isNumeric(var10)) {
                  var4.add(Integer.parseInt(var10));
               }
            }

            return var4;
         } else {
            return null;
         }
      }
   }

   private static Set<Integer> getBorderSlots(Menu var0) {
      HashSet var1 = Sets.newHashSet();
      if (var0.getRows() < 3) {
         Bukkit.getLogger().warning("You cannot use the border slot type with a menu with less than 3 rows.");
      } else {
         for (int var2 = 0; var2 < 9; var2++) {
            ItemStack var3 = var0.getInventory().getItem(var2);
            if (var3 == null || var3.getType().equals(Material.AIR)) {
               var1.add(var2);
            }
         }

         for (int var4 = (var0.getRows() - 1) * 9; var4 < var0.getRows() * 9 - 1; var4++) {
            ItemStack var7 = var0.getInventory().getItem(var4);
            if (var7 == null || var7.getType().equals(Material.AIR)) {
               var1.add(var4);
            }
         }

         for (byte var5 = 9; var5 < var0.getRows() * 9; var5 += 9) {
            ItemStack var8 = var0.getInventory().getItem(var5);
            if (var8 == null || var8.getType().equals(Material.AIR)) {
               var1.add(Integer.valueOf(var5));
            }
         }

         for (byte var6 = 8; var6 < var0.getRows() * 9; var6 += 9) {
            ItemStack var9 = var0.getInventory().getItem(var6);
            if (var9 == null || var9.getType().equals(Material.AIR)) {
               var1.add(Integer.valueOf(var6));
            }
         }
      }

      return var1;
   }
}
