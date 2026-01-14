package net.advancedplugins.jobs.events;

import java.util.function.Consumer;
import net.advancedplugins.jobs.objects.users.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserPointsGetEvent extends Event implements Cancellable {
   private static final HandlerList HANDLER_LIST = new HandlerList();
   private final User user;
   private final double points;
   private boolean cancelled;

   public UserPointsGetEvent(User var1, double var2) {
      this.user = var1;
      this.points = var2;
   }

   public void ifNotCancelled(Consumer<UserPointsGetEvent> var1) {
      if (!this.cancelled) {
         var1.accept(this);
      }
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }

   public static HandlerList getHandlerList() {
      return HANDLER_LIST;
   }

   @NotNull
   public HandlerList getHandlers() {
      return HANDLER_LIST;
   }

   public User getUser() {
      return this.user;
   }

   public double getPoints() {
      return this.points;
   }
}
