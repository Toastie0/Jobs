package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.ActionExecutionBuilder;
import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ClickQuest extends ActionContainer {
   public ClickQuest(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent var1) {
      Player var2 = var1.getPlayer();
      Block var3 = var1.getClickedBlock();
      ActionExecutionBuilder var4;
      switch (var1.getAction()) {
         case RIGHT_CLICK_BLOCK:
            if (var3 != null && !var3.getType().equals(Material.AIR) && !var1.isCancelled()) {
               this.executionBuilder("right-click-block").player(var2).root(var3).progressSingle().buildAndExecute();
               return;
            }

            return;
         case RIGHT_CLICK_AIR:
            var4 = this.executionBuilder("right-click");
            break;
         case LEFT_CLICK_AIR:
            var4 = this.executionBuilder("left-click");
            break;
         case LEFT_CLICK_BLOCK:
            if (var3 != null && !var3.getType().equals(Material.AIR) && !var1.isCancelled()) {
               this.executionBuilder("left-click-block").player(var2).root(var3).progressSingle().buildAndExecute();
               return;
            }

            return;
         default:
            return;
      }

      var4.player(var2).progressSingle().canBeAsync().buildAndExecute();
   }
}
