package net.advancedplugins.jobs.impl.actions.external;

import com.vexsoftware.votifier.model.VotifierEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class VotifierQuests extends ActionQuestExecutor {
   public VotifierQuests(JavaPlugin var1) {
      super(var1, "votifier");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onVote(VotifierEvent var1) {
      Player var2 = Bukkit.getPlayer(var1.getVote().getUsername());
      String var3 = var1.getVote().getServiceName();
      this.execute("vote", var2, var1x -> var1x.root(var3));
   }
}
