package net.advancedplugins.jobs.creator.gui;

import net.advancedplugins.jobs.creator.GUICreator;
import net.advancedplugins.jobs.creator.GUICreatorHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CreatorMenu implements Listener {
   private String invName = "";

   public CreatorMenu(JavaPlugin var1) {
      var1.getServer().getPluginManager().registerEvents(this, var1);
   }

   public String getInvName() {
      return this.invName;
   }

   public void setInvName(String var1) {
      this.invName = ChatColor.translateAlternateColorCodes('&', var1);
   }

   @EventHandler
   public void onClick(InventoryClickEvent var1) {
      if (var1.getClickedInventory() != null && var1.getCurrentItem() != null) {
         String var2 = var1.getView().getTitle();
         if (var2.equalsIgnoreCase(this.getInvName())) {
            var1.setCancelled(true);
            Player var3 = (Player)var1.getWhoClicked();
            GUICreator var4 = GUICreatorHandler.getHandler().getEditor(var3.getUniqueId());
            CreatorClickEvent var5 = new CreatorClickEvent(var1.getView(), var1.getSlotType(), var1.getSlot(), var1.getClick(), var1.getAction());
            var1.setCursor(null);
            var5.setCreator(var4);
            var5.setPlayer(var3);
            this.onEditorClick(var5);
         }
      }
   }

   void onEditorClick(CreatorClickEvent var1) {
   }

   public int getResponsiveInventorySize(int var1) {
      if (var1 <= 9) {
         return 9;
      } else {
         int var2 = var1;
         if (var1 / 9.0 > 0.0) {
            var2 = var1 / 9 * 9 + 9;
         }

         return Math.min(var2, 54);
      }
   }

   public static String color(String var0) {
      return ChatColor.translateAlternateColorCodes('&', var0);
   }
}
