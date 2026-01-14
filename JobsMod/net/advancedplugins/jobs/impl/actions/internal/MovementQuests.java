package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MovementQuests extends ActionContainer {
   private final boolean isNew = MinecraftVersion.isNew();

   public MovementQuests(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onMove(PlayerMoveEvent var1) {
      Player var2 = var1.getPlayer();
      if (var1.getFrom().getBlockX() != var1.getTo().getBlockX() || var1.getFrom().getBlockZ() != var1.getTo().getBlockZ()) {
         Material var3 = var2.getLocation().getBlock().getType();
         boolean var4 = var3.toString().toLowerCase().contains("water") && !var2.isInsideVehicle() && !var2.isFlying();
         boolean var5 = this.isNew && var2.isGliding();
         boolean var6 = var2.isFlying() || this.isNew && var2.isGliding();
         boolean var7 = var2.isSneaking();
         boolean var8 = var2.isSprinting();
         this.executionBuilder("move")
            .player(var2)
            .subRoot("isSwimming", var4)
            .subRoot("isGliding", var5)
            .subRoot("isFlying", var6)
            .subRoot("isSneaking", var7)
            .subRoot("isSprinting", var8)
            .progressSingle()
            .canBeAsync()
            .buildAndExecute();
         if (var4 && !var6 && !var5) {
            this.executionBuilder("swim").player(var2).progressSingle().canBeAsync().buildAndExecute();
         } else {
            if (var5) {
               this.executionBuilder("glide").player(var2).progressSingle().canBeAsync().buildAndExecute();
            }

            if (var6) {
               this.executionBuilder("fly").player(var2).progressSingle().canBeAsync().buildAndExecute();
            } else {
               if (var7 || var8) {
                  this.executionBuilder(var7 ? "sneak" : "sprint").player(var2).progressSingle().canBeAsync().buildAndExecute();
               }
            }
         }
      }
   }
}
