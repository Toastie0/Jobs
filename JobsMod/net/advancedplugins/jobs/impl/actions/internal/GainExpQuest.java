package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GainExpQuest extends ActionContainer {
   public GainExpQuest(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerExpChange(PlayerExpChangeEvent var1) {
      Player var2 = var1.getPlayer();
      int var3 = var1.getAmount();
      if (var3 > 0) {
         this.executionBuilder("gain-experience").player(var2).progress(var3).canBeAsync().buildAndExecute();
      }
   }
}
