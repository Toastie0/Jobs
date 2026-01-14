package net.advancedplugins.jobs.impl.actions.external;

import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import net.advancedplugins.jobs.impl.actions.objects.variable.ActionResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import us.talabrek.ultimateskyblock.api.event.InviteEvent;
import us.talabrek.ultimateskyblock.api.event.IslandChatEvent;
import us.talabrek.ultimateskyblock.api.event.MemberJoinedEvent;

public class USkyBlockQuests extends ActionQuestExecutor {
   public USkyBlockQuests(JavaPlugin var1) {
      super(var1, "uskyblock");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onIslandInvite(InviteEvent var1) {
      Player var2 = var1.getPlayer();
      Player var3 = var1.getGuest();
      this.execute("invite", var2, ActionResult::none);
      this.execute("invited", var3, ActionResult::none);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onIslandJoin(MemberJoinedEvent var1) {
      Player var2 = var1.getPlayerInfo().getPlayer();
      this.execute("join", var2, ActionResult::none);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onIslandChat(IslandChatEvent var1) {
      Player var2 = var1.getPlayer();
      this.execute("island_chat", var2, ActionResult::none);
   }
}
