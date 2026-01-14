package net.advancedplugins.jobs.impl.utils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ExperienceManager {
   @NotNull
   private final Player player;

   public int getTotalExperience() {
      return getExpAtLevel(this.player.getLevel()) + Math.round(getExpToNext(this.player.getLevel()) * this.player.getExp());
   }

   public static int getExpAtLevel(int var0) {
      if (var0 > 30) {
         return (int)(4.5 * var0 * var0 - 162.5 * var0 + 2220.0);
      } else {
         return var0 > 15 ? (int)(2.5 * var0 * var0 - 40.5 * var0 + 360.0) : var0 * var0 + 6 * var0;
      }
   }

   public static double getLevelFromExp(long var0) {
      int var2 = getIntLevelFromExp(var0);
      float var3 = (float)var0 - getExpAtLevel(var2);
      float var4 = var3 / getExpToNext(var2);
      return (double)var2 + var4;
   }

   public static int getIntLevelFromExp(long var0) {
      if (var0 > 1395L) {
         return (int)((Math.sqrt(72L * var0 - 54215.0) + 325.0) / 18.0);
      } else if (var0 > 315L) {
         return (int)(Math.sqrt(40L * var0 - 7839.0) / 10.0 + 8.1);
      } else {
         return var0 > 0L ? (int)(Math.sqrt(var0 + 9.0) - 3.0) : 0;
      }
   }

   private static int getExpToNext(int var0) {
      if (var0 >= 30) {
         return var0 * 9 - 158;
      } else {
         return var0 >= 15 ? var0 * 5 - 38 : var0 * 2 + 7;
      }
   }

   public void changeExp(int var1) {
      var1 += this.getTotalExperience();
      if (var1 < 0) {
         var1 = 0;
      }

      double var2 = getLevelFromExp(var1);
      int var4 = (int)var2;
      this.player.setLevel(var4);
      this.player.setExp((float)(var2 - var4));
   }

   public void setTotalExperience(int var1) {
      setTotalExperience(this.player, var1);
   }

   public static void setTotalExperience(Player var0, int var1) {
      if (var1 < 0) {
         var1 = 0;
      }

      double var2 = getLevelFromExp(var1);
      int var4 = (int)var2;
      var0.setLevel(var4);
      var0.setExp((float)(var2 - var4));
   }

   public ExperienceManager(@NotNull Player var1) {
      if (var1 == null) {
         throw new NullPointerException("player is marked non-null but is null");
      } else {
         this.player = var1;
      }
   }
}
