package net.advancedplugins.simplespigot.menu.item.click;

import org.bukkit.event.inventory.InventoryAction;

public enum ClickType {
   LEFT,
   SHIFT_LEFT,
   RIGHT,
   DROP,
   MIDDLE_MB,
   OTHER;

   public static ClickType parse(InventoryAction var0) {
      switch (var0) {
         case PICKUP_ALL:
         case PLACE_ALL:
         case PLACE_SOME:
         case SWAP_WITH_CURSOR:
            return LEFT;
         case PICKUP_HALF:
         case PLACE_ONE:
            return RIGHT;
         case MOVE_TO_OTHER_INVENTORY:
            return SHIFT_LEFT;
         case DROP_ALL_CURSOR:
         case DROP_ALL_SLOT:
         case DROP_ONE_CURSOR:
         case DROP_ONE_SLOT:
            return DROP;
         case CLONE_STACK:
            return MIDDLE_MB;
         default:
            return OTHER;
      }
   }
}
