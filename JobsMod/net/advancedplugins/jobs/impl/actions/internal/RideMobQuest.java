package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class RideMobQuest extends ActionContainer {
   public RideMobQuest(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onMove(PlayerMoveEvent var1) {
      Player var2 = var1.getPlayer();
      if (var2.getVehicle() != null && (var1.getFrom().getBlockX() != var1.getTo().getBlockX() || var1.getFrom().getBlockZ() != var1.getTo().getBlockZ())) {
         Entity var3 = var2.getVehicle();
         this.executionBuilder("ride-mob").player(var2).root(var3).progressSingle().buildAndExecute();
      }
   }
}
