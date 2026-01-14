package net.advancedplugins.jobs.impl.actions.external;

import me.clip.chatreaction.events.ReactionWinEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import net.advancedplugins.jobs.impl.actions.objects.variable.ActionResult;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatReactionQuests extends ActionQuestExecutor {
   private final JavaPlugin plugin;

   public ChatReactionQuests(JavaPlugin var1) {
      super(var1, "chatreaction");
      this.plugin = var1;
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onReactionWin(ReactionWinEvent var1) {
      Player var2 = var1.getWinner();
      FoliaScheduler.runTask(this.plugin, () -> this.execute("win", var2, ActionResult::none));
   }
}
