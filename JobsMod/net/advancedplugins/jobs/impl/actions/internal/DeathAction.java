package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathAction extends ActionContainer {
   public DeathAction(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler
   public void onDeath(EntityDeathEvent var1) {
      LivingEntity var2 = var1.getEntity();
      EntityDamageEvent var3 = var2.getLastDamageCause();
      if (var3 != null && var3.getCause() != null) {
         DamageCause var4 = var3.getCause();
         if (var2 instanceof Player) {
            this.executionBuilder("death").player(((Player)var2).getPlayer()).root(var4.toString()).progressSingle().canBeAsync().buildAndExecute();
         }
      }
   }
}
