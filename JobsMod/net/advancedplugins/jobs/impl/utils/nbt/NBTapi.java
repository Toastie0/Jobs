package net.advancedplugins.jobs.impl.utils.nbt;

import net.advancedplugins.jobs.impl.utils.pdc.PDCHandler;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class NBTapi {
   public static ItemStack addNBTTag(String var0, String var1, ItemStack var2) {
      ItemMeta var3 = var2.getItemMeta();
      PDCHandler.setString(var3, var0, var1);
      var2.setItemMeta(var3);
      return var2;
   }

   public static ItemStack addNBTTag(String var0, int var1, ItemStack var2) {
      ItemMeta var3 = var2.getItemMeta();
      PDCHandler.setInt(var3, var0, var1);
      var2.setItemMeta(var3);
      return var2;
   }

   public static ItemStack addNBTTag(String var0, long var1, ItemStack var3) {
      ItemMeta var4 = var3.getItemMeta();
      PDCHandler.setLong(var4, var0, var1);
      var3.setItemMeta(var4);
      return var3;
   }

   public static boolean contains(String var0, ItemStack var1) {
      return var1 == null || var1.getType().equals(Material.AIR) ? false : var1.hasItemMeta() && PDCHandler.contains(var1.getItemMeta(), var0);
   }

   public static String get(String var0, ItemStack var1) {
      if (var1 != null && !var1.getType().isAir()) {
         return var1.hasItemMeta() && PDCHandler.hasString(var1.getItemMeta(), var0) ? PDCHandler.getString(var1.getItemMeta(), var0) : null;
      } else {
         return null;
      }
   }

   public static Integer getInt(String var0, ItemStack var1) {
      if (var1 != null && !var1.getType().isAir()) {
         return PDCHandler.hasInt(var1.getItemMeta(), var0) ? PDCHandler.getInt(var1.getItemMeta(), var0) : 0;
      } else {
         return null;
      }
   }

   public static long getLong(String var0, ItemStack var1) {
      if (var1 == null) {
         return 0L;
      } else {
         return PDCHandler.has(var1.getItemMeta(), var0, PersistentDataType.LONG) ? PDCHandler.getLong(var1.getItemMeta(), var0) : 0L;
      }
   }

   public static ItemStack removeTag(String var0, ItemStack var1) {
      ItemMeta var2 = var1.getItemMeta();
      PDCHandler.remove(var2, var0);
      var1.setItemMeta(var2);
      return var1;
   }
}
