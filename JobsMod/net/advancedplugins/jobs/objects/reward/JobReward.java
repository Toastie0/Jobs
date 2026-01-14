package net.advancedplugins.jobs.objects.reward;

import net.advancedplugins.jobs.objects.IBoostable;
import net.advancedplugins.jobs.objects.job.Job;

public class JobReward implements IBoostable {
   private final Job job;
   private final Reward<?> reward;

   public JobReward(Job var1, Reward<?> var2) {
      this.job = var1;
      this.reward = var2;
   }

   public Job getJob() {
      return this.job;
   }

   public Reward<?> getReward() {
      return this.reward;
   }
}
