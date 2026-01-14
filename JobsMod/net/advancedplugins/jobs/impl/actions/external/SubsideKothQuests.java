package net.advancedplugins.jobs.impl.actions.external;

import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import subside.plugins.koth.captureentities.Capper;
import subside.plugins.koth.captureentities.CappingPlayer;
import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.events.KothLeftEvent;

public class SubsideKothQuests extends ActionQuestExecutor {
   public SubsideKothQuests(JavaPlugin var1) {
      super(var1, "koth");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onKothEnd(KothEndEvent var1) {
      String var2 = var1.getRunningKoth().getKoth().getName();
      Capper var3 = var1.getWinner();
      if (var3 instanceof CappingPlayer) {
         Player var4 = Bukkit.getPlayer(((OfflinePlayer)var3.getObject()).getUniqueId());
         this.execute("win_cap", var4, var1x -> var1x.root(var2), var1x -> var1x.set("koth_name", var2));
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onKothCap(KothLeftEvent var1) {
      String var2 = var1.getRunningKoth().getKoth().getName();
      Capper var3 = var1.getCapper();
      if (var3 instanceof CappingPlayer) {
         Player var4 = Bukkit.getPlayer(((OfflinePlayer)var3.getObject()).getUniqueId());
         int var5 = var1.getAmountSecondsCapped();
         this.execute("capture", var4, var5, var1x -> var1x.root(var2), var1x -> var1x.set("koth_name", var2));
      }
   }
}
