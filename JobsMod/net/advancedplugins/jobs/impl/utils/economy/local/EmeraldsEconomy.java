package net.advancedplugins.jobs.impl.utils.economy.local;

import java.util.Collections;
import net.advancedplugins.ae.utils.AManager;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.economy.AdvancedEconomy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EmeraldsEconomy implements AdvancedEconomy {
   @Override
   public String getName() {
      return "EMERALDS";
   }

   @Override
   public boolean chargeUser(Player var1, double var2) {
      if (!AManager.hasAmount(var1, Material.EMERALD, (int)var2)) {
         return false;
      } else {
         AManager.removeItems(var1.getInventory(), Material.EMERALD, (int)var2);
         return true;
      }
   }

   @Override
   public double getBalance(Player var1) {
      return AManager.getAmount(var1, Material.EMERALD);
   }

   @Override
   public boolean giveUser(Player var1, double var2) {
      ASManager.giveItem(var1, Collections.nCopies((int)var2, Material.EMERALD).stream().map(ItemStack::new).toArray(ItemStack[]::new));
      return true;
   }
}
