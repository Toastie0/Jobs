package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TameQuest extends ActionContainer {
   public TameQuest(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onTaming(EntityTameEvent var1) {
      if (var1.getOwner() instanceof Player) {
         Player var2 = (Player)var1.getOwner();
         LivingEntity var3 = var1.getEntity();
         String var4 = var1.getEntity().getCustomName();
         this.executionBuilder("tame").player(var2).root(var3).subRoot("name", var4).progressSingle().buildAndExecute();
      }
   }
}
