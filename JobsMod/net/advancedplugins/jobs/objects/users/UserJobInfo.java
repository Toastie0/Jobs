package net.advancedplugins.jobs.objects.users;

import java.math.BigDecimal;

public class UserJobInfo {
   private String job;
   private int level;
   private BigDecimal progress;
   private boolean active;

   public UserJobInfo(String var1, boolean var2) {
      this.job = var1;
      this.level = 1;
      this.progress = BigDecimal.ZERO;
      this.active = var2;
   }

   public String getJob() {
      return this.job;
   }

   public int getLevel() {
      return this.level;
   }

   public BigDecimal getProgress() {
      return this.progress;
   }

   public boolean isActive() {
      return this.active;
   }

   public void setJob(String var1) {
      this.job = var1;
   }

   public void setLevel(int var1) {
      this.level = var1;
   }

   public void setProgress(BigDecimal var1) {
      this.progress = var1;
   }

   public void setActive(boolean var1) {
      this.active = var1;
   }

   public UserJobInfo(String var1, int var2, BigDecimal var3, boolean var4) {
      this.job = var1;
      this.level = var2;
      this.progress = var3;
      this.active = var4;
   }
}
