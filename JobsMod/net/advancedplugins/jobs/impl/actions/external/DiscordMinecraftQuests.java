package net.advancedplugins.jobs.impl.actions.external;

import me.enderaura.dcmc.api.event.AccountLinkedEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import net.advancedplugins.jobs.impl.actions.objects.variable.ActionResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscordMinecraftQuests extends ActionQuestExecutor {
   public DiscordMinecraftQuests(JavaPlugin var1) {
      super(var1, "discordminecraft");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onAccountLink(AccountLinkedEvent var1) {
      Player var2 = var1.getPlayer().getPlayer();
      this.execute("link", var2, ActionResult::none);
   }
}
