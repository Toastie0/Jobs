package net.advancedplugins.jobs.impl.actions.external;

import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.buildbattle.api.event.game.BBGameEndEvent;
import plugily.projects.buildbattle.api.event.game.BBGameJoinEvent;
import plugily.projects.buildbattle.api.event.game.BBGameStartEvent;

public class BuildBattleTigerQuests extends ActionQuestExecutor {
   public BuildBattleTigerQuests(JavaPlugin var1) {
      super(var1, "buildbattle");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onGameJoin(BBGameJoinEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getArena().getMapName();
      this.execute("join", var2, var1x -> var1x.root(var3));
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onGameEnd(BBGameStartEvent var1) {
      String var2 = var1.getArena().getMapName();

      for (Player var4 : var1.getArena().getPlayers()) {
         this.execute("play", var4, var1x -> var1x.root(var2));
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onGameEnd(BBGameEndEvent var1) {
      String var2 = var1.getArena().getMapName();

      for (Player var4 : var1.getArena().getPlayers()) {
         this.execute("finish", var4, var1x -> var1x.root(var2));
      }
   }
}
