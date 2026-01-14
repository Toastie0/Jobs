package net.advancedplugins.jobs.impl.utils.economy;

import java.util.HashMap;
import java.util.Locale;
import net.advancedplugins.jobs.impl.utils.economy.local.DiamondsEconomy;
import net.advancedplugins.jobs.impl.utils.economy.local.EmeraldsEconomy;
import net.advancedplugins.jobs.impl.utils.economy.local.ExpEconomy;
import net.advancedplugins.jobs.impl.utils.economy.local.GoldEconomy;
import net.advancedplugins.jobs.impl.utils.economy.local.LevelEconomy;
import net.advancedplugins.jobs.impl.utils.economy.local.SoulsEconomy;
import net.advancedplugins.jobs.impl.utils.economy.local.VaultEconomy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyHandler {
   private final HashMap<String, AdvancedEconomy> econMap = new HashMap<>();

   public EconomyHandler(JavaPlugin var1) {
      this.registerEconomy(new ExpEconomy());
      this.registerEconomy(new LevelEconomy());
      this.registerEconomy(new DiamondsEconomy());
      this.registerEconomy(new EmeraldsEconomy());
      this.registerEconomy(new GoldEconomy());
      this.registerEconomy(new SoulsEconomy());
      if (var1.getServer().getPluginManager().isPluginEnabled("Vault")) {
         this.registerEconomy(new VaultEconomy());
      }
   }

   public boolean charge(Player var1, String var2) {
      String var3 = var2.split(":")[0].toUpperCase(Locale.ROOT);
      double var4 = Double.parseDouble(var2.split(":")[1]);
      return this.econMap.get(var3).chargeUser(var1, var4);
   }

   public AdvancedEconomy getEcon(String var1) {
      return this.econMap.get(var1.toUpperCase(Locale.ROOT));
   }

   public boolean registerEconomy(AdvancedEconomy var1) {
      String var2 = var1.getName().toUpperCase(Locale.ROOT);
      if (this.econMap.containsKey(var2)) {
         return false;
      } else {
         this.econMap.put(var2, var1);
         return true;
      }
   }
}
