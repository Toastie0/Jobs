package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ExecuteCommandQuest extends ActionContainer {
   public ExecuteCommandQuest(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler
   public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getMessage();
      this.executionBuilder("execute-command").player(var2).root(var3).progressSingle().canBeAsync().buildAndExecute();
   }
}
