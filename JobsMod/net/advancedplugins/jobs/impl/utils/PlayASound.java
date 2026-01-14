package net.advancedplugins.jobs.impl.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlayASound {
   private static final Cache<String, Boolean> warnedSounds = CacheBuilder.newBuilder().maximumSize(1000L).build();

   private static Sound getSound(String var0) {
      if (var0 == null) {
         return null;
      } else if (var0.equalsIgnoreCase("none")) {
         return null;
      } else if (var0.isEmpty()) {
         return null;
      } else {
         try {
            return Sound.valueOf(var0);
         } catch (IllegalArgumentException var7) {
            if (var0.contains("_")) {
               String[] var3 = var0.split("_");
               if (var3.length < 3) {
                  String var4 = var3[1] + "_" + var3[0];

                  try {
                     return Sound.valueOf(var4);
                  } catch (IllegalArgumentException var6) {
                     return null;
                  }
               }
            }

            return null;
         }
      }
   }

   private static void warn(String var0) {
      if (warnedSounds.getIfPresent(var0) == null) {
         Bukkit.getLogger().warning("Sound " + var0 + " couldn't be found: invalid sound for this minecraft version?");
         warnedSounds.put(var0, true);
      }
   }

   public static void playSound(String var0, Player var1) {
      playSound(var0, var1, 1.0F, 1.0F);
   }

   public static void playSound(String var0, Player var1, float var2, float var3) {
      Sound var4 = getSound(var0);
      if (var4 == null) {
         warn(var0);
      } else {
         var1.playSound(var1.getLocation(), var4, var3, var2);
      }
   }

   public static void playSound(String var0, Location var1) {
      playSound(var0, var1, 1.0F, 1.0F);
   }

   public static void playSound(String var0, Location var1, float var2, float var3) {
      Sound var4 = getSound(var0);
      if (var4 == null) {
         warn(var0);
      } else {
         var1.getWorld().playSound(var1, var4, var3, var2);
      }
   }
}
