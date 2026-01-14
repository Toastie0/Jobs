package net.advancedplugins.jobs.impl.actions.external;

import com.nisovin.shopkeepers.api.events.ShopkeeperOpenUIEvent;
import com.nisovin.shopkeepers.api.events.ShopkeeperTradeEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopkeepersQuests extends ActionQuestExecutor {
   public ShopkeepersQuests(JavaPlugin var1) {
      super(var1, "shopkeepers");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onShopkeeperTrade(ShopkeeperTradeEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getShopkeeper().getUniqueId().toString().toLowerCase();
      this.execute("trade", var2, var1x -> var1x.root(var3));
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onShopkeeperOpen(ShopkeeperOpenUIEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getShopkeeper().getUniqueId().toString().toLowerCase();
      this.execute("open", var2, var1x -> var1x.root(var3));
   }
}
