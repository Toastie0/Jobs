package net.advancedplugins.jobs.impl.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.HooksHandler;
import net.advancedplugins.jobs.impl.utils.hooks.factions.FactionsPluginHook;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.metadata.FixedMetadataValue;

public class AreaUtils {
   private static final List<String> ignoredEntityNamesPre1_20_6 = Arrays.asList(
      "ARMOR_STAND",
      "ITEM_FRAME",
      "PAINTING",
      "LEASH_HITCH",
      "MINECART",
      "MINECART_CHEST",
      "MINECART_COMMAND",
      "MINECART_FURNACE",
      "MINECART_HOPPER",
      "MINECART_MOB_SPAWNER",
      "MINECART_TNT",
      "BOAT",
      "FISHING_HOOK",
      "DROPPED_ITEM",
      "ARROW",
      "SPECTRAL_ARROW",
      "EGG",
      "ENDER_PEARL",
      "ENDER_SIGNAL",
      "EXPERIENCE_ORB",
      "FIREBALL",
      "FIREWORK",
      "SMALL_FIREBALL",
      "LLAMA_SPIT",
      "SNOWBALL",
      "SPLASH_POTION",
      "THROWN_EXP_BOTTLE",
      "WITHER_SKULL",
      "SHULKER_BULLET",
      "PRIMED_TNT",
      "TRIDENT",
      "DRAGON_FIREBALL",
      "LIGHTNING",
      "AREA_EFFECT_CLOUD",
      "UNKNOWN"
   );
   private static final List<String> ignoredEntityNames1_20_6 = Arrays.asList(
      "ARMOR_STAND",
      "ITEM_FRAME",
      "PAINTING",
      "LEASH_KNOT",
      "MINECART",
      "CHEST_MINECART",
      "COMMAND_BLOCK_MINECART",
      "FURNACE_MINECART",
      "HOPPER_MINECART",
      "SPAWNER_MINECART",
      "TNT_MINECART",
      "BOAT",
      "ITEM",
      "ARROW",
      "SPECTRAL_ARROW",
      "EGG",
      "ENDER_PEARL",
      "EXPERIENCE_ORB",
      "FIREBALL",
      "FIREWORK_ROCKET",
      "SMALL_FIREBALL",
      "LLAMA_SPIT",
      "SNOWBALL",
      "POTION",
      "EXPERIENCE_BOTTLE",
      "WITHER_SKULL",
      "SHULKER_BULLET",
      "TNT",
      "TRIDENT",
      "DRAGON_FIREBALL",
      "LIGHTNING_BOLT",
      "AREA_EFFECT_CLOUD",
      "UNKNOWN"
   );
   private static final List<EntityType> ignoredEntities = Collections.unmodifiableList(
      (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ? ignoredEntityNames1_20_6 : ignoredEntityNamesPre1_20_6)
         .stream()
         .map(EntityType::fromName)
         .collect(Collectors.toList())
   );

   public static List<LivingEntity> getEntitiesInRadius(int var0, Entity var1, boolean var2, boolean var3, boolean var4) {
      ArrayList var5 = new ArrayList();
      if (!var4) {
         for (Entity var7 : var1.getNearbyEntities(var0, var0, var0)) {
            if (var7 instanceof Player var8) {
               if (var2) {
                  var8.setMetadata("ae_ignore", new FixedMetadataValue(ASManager.getInstance(), true));
                  EntityDamageByEntityEvent var9 = new EntityDamageByEntityEvent(var1, var8, DamageCause.CUSTOM, 0.0);
                  Bukkit.getPluginManager().callEvent(var9);
                  var8.removeMetadata("ae_ignore", ASManager.getInstance());
                  if (var9.isCancelled() ? var3 : !var3) {
                     continue;
                  }
               }

               var5.add(var8);
            }
         }
      } else {
         for (Entity var11 : var1.getNearbyEntities(var0, var0, var0)) {
            if (var11 instanceof LivingEntity && !(var11 instanceof Player) && !(var11 instanceof ArmorStand)) {
               var5.add((LivingEntity)var11);
            }
         }
      }

      return var5;
   }

   public static List<LivingEntity> getEntitiesInRadius(int var0, Entity var1, AreaUtils.RadiusTarget var2) {
      ArrayList var3 = new ArrayList();
      switch (var2) {
         case ALL:
            for (Entity var13 : var1.getNearbyEntities(var0, var0, var0)) {
               if (var13 instanceof LivingEntity && !ignoredEntities.contains(var13.getType())) {
                  var3.add((LivingEntity)var13);
               }
            }
            break;
         case PLAYERS:
            for (Entity var12 : var1.getNearbyEntities(var0, var0, var0)) {
               if (var12 instanceof Player) {
                  var3.add((Player)var12);
               }
            }
            break;
         case MOBS:
            for (Entity var11 : var1.getNearbyEntities(var0, var0, var0)) {
               if (var11 instanceof LivingEntity && isDamageable(var1, var11) && !ignoredEntities.contains(var11.getType()) && !(var11 instanceof Player)) {
                  var3.add((LivingEntity)var11);
               }
            }
            break;
         case DAMAGEABLE:
            for (Entity var10 : var1.getNearbyEntities(var0, var0, var0)) {
               if (var10 instanceof LivingEntity && isDamageable(var1, var10) && !ignoredEntities.contains(var10.getType())) {
                  var3.add((LivingEntity)var10);
               }
            }
            break;
         case UNDAMAGEABLE:
            for (Entity var5 : var1.getNearbyEntities(var0, var0, var0)) {
               if (var5 instanceof LivingEntity && !isDamageable(var1, var5) && !ignoredEntities.contains(var5.getType())) {
                  var3.add((LivingEntity)var5);
               }
            }
      }

      return var3;
   }

   public static boolean isDamageable(Entity var0, Entity var1) {
      var1.setMetadata("ae_ignore", new FixedMetadataValue(ASManager.getInstance(), true));
      EntityDamageByEntityEvent var2 = new EntityDamageByEntityEvent(var0, var1, DamageCause.CUSTOM, 0.0);
      Bukkit.getPluginManager().callEvent(var2);
      var1.removeMetadata("ae_ignore", ASManager.getInstance());
      if (var1 instanceof Player var3) {
         if (var3.getGameMode() == GameMode.CREATIVE || var3.getGameMode() == GameMode.SPECTATOR) {
            return false;
         }

         if (var0 instanceof Player && HooksHandler.isEnabled(HookPlugin.FACTIONS)) {
            FactionsPluginHook var4 = (FactionsPluginHook)HooksHandler.getHook(HookPlugin.FACTIONS);
            String var5 = var4.getRelation(var3, (Player)var0);
            if (var5.equals("member")) {
               return false;
            }
         }
      }

      return !var2.isCancelled();
   }

   public static enum RadiusTarget {
      ALL,
      PLAYERS,
      MOBS,
      DAMAGEABLE,
      UNDAMAGEABLE;
   }
}
