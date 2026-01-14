package net.advancedplugins.jobs.creator.gui;

import net.advancedplugins.jobs.creator.GUICreator;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.InventoryView;

public class CreatorClickEvent extends InventoryClickEvent {
   private GUICreator creator;
   private Player player;
   private static final HandlerList handlers = new HandlerList();

   public CreatorClickEvent(InventoryView var1, SlotType var2, int var3, ClickType var4, InventoryAction var5) {
      super(var1, var2, var3, var4, var5);
   }

   public GUICreator getCreator() {
      return this.creator;
   }

   public void setCreator(GUICreator var1) {
      this.creator = var1;
   }

   public Player getPlayer() {
      return this.player;
   }

   public void setPlayer(Player var1) {
      this.player = var1;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
