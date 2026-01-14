package net.advancedplugins.jobs.impl.actions.external;

import net.advancedplugins.chat.api.AdvancedChatEvent;
import net.advancedplugins.jobs.impl.actions.containers.ExternalActionContainer;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedChatQuests extends ExternalActionContainer {
   private final JavaPlugin plugin;

   public AdvancedChatQuests(JavaPlugin var1) {
      super(var1, "advancedchat");
      this.plugin = var1;
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.MONITOR
   )
   public void onPlayerChat(AdvancedChatEvent var1) {
      FoliaScheduler.runTask(this.plugin, () -> {
         Player var2 = var1.getChatEvent().getPlayer();
         String var3 = var1.getMessage().toLowerCase();
         this.executionBuilder("chat-stripped").player(var2).root(ChatColor.stripColor(var3)).progressSingle().canBeAsync().buildAndExecute();
         this.executionBuilder("chat").player(var2).root(var3).progressSingle().canBeAsync().buildAndExecute();
      });
   }
}
