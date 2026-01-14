package net.advancedplugins.jobs.impl.actions.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityEnterLoveModeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class EntityBreedQuest extends ActionContainer {
   private static boolean hasSniffers;
   private final Map<UUID, List<Integer>> fedTurtles;
   private Map<Integer, Player> snifferBreeders = new HashMap<>();

   public EntityBreedQuest(JavaPlugin var1) {
      super(var1);
      this.fedTurtles = new HashMap<>();
   }

   @EventHandler
   public void onBreed(EntityBreedEvent var1) {
      if (var1.getBreeder() instanceof Player var3) {
         LivingEntity var4 = var1.getEntity();
         String var5 = var1.getEntity().getCustomName();
         this.executionBuilder("breed").player(var3).root(var4).subRoot("name", var5).progressSingle().buildAndExecute();
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onItemSpawn(ItemSpawnEvent var1) {
      if (hasSniffers) {
         Item var2 = var1.getEntity();
         if (var2.getItemStack().getType().name().equals("SNIFFER_EGG")) {
            Location var3 = var2.getLocation();

            for (Entity var6 : var3.getWorld().getNearbyEntities(var2.getBoundingBox().expand(0.5, 0.5, 0.5))) {
               if (var6.getType().toString().equals("SNIFFER")) {
                  Player var7 = this.snifferBreeders.get(var6.getEntityId());
                  if (var7 != null) {
                     this.snifferBreeders.remove(var6.getEntityId());

                     for (Entity var10 : var6.getLocation().getWorld().getNearbyEntities(var6.getBoundingBox().expand(1.0, 1.0, 1.0))) {
                        if (var10 != var6 && var10.getType().toString().equals("SNIFFER")) {
                           this.snifferBreeders.remove(var10.getEntityId());
                        }
                     }

                     String var11 = var6.getCustomName();
                     this.executionBuilder("breed").player(var7).root(var6).subRoot("name", var11).progressSingle().buildAndExecute();
                     break;
                  }
               }
            }
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onEnterLoveMode(EntityEnterLoveModeEvent var1) {
      if (hasSniffers) {
         if (var1.getEntity().getType().toString().equals("SNIFFER")) {
            this.snifferBreeders.put(var1.getEntity().getEntityId(), (Player)var1.getHumanEntity());
         }
      }
   }

   @EventHandler
   public void onInteract(PlayerInteractAtEntityEvent var1) {
      if (!var1.isCancelled()) {
         Player var2 = var1.getPlayer();
         UUID var3 = var2.getUniqueId();
         PlayerInventory var4 = var2.getInventory();
         Entity var5 = var1.getRightClicked();
         EquipmentSlot var6 = var1.getHand();
         if (var5.getType() == EntityType.TURTLE) {
            ItemStack var7 = var6 == EquipmentSlot.HAND ? var4.getItemInMainHand() : var4.getItemInOffHand();
            if (var7 != null && var7.getType() == Material.SEAGRASS) {
               this.fedTurtles.putIfAbsent(var3, new ArrayList<>());
               this.fedTurtles.get(var3).add(var5.getEntityId());
            }
         }
      }
   }

   @EventHandler
   public void onLayEgg(EntityChangeBlockEvent var1) {
      Entity var2 = var1.getEntity();
      if (var2.getType() == EntityType.TURTLE) {
         if (var1.getTo() == Material.TURTLE_EGG) {
            AtomicReference var3 = new AtomicReference();
            this.fedTurtles.forEach((var2x, var3x) -> {
               if (var3x.contains(var2.getEntityId())) {
                  var3.set(var2x);
                  var3x.remove(Integer.valueOf(var2.getEntityId()));
               }
            });
            if (var3.get() != null) {
               Player var4 = Bukkit.getPlayer((UUID)var3.get());
               if (var4 != null && var4.isOnline()) {
                  String var5 = var2.getCustomName();
                  this.executionBuilder("breed").player(var4).root(var2).subRoot("name", var5).progressSingle().buildAndExecute();
               }
            }
         }
      }
   }

   static {
      try {
         hasSniffers = EntityType.valueOf("SNIFFER") != null;
      } catch (IllegalArgumentException var1) {
         hasSniffers = false;
      }
   }
}
