package net.advancedplugins.jobs.objects.job;

import java.util.UUID;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.jobs.JobsResetHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommunityJob {
   private Core core;
   private JobsResetHandler jobHandler;
   private UUID jobCreator;
   private ItemStack request;
   private double payment;

   public CommunityJob(Core var1, Player var2, ItemStack var3, double var4) {
      this.core = var1;
      this.jobHandler = var1.getJobsResetHandler();
      this.jobCreator = var2.getUniqueId();
      this.request = var3;
      this.payment = var4;
      this.post();
   }

   public void finish(Player var1) {
   }

   private void post() {
   }

   public UUID getJobCreatorUUID() {
      return this.jobCreator;
   }

   public ItemStack getRequest() {
      return this.request;
   }

   public double getPayment() {
      return this.payment;
   }
}
