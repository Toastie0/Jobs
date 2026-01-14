package net.advancedplugins.jobs.commands.jobs;

import java.util.Optional;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.commands.JobSubCommand;
import net.advancedplugins.jobs.menus.menus.ProgressMenu;
import net.advancedplugins.jobs.objects.job.Job;
import org.bukkit.entity.Player;

public class JobProgressSub extends JobSubCommand<Player> {
   private final Core core;

   public JobProgressSub(Core var1) {
      super(var1);
      this.core = var1;
      this.inheritPermission();
      this.addFlat("progress");
      this.addArgument(Job.class, "job", var1x -> this.core.getJobCache().getAllJobs().stream().map(Job::getId).collect(Collectors.toList()), new String[0]);
   }

   public void onExecute(Player var1, String[] var2) {
      Job var3 = this.<Optional<Job>>parseArgument(var2, 1).orElse(null);
      if (var3 != null) {
         ProgressMenu var4 = new ProgressMenu(this.plugin, this.plugin.getConfig("progress-menu"), var1, var3);
         this.plugin.getMenuFactory().initiateMenu(var1, () -> var4).show();
      }
   }
}
