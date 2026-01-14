package net.advancedplugins.jobs.impl.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class RemoveDeathItems implements Listener {
   private static final HashMap<UUID, List<ItemStack>> itemsCache = new HashMap<>();

   public static void add(UUID var0, ItemStack var1) {
      if (ASManager.isValid(var1)) {
         Object var2 = itemsCache.containsKey(var0) ? itemsCache.get(var0) : new ArrayList();
         var2.add(var1);
         itemsCache.put(var0, (List<ItemStack>)var2);
      }
   }

   @EventHandler
   public void onDeath(EntityDeathEvent var1) {
      if (var1.getEntityType() == EntityType.PLAYER) {
         if (itemsCache.containsKey(var1.getEntity().getUniqueId())) {
            List var2 = itemsCache.remove(var1.getEntity().getUniqueId());

            for (ItemStack var4 : new ArrayList(var1.getDrops())) {
               ItemStack var5 = var4.clone();

               for (ItemStack var7 : var2) {
                  ItemStack var8 = var7.clone();
                  boolean var9 = var8.getType() == var5.getType();
                  if (var9) {
                     var1.getDrops().remove(var4);
                  }
               }
            }
         }
      }
   }
}
