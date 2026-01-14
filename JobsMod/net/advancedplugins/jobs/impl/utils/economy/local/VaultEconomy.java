package net.advancedplugins.jobs.impl.utils.economy.local;

import net.advancedplugins.jobs.impl.utils.economy.AdvancedEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomy implements AdvancedEconomy {
   private Economy econ;

   public VaultEconomy() {
      this.setupEconomy();
   }

   private boolean setupEconomy() {
      if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
         return false;
      } else {
         RegisteredServiceProvider var1 = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
         if (var1 == null) {
            return false;
         } else {
            this.econ = (Economy)var1.getProvider();
            return this.econ != null;
         }
      }
   }

   @Override
   public boolean giveUser(Player var1, double var2) {
      return this.econ.depositPlayer(var1, var2).transactionSuccess();
   }

   @Override
   public String getName() {
      return "MONEY";
   }

   @Override
   public boolean chargeUser(Player var1, double var2) {
      return this.getBalance(var1) < var2 ? false : this.econ.withdrawPlayer(var1, var2).transactionSuccess();
   }

   @Override
   public double getBalance(Player var1) {
      return this.econ.getBalance(var1);
   }
}
