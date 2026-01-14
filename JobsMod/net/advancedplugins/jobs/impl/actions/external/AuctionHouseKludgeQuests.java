package net.advancedplugins.jobs.impl.actions.external;

import com.spawnchunk.auctionhouse.events.AuctionItemEvent;
import com.spawnchunk.auctionhouse.events.ItemAction;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class AuctionHouseKludgeQuests extends ActionQuestExecutor {
   public AuctionHouseKludgeQuests(JavaPlugin var1) {
      super(var1, "auctionhouse_kludge");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onAuctionItemInteract(AuctionItemEvent var1) {
      Player var2 = var1.getSeller().getPlayer();
      int var3 = Math.round(var1.getPrice());
      ItemAction var4 = var1.getItemAction();
      ItemStack var5 = var1.getItem();
      if (var2 != null && var4 != null) {
         switch (var4) {
            case ITEM_LISTED:
               this.execute("list", var2, var5.getAmount(), var1x -> var1x.root(var5));
               this.execute("list_singular", var2, var1x -> var1x.root(var5));
               break;
            case ITEM_SOLD:
               Player var6 = var1.getBuyer().getPlayer();
               if (var6 == null) {
                  return;
               }

               this.execute("buy_singular", var6, var1x -> var1x.root(var5));
               this.execute("sell_singular", var2, var1x -> var1x.root(var5));
               this.execute("buy", var6, var5.getAmount(), var1x -> var1x.root(var5));
               this.execute("sell", var2, var5.getAmount(), var1x -> var1x.root(var5));
               this.execute("profit", var2, var3, var1x -> var1x.root(var5));
               this.execute("spend", var6, var3, var1x -> var1x.root(var5));
         }
      }
   }
}
