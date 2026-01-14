package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayTimeQuest extends ActionContainer {
   public PlayTimeQuest(JavaPlugin var1) {
      super(var1);
      this.run(var1);
   }

   public void run(JavaPlugin var1) {
      FoliaScheduler.runTaskTimer(var1, () -> {
         for (Player var2 : Bukkit.getOnlinePlayers()) {
            this.executionBuilder("playtime").player(var2).canBeAsync().progress(5).buildAndExecute();
         }
      }, 100L, 100L);
   }
}
