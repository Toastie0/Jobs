package net.advancedplugins.jobs.impl.actions.external;

import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import se.file14.procosmetics.api.events.PlayerOpenTreasureEvent;
import se.file14.procosmetics.api.events.PlayerPurchaseCosmeticEvent;
import se.file14.procosmetics.api.events.PlayerPurchaseTreasureEvent;

public class ProCosmeticsQuests extends ActionQuestExecutor {
   public ProCosmeticsQuests(JavaPlugin var1) {
      super(var1, "procosmetics");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onTreasurePurchase(PlayerPurchaseTreasureEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getTreasure().getName();
      int var4 = var1.getTreasure().getCost();
      this.execute("spend", var2, var4, var0 -> var0.root("treasure-chest"));
      this.execute("buy_treasure", var2, var1x -> var1x.root(var3));
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerOpenTreasure(PlayerOpenTreasureEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getTreasure().getName();
      this.execute("open_treasure", var2, var1x -> var1x.root(var3));
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onCosmeticPurchase(PlayerPurchaseCosmeticEvent var1) {
      Player var2 = var1.getPlayer();
      int var3 = var1.getCosmeticType().getCost();
      String var4 = var1.getCosmeticType().getName();
      this.execute("buy_cosmetic", var2, var1x -> var1x.root(var4));
      this.execute("spend", var2, var3, var0 -> var0.root("cosmetic"));
   }
}
