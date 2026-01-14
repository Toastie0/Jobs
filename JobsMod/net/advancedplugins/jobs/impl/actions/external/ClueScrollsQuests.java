package net.advancedplugins.jobs.impl.actions.external;

import com.electro2560.dev.cluescrolls.events.PlayerClueCompletedEvent;
import com.electro2560.dev.cluescrolls.events.PlayerScrollCompletedEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ClueScrollsQuests extends ActionQuestExecutor {
   public ClueScrollsQuests(JavaPlugin var1) {
      super(var1, "cluescrolls");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onClueCompleted(PlayerClueCompletedEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getClueType();
      this.execute("complete_clue", var2, var1x -> var1x.root(var3));
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onScrollCompleted(PlayerScrollCompletedEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getTierType();
      this.execute("complete_scroll", var2, var1x -> var1x.root(var3));
   }
}
