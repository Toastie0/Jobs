package net.advancedplugins.jobs.impl.actions.internal;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class EnchantQuests extends ActionContainer {
   public EnchantQuests(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onEnchant(EnchantItemEvent var1) {
      Player var2 = var1.getEnchanter();
      ItemStack var3 = var1.getItem().clone();
      int var4 = var1.getExpLevelCost();
      Map var5 = var1.getEnchantsToAdd();

      for (Enchantment var7 : var5.keySet()) {
         String var8 = MinecraftVersion.isNew() ? this.getEnchantName(var7) : var7.getName();
         int var9 = (Integer)var5.get(var7);
         this.executionBuilder("enchant")
            .player(var2)
            .root(var8)
            .subRoot("level", String.valueOf(var9))
            .subRoot("cost", String.valueOf(var4))
            .subRoot(var3)
            .progress(1)
            .buildAndExecute();
         this.executionBuilder("enchant-all")
            .player(var2)
            .root(var8)
            .subRoot("level", String.valueOf(var9))
            .subRoot("cost", String.valueOf(var4))
            .subRoot(var3)
            .progress(1)
            .buildAndExecute();
      }
   }

   @EventHandler
   public void onAnvil(PrepareAnvilEvent var1) {
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onAnvilEnchant(InventoryClickEvent var1) {
      if (var1.getWhoClicked() instanceof Player
         && var1.getInventory() instanceof AnvilInventory
         && var1.getSlotType() == SlotType.RESULT
         && var1.getClick() != ClickType.MIDDLE) {
         Player var2 = (Player)var1.getWhoClicked();
         AnvilInventory var3 = (AnvilInventory)var1.getInventory();
         ItemStack var4 = var3.getItem(1);
         if (var4 != null
            && var4.getType() == Material.ENCHANTED_BOOK
            && (!var1.getClick().toString().contains("SHIFT") || ASManager.getEmptySlotCountInInventory(var2) >= 1)) {
            if (var2.getLevel() >= var3.getRepairCost()) {
               ItemStack var5 = var1.getCurrentItem();

               for (Entry var7 : ((EnchantmentStorageMeta)var4.getItemMeta()).getStoredEnchants().entrySet()) {
                  String var8 = this.getEnchantName((Enchantment)var7.getKey());
                  UnaryOperator var9 = var3x -> {
                     var3x.subRoot("level", String.valueOf(var7.getValue()));
                     var3x.subRoot(var5);
                     return var3x.root(var8);
                  };
                  this.executionBuilder("enchant-all")
                     .player(var2)
                     .root(var8)
                     .subRoot("level", String.valueOf(var7.getValue()))
                     .subRoot(var5)
                     .progress(1)
                     .buildAndExecute();
                  this.executionBuilder("enchant-anvil")
                     .player(var2)
                     .root(var8)
                     .subRoot("level", String.valueOf(var7.getValue()))
                     .subRoot(var5)
                     .progress(1)
                     .buildAndExecute();
               }
            }
         }
      }
   }

   private String getEnchantName(Enchantment var1) {
      if (MinecraftVersion.isNew()) {
         String var2 = var1.getKey().getKey();
         return var2.startsWith("minecraft:") ? var2.split("minecraft:")[1] : var2;
      } else {
         return var1.getName();
      }
   }
}
