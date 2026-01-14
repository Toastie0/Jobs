package net.advancedplugins.jobs.impl.actions.internal;

import com.google.common.collect.HashMultiset;
import java.util.concurrent.atomic.AtomicInteger;
import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SmeltQuest extends ActionContainer {
   public SmeltQuest(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onInventoryClick(InventoryClickEvent var1) {
      Player var2 = (Player)var1.getWhoClicked();
      ItemStack var3 = var1.getCurrentItem();
      ItemStack var4 = var1.getCursor();
      if ((var1.getInventory().getType().toString().contains("FURNACE") || var1.getInventory().getType().toString().contains("SMOKER"))
         && var1.getSlotType() == SlotType.RESULT
         && var3 != null) {
         if (var4 == null || var4.getType() == Material.AIR || var3.isSimilar(var4) || var1.isShiftClick()) {
            int var5 = var3.getAmount();
            if (var1.isShiftClick()) {
               int var6 = this.hasRoomForItem(var2, var3);
               if (var6 == -420 || var6 == 0) {
                  return;
               }

               var5 = var6;
            } else if (var4 != null && var4.getType() == var3.getType() && var4.getAmount() + var3.getAmount() > var3.getMaxStackSize()) {
               return;
            }

            if (var1.getClick().equals(ClickType.DROP)) {
               var5 = 1;
            }

            if (var1.getClick() == ClickType.NUMBER_KEY) {
               int var8 = var1.getHotbarButton();
               ItemStack var7 = var2.getInventory().getItem(var8);
               if (var7 != null && var7.getType() != Material.AIR) {
                  return;
               }
            }

            if (var1.getClick() == ClickType.RIGHT) {
               var5 = (int)Math.round(var5 / 2.0);
            }

            this.executionBuilder("smelt").player(var2).progress(var5).root(var3).buildAndExecute();
         }
      }
   }

   private int hasRoomForItem(Player var1, ItemStack var2) {
      if (var2 == null) {
         return 0;
      } else {
         int var3 = Math.min(this.getMaterialSpace(var1, var2.getType()), var2.getAmount());
         if (var1.getInventory().firstEmpty() != -1) {
            if (var2.getMaxStackSize() != 1) {
               return var3;
            } else {
               int var9 = 0;

               for (ItemStack var16 : var1.getInventory().getContents()) {
                  if (var16 == null) {
                     var9++;
                  }
               }

               return var9;
            }
         } else {
            HashMultiset var4 = HashMultiset.create();

            for (ItemStack var8 : var1.getInventory().getContents()) {
               if (var8 != null && var8.getType() == var2.getType() && var8.getAmount() < var8.getMaxStackSize()) {
                  var4.add(var8.getAmount());
               }
            }

            if (var4.size() <= 0) {
               return -420;
            } else {
               int var10 = 0;

               for (int var14 : var4) {
                  var10 += var14;
               }

               return Math.min(var4.size() * 64 - var10, var3);
            }
         }
      }
   }

   private int getMaterialSpace(Player var1, Material var2) {
      AtomicInteger var3 = new AtomicInteger();

      for (ItemStack var5 : var1.getInventory()) {
         var3.addAndGet(var5 == null ? var2.getMaxStackSize() : (var5.getType() == var2 ? var2.getMaxStackSize() - var5.getAmount() : 0));
      }

      return var3.get();
   }
}
