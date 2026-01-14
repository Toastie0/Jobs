package net.advancedplugins.jobs.impl.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.LivingEntity;

public class ACooldown {
   private static final Map<UUID, Map<String, Double>> cooldown = new HashMap<>();

   public static void reload() {
      cooldown.clear();
   }

   public static boolean isInCooldown(LivingEntity var0, String var1) {
      if (var0 != null && var1 != null) {
         UUID var2 = var0.getUniqueId();
         if (!cooldown.containsKey(var2)) {
            return false;
         } else {
            Map var3 = cooldown.get(var2);
            if (!var3.containsKey(var1)) {
               return false;
            } else {
               double var4 = (Double)var3.get(var1);
               if (var4 >= System.currentTimeMillis()) {
                  return true;
               } else {
                  var3.remove(var1);
                  cooldown.put(var2, var3);
                  return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   public static void putToCooldown(LivingEntity var0, String var1, double var2) {
      if (var0 != null && var1 != null) {
         UUID var4 = var0.getUniqueId();
         Map var5 = cooldown.getOrDefault(var4, new HashMap<>());
         var5.put(var1, System.currentTimeMillis() + var2 * 1000.0);
         cooldown.put(var4, var5);
      }
   }
}
