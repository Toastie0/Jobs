package net.advancedplugins.jobs.jobs;

import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.cache.JobCache;
import net.advancedplugins.jobs.cache.UserCache;
import net.advancedplugins.jobs.impl.actions.ActionExecution;
import net.advancedplugins.jobs.jobs.steps.JobsValidationStep;
import net.advancedplugins.jobs.objects.users.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class JobsPipeline {
   private final UserCache userCache;
   private final JobCache jobCache;
   private final JobsValidationStep validationStep;

   public JobsPipeline(Core var1) {
      this.userCache = var1.getUserCache();
      this.jobCache = var1.getJobCache();
      this.validationStep = new JobsValidationStep(var1);
   }

   public void handle(ActionExecution var1) {
      Player var2 = var1.getPlayer();
      if (Bukkit.getPlayer(var2.getName()) != null && !var2.isDead() && var2.isValid()) {
         User var3 = this.userCache.getSync(var2.getUniqueId()).orElse(null);
         if (var3 != null) {
            var1.setUser(var3);
            this.validationStep.processCompletion(var1, this.jobCache.getAllJobs());
         }
      }
   }
}
