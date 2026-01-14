package net.advancedplugins.jobs.commands.jobs;

import java.util.ArrayList;
import java.util.Optional;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.cache.UserCache;
import net.advancedplugins.jobs.commands.JobSubCommand;
import net.advancedplugins.jobs.controller.JobController;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.objects.users.User;
import org.bukkit.entity.Player;

public class JobLeaveSub extends JobSubCommand<Player> {
   private final JobController controller;
   private final UserCache userCache;

   public JobLeaveSub(Core var1) {
      super(var1);
      this.controller = var1.getJobController();
      this.userCache = var1.getUserCache();
      this.inheritPermission();
      this.addFlat("leave");
      this.addArgument(
         Job.class,
         "job",
         var1x -> new ArrayList<>(this.controller.getActiveJobs(this.userCache.getSync(((Player)var1x).getUniqueId()).orElse(null)).keySet()),
         new String[0]
      );
   }

   public void onExecute(Player var1, String[] var2) {
      Job var3 = this.<Optional<Job>>parseArgument(var2, 1).orElse(null);
      if (var3 != null) {
         User var4 = this.userCache.getSync(var1.getUniqueId()).orElse(null);
         if (var4 != null) {
            boolean var5 = this.controller.getActiveJobs(var4).containsKey(var3.getId());
            if (!var5) {
               var1.sendMessage(Text.modify(this.locale.getString("job-leave.already-left")));
            } else {
               this.controller.leaveJob(var3, var4);
            }
         }
      }
   }
}
