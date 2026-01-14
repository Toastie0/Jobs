package net.advancedplugins.jobs.impl.actions.external;

import Clans.Events.ClanCreateEvent;
import Clans.Events.ClanJoinEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import net.advancedplugins.jobs.impl.actions.objects.variable.ActionResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ClansQuests extends ActionQuestExecutor {
   public ClansQuests(JavaPlugin var1) {
      super(var1, "clans");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onClanCreate(ClanCreateEvent var1) {
      Player var2 = var1.getOwner();
      this.execute("create", var2, ActionResult::none);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onClanInvite(ClanJoinEvent var1) {
      Player var2 = var1.getPlayer();
      this.execute("join", var2, ActionResult::none);
   }
}
