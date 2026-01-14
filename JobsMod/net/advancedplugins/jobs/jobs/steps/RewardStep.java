package net.advancedplugins.jobs.jobs.steps;

import java.math.BigDecimal;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.controller.BoostersController;
import net.advancedplugins.jobs.controller.JobController;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.objects.users.User;
import net.advancedplugins.jobs.objects.users.UserJobInfo;

public class RewardStep {
   private final Core plugin;
   private final JobController controller;

   public RewardStep(Core var1) {
      this.plugin = var1;
      this.controller = var1.getJobController();
   }

   public void process(User var1, Job var2) {
      this.plugin.runSync(() -> {
         UserJobInfo var3 = this.controller.getJobInfo(var1, var2);
         double var4 = var2.getRewardedPoints(var3.getLevel()).doubleValue();
         var4 *= 1.0 + this.plugin.getBoostersController().calculateFinalBooster(var1.getUuid(), BoostersController.BoosterType.POINTS, var2) / 100.0;
         this.controller.givePoints(var1, var4);
         this.controller.levelUp(var1, var2);
         var3.setProgress(BigDecimal.ZERO);
      });
   }
}
