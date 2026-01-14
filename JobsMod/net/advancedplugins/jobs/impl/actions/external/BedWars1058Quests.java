package net.advancedplugins.jobs.impl.actions.external;

import com.andrei1058.bedwars.api.events.player.PlayerBedBreakEvent;
import com.andrei1058.bedwars.api.events.player.PlayerFirstSpawnEvent;
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent.PlayerKillCause;
import com.andrei1058.bedwars.api.events.shop.ShopBuyEvent;
import com.andrei1058.bedwars.api.events.upgrades.UpgradeBuyEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import net.advancedplugins.jobs.impl.actions.objects.variable.ActionResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class BedWars1058Quests extends ActionQuestExecutor {
   public BedWars1058Quests(JavaPlugin var1) {
      super(var1, "bedwars1058");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerBedBreak(PlayerBedBreakEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getVictimTeam().getName();
      String var4 = var1.getArena().getDisplayName();
      if (var3 != null && var4 != null) {
         this.execute("break_beds", var2, var1x -> var1x.root(var3));
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerKill(PlayerKillEvent var1) {
      Player var2 = var1.getKiller();
      Player var3 = var1.getVictim();
      PlayerKillCause var4 = var1.getCause();
      String var5 = var1.getArena().getDisplayName();
      if (var2 != null && var3 != null && var4 != null && var5 != null) {
         this.execute("kill_players", var2, var1x -> var1x.root(var4.toString()));
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerFirstSpawn(PlayerFirstSpawnEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getArena().getDisplayName();
      String var4 = var1.getTeam().getName();
      if (var3 != null && var4 != null) {
         this.execute("play_games", var2, var1x -> var1x.root(var3));
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onShopBuy(ShopBuyEvent var1) {
      Player var2 = var1.getBuyer();
      this.execute("buy_items", var2, ActionResult::none);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onUpgradeBuy(UpgradeBuyEvent var1) {
      Player var2 = var1.getPlayer();
      this.execute("buy_upgrades", var2, ActionResult::none);
   }
}
