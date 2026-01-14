package net.advancedplugins.jobs.objects.users;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class User {
   private final UUID uuid;
   private final JobStore jobStore;
   private boolean premium;
   private double points;

   public User(UUID var1) {
      this(var1, new JobStore(), false, 0.0);
   }

   public User(UUID var1, JobStore var2, boolean var3, double var4) {
      this.uuid = var1;
      this.jobStore = var2;
      this.premium = var3;
      this.points = var4;
   }

   public String getPassId() {
      return this.premium ? "premium" : "free";
   }

   public void clearPoints() {
      this.points = 0.0;
   }

   public void addPoints(double var1) {
      this.points += var1;
   }

   public void takePoints(double var1) {
      this.points -= var1;
   }

   public Player getPlayer() {
      return Bukkit.getPlayer(this.uuid);
   }

   public OfflinePlayer getOfflinePlayer() {
      return Bukkit.getOfflinePlayer(this.uuid);
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public JobStore getJobStore() {
      return this.jobStore;
   }

   public boolean isPremium() {
      return this.premium;
   }

   public double getPoints() {
      return this.points;
   }

   public void setPremium(boolean var1) {
      this.premium = var1;
   }

   public void setPoints(double var1) {
      this.points = var1;
   }
}
