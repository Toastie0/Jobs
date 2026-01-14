package net.advancedplugins.jobs.jobs.steps;

import java.math.BigDecimal;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.controller.JobController;
import net.advancedplugins.jobs.impl.actions.ActionExecution;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.objects.users.User;

public class JobsCompleteStep {
   private final JobController controller;
   private final NotificationStep notificationStep;

   public JobsCompleteStep(Core var1) {
      this.controller = var1.getJobController();
      this.notificationStep = new NotificationStep(var1);
   }

   public void process(ActionExecution var1, Job var2, BigDecimal var3, BigDecimal var4) {
      User var5 = (User)var1.getUser();
      BigDecimal var7 = this.controller.getRequiredProgress(var5, var2);
      BigDecimal var6;
      if (var1.shouldOverrideUpdate()) {
         var6 = var4.min(var7);
         this.controller.setJobProgress(var5, var2, var6);
      } else {
         var6 = this.controller.addJobProgress(var5, var2, var4);
      }

      if (!(var3.doubleValue() >= var7.doubleValue())) {
         this.notificationStep.process(var1.getPlayer(), var5, var2, var3, var6, var1.getQuestType());
      }
   }
}
