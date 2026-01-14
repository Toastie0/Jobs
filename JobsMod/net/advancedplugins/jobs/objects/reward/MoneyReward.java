package net.advancedplugins.jobs.objects.reward;

import com.google.common.collect.Multiset;
import java.util.Map;
import net.advancedplugins.jobs.Core;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class MoneyReward extends Reward<String> {
   private final Economy econ;

   public MoneyReward(String var1, String var2, Map<String, String> var3, Multiset<String> var4) {
      super(var1, var2, var3, var4);
      RegisteredServiceProvider var5 = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
      if (var5 == null) {
         this.econ = null;
         Core.getInstance().getLogger().warning("No economy dependency found! It's required for 'money' reward");
      } else {
         this.econ = (Economy)var5.getProvider();
      }
   }

   @Override
   public void reward(Player var1, String var2, int var3, String var4, double var5) {
      String var7 = (String)this.set.stream().findFirst().orElse(null);
      if (var7 != null) {
         double var8 = Double.parseDouble(this.fillVariables(var7, var3, var1, var4, var5));
         this.econ.depositPlayer(var1, var8);
      }
   }
}
