package net.advancedplugins.jobs.impl.actions.external.chestshop;

import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType;
import java.util.Arrays;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import net.advancedplugins.jobs.impl.actions.objects.variable.ActionResult;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestShopQuests extends ActionQuestExecutor {
   public ChestShopQuests(JavaPlugin var1) {
      super(var1, "chestshop");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void afterShopCreate(ShopCreatedEvent var1) {
      Player var2 = var1.getPlayer();
      this.execute("create", var2, ActionResult::none);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onTransaction(TransactionEvent var1) {
      Player var2 = var1.getClient();
      double var3 = var1.getExactPrice().doubleValue();
      ItemStack var5 = Arrays.stream(var1.getStock())
         .filter(var0 -> var0 != null && var0.getType() != Material.AIR)
         .findFirst()
         .orElse(new ItemStack(Material.AIR));
      String var6 = var1.getOwnerAccount().getName();
      Player var7 = Bukkit.getPlayer(var1.getOwnerAccount().getUuid());
      int var8 = var5.getAmount();
      if (var2 != null) {
         if (var1.getTransactionType() == TransactionType.BUY) {
            this.execute("buy", var2, var8, var2x -> var2x.root(var6).subRoot(var5));
            this.execute("spend", var2, (int)Math.round(var3), var1x -> var1x.root(var6));
            if (var7 != null) {
               this.execute("sell", var7, var8, var2x -> var2x.root(var2.getName()).subRoot(var5));
               this.execute("profit", var7, (int)Math.round(var3), var1x -> var1x.root(var2.getName()));
            }
         } else {
            this.execute("sell", var2, var8, var2x -> var2x.root(var6).subRoot(var5));
            this.execute("profit", var2, (int)Math.round(var3), var1x -> var1x.root(var6));
            if (var7 != null) {
               this.execute("buy", var7, var8, var2x -> var2x.root(var2.getName()).subRoot(var5));
               this.execute("spend", var7, (int)Math.round(var3), var1x -> var1x.root(var2.getName()));
            }
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void afterShopCreate(ShopDestroyedEvent var1) {
      Player var2 = var1.getDestroyer();
      this.execute("destroy", var2, ActionResult::none);
   }
}
