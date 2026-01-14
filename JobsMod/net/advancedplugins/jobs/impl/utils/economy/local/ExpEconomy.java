package net.advancedplugins.jobs.impl.utils.economy.local;

import net.advancedplugins.jobs.impl.utils.ExperienceManager;
import net.advancedplugins.jobs.impl.utils.economy.AdvancedEconomy;
import org.bukkit.entity.Player;

public class ExpEconomy implements AdvancedEconomy {
   @Override
   public String getName() {
      return "EXP";
   }

   @Override
   public boolean chargeUser(Player var1, double var2) {
      ExperienceManager var4 = new ExperienceManager(var1);
      if (var4.getTotalExperience() < var2) {
         return false;
      } else {
         var4.setTotalExperience((int)(var4.getTotalExperience() - var2));
         return true;
      }
   }

   @Override
   public double getBalance(Player var1) {
      return new ExperienceManager(var1).getTotalExperience();
   }

   @Override
   public boolean giveUser(Player var1, double var2) {
      ExperienceManager var4 = new ExperienceManager(var1);
      var4.setTotalExperience((int)(var4.getTotalExperience() - var2));
      return true;
   }
}
