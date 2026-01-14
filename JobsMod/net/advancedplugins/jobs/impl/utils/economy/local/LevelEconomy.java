package net.advancedplugins.jobs.impl.utils.economy.local;

import net.advancedplugins.jobs.impl.utils.economy.AdvancedEconomy;
import org.bukkit.entity.Player;

public class LevelEconomy implements AdvancedEconomy {
   @Override
   public String getName() {
      return "LEVEL";
   }

   @Override
   public boolean chargeUser(Player var1, double var2) {
      if (var1.getLevel() < var2) {
         return false;
      } else {
         var1.setLevel((int)(var1.getLevel() - var2));
         return true;
      }
   }

   @Override
   public double getBalance(Player var1) {
      return var1.getLevel();
   }

   @Override
   public boolean giveUser(Player var1, double var2) {
      var1.setLevel((int)(var1.getLevel() + var2));
      return true;
   }
}
