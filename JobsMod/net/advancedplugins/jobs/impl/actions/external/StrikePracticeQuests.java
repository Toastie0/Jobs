package net.advancedplugins.jobs.impl.actions.external;

import ga.strikepractice.events.BotDuelEndEvent;
import ga.strikepractice.events.BotDuelStartEvent;
import ga.strikepractice.events.DuelEndEvent;
import ga.strikepractice.events.DuelStartEvent;
import ga.strikepractice.events.PlayerHostEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class StrikePracticeQuests extends ActionQuestExecutor {
   public StrikePracticeQuests(JavaPlugin var1) {
      super(var1, "strikepractice");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerHost(PlayerHostEvent var1) {
      if (var1.getHost() instanceof Player) {
         Player var2 = (Player)var1.getHost();
         String var3 = var1.getPvPEventName().toLowerCase();
         this.execute("host_events", var2, var1x -> var1x.root(var3));
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onBotDuelStart(BotDuelStartEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getFight().getKit().getName();
      this.execute("play_games", var2, var1x -> var1x.root(var3));
      this.execute("play_bot_games", var2, var1x -> var1x.root(var3));
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onBotDuelEnd(BotDuelEndEvent var1) {
      if (!var1.getWinner().equals(var1.getBot().getFullName())) {
         Player var2 = Bukkit.getPlayer(var1.getWinner());
         String var3 = var1.getFight().getKit().getName();
         this.execute("win_games", var2, var1x -> var1x.root(var3));
         this.execute("win_bot_games", var2, var1x -> var1x.root(var3));
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onDuelStart(DuelStartEvent var1) {
      Player var2 = var1.getPlayer1();
      Player var3 = var1.getPlayer2();
      String var4 = var1.getKit().getName();
      this.execute("play_games", var3, var1x -> var1x.root(var4));
      this.execute("play_games", var2, var1x -> var1x.root(var4));
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onDuelEnd(DuelEndEvent var1) {
      Player var2 = var1.getLoser();
      Player var3 = var1.getWinner();
      String var4 = var1.getFight().getKit().getName();
      this.execute("win_games", var3, var1x -> var1x.root(var4));
      this.execute("lose_games", var2, var1x -> var1x.root(var4));
   }
}
