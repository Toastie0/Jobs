package net.advancedplugins.simplespigot.menu.listener;

import net.advancedplugins.simplespigot.menu.Menu;
import net.advancedplugins.simplespigot.menu.item.MenuItem;
import net.advancedplugins.simplespigot.menu.item.click.ClickAction;
import net.advancedplugins.simplespigot.menu.item.click.ClickType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class MenuListener implements Listener {
   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.LOW
   )
   public void onInventoryClick(InventoryClickEvent var1) {
      Inventory var2 = var1.getInventory();
      InventoryAction var3 = var1.getAction();
      if (var2.getHolder() instanceof Menu) {
         Menu var4 = (Menu)var2.getHolder();
         var1.setCancelled(true);
         int var5 = var1.getRawSlot();
         if (var5 < var2.getSize() && !var3.equals(InventoryAction.NOTHING)) {
            MenuItem var6 = var4.getMenuItem(var5);
            if (var6 == null) {
               return;
            }

            ClickAction var7 = var6.getClickAction();
            if (var7 == null) {
               return;
            }

            var7.onClick(var6, ClickType.parse(var3));
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.MONITOR
   )
   public void onInventoryClose(InventoryCloseEvent var1) {
      Inventory var2 = var1.getInventory();
      if (var2.getHolder() instanceof Menu) {
         Menu var3 = (Menu)var2.getHolder();
         Runnable var4 = var3.getCloseAction();
         if (var4 == null) {
            return;
         }

         var4.run();
      }
   }
}
