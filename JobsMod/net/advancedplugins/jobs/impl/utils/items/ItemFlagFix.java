package net.advancedplugins.jobs.impl.utils.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ItemFlagFix {
   private static final UUID FIX_UUID = UUID.fromString("90787d5e-1940-4722-a91e-f0ba37f7c29d");

   public static void fix(ItemStack var0) {
      if (var0.hasItemMeta()) {
         ItemMeta var1 = var0.getItemMeta();
         fix(var1);
         var0.setItemMeta(var1);
      }
   }

   public static void fix(ItemMeta var0) {
      try {
         if (!MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)) {
            return;
         }

         Multimap var1 = var0.getAttributeModifiers();
         if (var1 == null) {
            HashMultimap var3 = HashMultimap.create();
            var0.setAttributeModifiers(var3);
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }
   }

   @NotNull
   public static ItemFlag[] hideAllAttributes() {
      ArrayList var0 = new ArrayList<>(
         List.of(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE)
      );
      if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)) {
         var0.add(ItemFlag.valueOf("HIDE_ADDITIONAL_TOOLTIP"));
      } else {
         var0.add(ItemFlag.valueOf("HIDE_POTION_EFFECTS"));
      }

      if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_19_R3)) {
         var0.add(ItemFlag.valueOf("HIDE_ARMOR_TRIM"));
      }

      return var0.toArray(ItemFlag[]::new);
   }
}
