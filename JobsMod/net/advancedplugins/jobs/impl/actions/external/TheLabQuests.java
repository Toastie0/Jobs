package net.advancedplugins.jobs.impl.actions.external;

import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import net.advancedplugins.jobs.impl.actions.objects.variable.ActionResult;
import net.advancedplugins.jobs.impl.utils.trycatch.TryCatchUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import ro.fr33styler.thelab.api.engine.event.game.player.GameJoinEvent;
import ro.fr33styler.thelab.api.engine.event.game.player.GameLeaveEvent;

public class TheLabQuests extends ActionQuestExecutor {
   public TheLabQuests(JavaPlugin var1) {
      super(var1, "thelab");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onGameEnd(GameLeaveEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = TryCatchUtil.tryAndReturn(
         () -> (String)Class.forName("ro.fr33styler.thelab.api.engine.event.game.player.GameLeaveEvent$Outcome")
            .getMethod("name")
            .invoke(null, var1.getOutcome())
      );
      switch (var3) {
         case "WON":
            this.execute("win", var2, ActionResult::none);
            break;
         case "LOST":
            this.execute("finish", var2, ActionResult::none);
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onGameJoin(GameJoinEvent var1) {
      Player var2 = var1.getPlayer();
      this.execute("join", var2, ActionResult::none);
   }
}
