package net.advancedplugins.jobs.impl.utils.hooks.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PreExternalTeleportEvent extends Event implements Cancellable {
   private boolean cancelled;
   private Player player;
   private Location location;

   @NotNull
   public HandlerList getHandlers() {
      return getHandlerList();
   }

   @NotNull
   public static HandlerList getHandlerList() {
      return new HandlerList();
   }

   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }

   public PreExternalTeleportEvent(boolean var1, Player var2, Location var3) {
      this.cancelled = var1;
      this.player = var2;
      this.location = var3;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public Player getPlayer() {
      return this.player;
   }

   public Location getLocation() {
      return this.location;
   }
}
