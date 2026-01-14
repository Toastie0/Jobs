package net.advancedplugins.jobs.commands.jobs;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.cache.UserCache;
import net.advancedplugins.jobs.commands.JobSubCommand;
import net.advancedplugins.jobs.controller.JobController;
import net.advancedplugins.jobs.jobs.JobsResetHandler;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.objects.users.User;
import org.bukkit.entity.Player;

public class JobJoinSub extends JobSubCommand<Player> {
   private final JobController controller;
   private final UserCache userCache;
   private final JobsResetHandler resetHandler;

   public JobJoinSub(Core var1) {
      super(var1);
      this.controller = var1.getJobController();
      this.userCache = var1.getUserCache();
      this.resetHandler = var1.getJobsResetHandler();
      this.inheritPermission();
      this.addFlat("join");
      this.addArgument(Job.class, "job", var1x -> {
         if (!(var1x instanceof Player)) {
            return new ArrayList<>();
         } else {
            User var2 = this.userCache.getSync(((Player)var1x).getUniqueId()).orElse(null);
            if (var2 == null) {
               return new ArrayList<>();
            } else {
               ArrayList var3 = new ArrayList(this.resetHandler.getCurrentFreeJobs().stream().map(Job::getId).collect(Collectors.toList()));
               if (var2.isPremium()) {
                  var3.addAll(this.resetHandler.getCurrentPremiumJobs().stream().map(Job::getId).collect(Collectors.toList()));
               }

               return var3;
            }
         }
      }, new String[0]);
   }

   public void onExecute(Player var1, String[] var2) {
      Job var3 = this.<Optional<Job>>parseArgument(var2, 1).orElse(null);
      if (var3 != null) {
         if (var3.isPremiumJob()) {
            if (!this.resetHandler.getCurrentPremiumJobs().contains(var3)) {
               return;
            }
         } else if (!this.resetHandler.getCurrentFreeJobs().contains(var3)) {
            return;
         }

         User var4 = this.userCache.getSync(var1.getUniqueId()).orElse(null);
         if (var4 != null) {
            boolean var5 = this.controller.getActiveJobs(var4).containsKey(var3.getId());
            if (!var5) {
               this.controller.joinToJob(var3, var4);
            }
         }
      }
   }
}
