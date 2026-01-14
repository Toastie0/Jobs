package net.advancedplugins.jobs.events.admin;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JobsReloadEvent extends Event {
   private static final HandlerList handlers = new HandlerList();

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public HandlerList getHandlers() {
      return handlers;
   }
}
