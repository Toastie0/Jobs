package net.advancedplugins.jobs.impl.actions.external;

import io.github.Leonardo0013YT.UltraSkyWars.api.events.GameFinishEvent;
import io.github.Leonardo0013YT.UltraSkyWars.api.events.GameStartEvent;
import io.github.Leonardo0013YT.UltraSkyWars.api.events.GameWinEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class UltraSkyWarsQuests extends ActionQuestExecutor {
   public UltraSkyWarsQuests(JavaPlugin var1) {
      super(var1, "ultraskywars");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onGameStart(GameStartEvent var1) {
      String var2 = var1.getGame().getName();

      for (Player var4 : var1.getPlayers()) {
         this.execute("start", var4, var1x -> var1x.root(var2));
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onGameEnd(GameFinishEvent var1) {
      String var2 = var1.getGame().getName();

      for (Player var4 : var1.getPlayers()) {
         this.execute("finish", var4, var1x -> var1x.root(var2));
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onGameWin(GameWinEvent var1) {
      String var2 = var1.getGame().getName();

      for (Player var4 : var1.getWinner().getMembers()) {
         this.execute("win", var4, var1x -> var1x.root(var2));
      }
   }
}
