package net.advancedplugins.jobs.impl.utils.economy.local;

import net.advancedplugins.ae.features.souls.SoulsAPI;
import net.advancedplugins.ae.utils.ItemInHand;
import net.advancedplugins.jobs.impl.utils.economy.AdvancedEconomy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SoulsEconomy implements AdvancedEconomy {
   @Override
   public String getName() {
      return "SOULS";
   }

   @Override
   public boolean chargeUser(Player var1, double var2) {
      ItemStack var4 = new ItemInHand(var1).get();
      if (this.getBalance(var1) < var2) {
         return false;
      } else {
         new ItemInHand(var1).set(SoulsAPI.useSouls(var4, (int)var2));
         return true;
      }
   }

   @Override
   public double getBalance(Player var1) {
      ItemStack var2 = new ItemInHand(var1).get();
      return SoulsAPI.getSoulsOnItem(var2);
   }

   @Override
   public boolean giveUser(Player var1, double var2) {
      ItemStack var4 = new ItemInHand(var1).get();
      new ItemInHand(var1).set(SoulsAPI.addSouls(var4, (int)var2));
      return true;
   }
}
