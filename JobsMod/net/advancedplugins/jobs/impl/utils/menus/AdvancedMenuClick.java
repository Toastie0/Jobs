package net.advancedplugins.jobs.impl.utils.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class AdvancedMenuClick implements Listener {
   @EventHandler
   public void onClick(InventoryClickEvent var1) {
      Inventory var2 = var1.getInventory();
      ClickType var3 = var1.getClick();
      if (var2.getHolder() instanceof AdvancedMenu) {
         AdvancedMenu var4 = (AdvancedMenu)var2.getHolder();
         var1.setCancelled(true);
         int var5 = var1.getRawSlot();
         if (!var3.equals(ClickType.UNKNOWN) && var5 <= var2.getSize()) {
            var4.onClick((Player)var1.getWhoClicked(), var5, var3);
         }
      }
   }

   @EventHandler
   public void onClose(InventoryCloseEvent var1) {
      Inventory var2 = var1.getInventory();
      if (var2.getHolder() instanceof AdvancedMenu) {
         AdvancedMenu var3 = (AdvancedMenu)var2.getHolder();
         var3.onClose((Player)var1.getPlayer());
      }
   }
}
