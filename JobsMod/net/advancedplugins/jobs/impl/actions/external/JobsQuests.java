package net.advancedplugins.jobs.impl.actions.external;

import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsJoinEvent;
import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class JobsQuests extends ActionQuestExecutor {
   public JobsQuests(JavaPlugin var1) {
      super(var1, "jobs");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onJobJoin(JobsJoinEvent var1) {
      Player var2 = var1.getPlayer().getPlayer();
      String var3 = var1.getJob().getName();
      this.execute("join", var2, var1x -> var1x.root(var3));
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onExpGain(JobsExpGainEvent var1) {
      Player var2 = var1.getPlayer().getPlayer();
      String var3 = var1.getJob().getName();
      this.execute("gain_exp", var2, (int)Math.max(1.0, var1.getExp()), var1x -> var1x.root(var3));
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onLevelUp(JobsLevelUpEvent var1) {
      Player var2 = var1.getPlayer().getPlayer();
      String var3 = var1.getJob().getName();
      this.execute("level_up", var2, var1.getLevel(), var1x -> var1x.root(var3), var0 -> var0, true);
   }
}
