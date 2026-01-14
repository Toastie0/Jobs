package net.advancedplugins.jobs.impl.utils;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class TotemUndying {
   public boolean playEffect(@NotNull LivingEntity var1, @NotNull EquipmentSlot var2, int var3, int var4, int var5, boolean var6) {
      if (var6) {
         EntityResurrectEvent var7 = new EntityResurrectEvent(var1, var2);
         Bukkit.getPluginManager().callEvent(var7);
         if (var7.isCancelled()) {
            return false;
         }
      }

      ArrayList var8 = new ArrayList(var1.getActivePotionEffects());
      var8.forEach(var1x -> var1.removePotionEffect(var1x.getType()));
      var1.playEffect(EntityEffect.TOTEM_RESURRECT);
      var1.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, var3, 2));
      var1.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, var4, 1));
      var1.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, var5, 2));
      return true;
   }
}
