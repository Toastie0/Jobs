package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ShearSheepQuest extends ActionContainer {
   public ShearSheepQuest(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onShear(PlayerShearEntityEvent var1) {
      Player var2 = var1.getPlayer();
      Entity var3 = var1.getEntity();
      String var4 = var1.getEntity().getCustomName();
      if (var3 instanceof Sheep) {
         String var5 = this.or(((Sheep)var3).getColor(), DyeColor.WHITE).name();
         this.executionBuilder("shear").player(var2).root(var4).subRoot("color", var5).canBeAsync().progressSingle().buildAndExecute();
      }
   }

   private <T> T or(T var1, T var2) {
      return (T)(var1 == null ? var2 : var1);
   }
}
