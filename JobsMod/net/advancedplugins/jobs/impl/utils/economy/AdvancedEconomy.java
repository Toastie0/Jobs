package net.advancedplugins.jobs.impl.utils.economy;

import org.bukkit.entity.Player;

public interface AdvancedEconomy {
   String getName();

   boolean chargeUser(Player var1, double var2);

   double getBalance(Player var1);

   boolean giveUser(Player var1, double var2);
}
