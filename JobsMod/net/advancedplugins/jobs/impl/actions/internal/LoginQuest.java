package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LoginQuest extends ActionContainer {
   private final JavaPlugin plugin;

   public LoginQuest(JavaPlugin var1) {
      super(var1);
      this.plugin = var1;
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onJoin(PlayerJoinEvent var1) {
      Player var2 = var1.getPlayer();
      if (var2.isOnline()) {
         FoliaScheduler.runTaskLater(this.plugin, () -> this.executionBuilder("login").player(var2).canBeAsync().progressSingle().buildAndExecute(), 80L);
      }
   }
}
