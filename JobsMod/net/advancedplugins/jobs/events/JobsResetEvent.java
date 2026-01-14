package net.advancedplugins.jobs.events;

import net.advancedplugins.jobs.Core;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JobsResetEvent extends Event {
   private final HandlerList handlers = new HandlerList();

   public JobsResetEvent() {
      Bukkit.broadcastMessage(Core.getInstance().getLocale().getString("daily-reset-info", "&a[AdvancedJobs] A daily reset has occurred."));
   }

   public HandlerList getHandlers() {
      return this.handlers;
   }
}
