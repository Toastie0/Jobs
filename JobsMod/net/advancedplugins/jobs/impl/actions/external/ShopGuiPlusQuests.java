package net.advancedplugins.jobs.impl.actions.external;

import java.util.function.UnaryOperator;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import net.brcdev.shopgui.event.ShopPostTransactionEvent;
import net.brcdev.shopgui.shop.ShopTransactionResult;
import net.brcdev.shopgui.shop.ShopManager.ShopAction;
import net.brcdev.shopgui.shop.ShopTransactionResult.ShopTransactionResultType;
import net.brcdev.shopgui.shop.item.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopGuiPlusQuests extends ActionQuestExecutor {
   public ShopGuiPlusQuests(JavaPlugin var1) {
      super(var1, "shopguiplus");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void afterShopTransaction(ShopPostTransactionEvent var1) {
      ShopTransactionResult var2 = var1.getResult();
      if (var2.getResult() == ShopTransactionResultType.SUCCESS) {
         Player var3 = var2.getPlayer();
         ShopAction var4 = var2.getShopAction();
         ShopItem var5 = var2.getShopItem();
         String var6 = var5.getShop().getId();
         int var7 = var2.getAmount();
         int var8 = (int)Math.ceil(var2.getPrice());
         UnaryOperator var9 = var2x -> var2x.root(var5.getItem()).subRoot("shop", var6).subRoot("item-id", var5.getId());
         if (var4 == ShopAction.BUY) {
            super.execute("buy", var3, var7, var9);
            super.execute("buy_singular", var3, var9);
            super.execute("spend", var3, var8, var9);
         } else {
            super.execute("sell", var3, var7, var9);
            super.execute("sell_singular", var3, var9);
            super.execute("profit", var3, var8, var9);
         }
      }
   }
}
