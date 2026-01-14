package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.Material;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MilkQuest extends ActionContainer {
   public MilkQuest(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onMilk(PlayerInteractEntityEvent var1) {
      Player var2 = var1.getPlayer();
      if (var1.getRightClicked() instanceof Cow && var2.getItemInHand().getType() == Material.BUCKET) {
         this.executionBuilder("milk").player(var2).canBeAsync().progressSingle().buildAndExecute();
      }
   }
}
