package net.advancedplugins.jobs.impl.actions.external;

import me.gypopo.economyshopgui.api.events.PostTransactionEvent;
import me.gypopo.economyshopgui.util.Transaction.Result;
import net.advancedplugins.jobs.impl.actions.containers.ExternalActionContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyShopGUIQuests extends ExternalActionContainer {
   public EconomyShopGUIQuests(JavaPlugin var1) {
      super(var1, "economyshopgui");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onTransaction(PostTransactionEvent var1) {
      Player var2 = var1.getPlayer();
      if (var1.getTransactionResult() == Result.SUCCESS) {
         String var3 = var1.getTransactionType().name().contains("SELL") ? "sell" : "buy";
         super.executionBuilder(var3).player(var2).root(var1.getShopItem().getItemToGive()).progress(var1.getAmount()).buildAndExecute();
      }
   }
}
