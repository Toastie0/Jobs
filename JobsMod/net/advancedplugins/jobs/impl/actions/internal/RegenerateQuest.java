package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.plugin.java.JavaPlugin;

public class RegenerateQuest extends ActionContainer {
   public RegenerateQuest(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onGainHealth(EntityRegainHealthEvent var1) {
      if (var1.getEntity() instanceof Player) {
         Player var2 = (Player)var1.getEntity();
         RegainReason var3 = var1.getRegainReason();
         this.executionBuilder("regenerate").player(var2).progress(var1.getAmount()).root(var3.toString()).canBeAsync().progressSingle().buildAndExecute();
      }
   }
}
