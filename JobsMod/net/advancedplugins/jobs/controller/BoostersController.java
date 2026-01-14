package net.advancedplugins.jobs.controller;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.cache.JobCache;
import net.advancedplugins.jobs.cache.RewardCache;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.jobs.jobs.JobsResetHandler;
import net.advancedplugins.jobs.locale.LocaleHandler;
import net.advancedplugins.jobs.objects.IBoostable;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.objects.reward.JobReward;
import net.advancedplugins.simplespigot.service.simple.Simple;
import net.advancedplugins.simplespigot.storage.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BoostersController {
   private Map<BoostersController.BoosterType, List<BoostersController.Booster>> serverBoosters = new HashMap<>();
   private Map<UUID, Map<BoostersController.BoosterType, List<BoostersController.Booster>>> playerBoosters = new HashMap<>();
   private final Map<BoostersController.BoosterType, Map<String, BoostersController.Booster>> permissionBoosters;
   private final BoostersController.BoostersStrategy strategy;
   private final LocaleHandler lang;
   private final JobCache jobCache;
   private final RewardCache rewardCache;
   private final JobsResetHandler resetHandler;
   private final Core plugin;
   private final List<BukkitTask> runnables;
   private final Storage<BoostersController> storage;

   public BoostersController(Core var1) {
      String var2 = var1.getConfig("config").string("boosters-strategy");
      if (var2 == null) {
         var2 = BoostersController.BoostersStrategy.SUM_DIFFERENT.name();
      }

      this.strategy = BoostersController.BoostersStrategy.valueOf(var2.toUpperCase());
      this.lang = var1.getLocale();
      this.jobCache = var1.getJobCache();
      this.rewardCache = var1.getRewardCache();
      this.resetHandler = var1.getJobsResetHandler();
      this.plugin = var1;
      this.runnables = new ArrayList<>();
      this.storage = var1.getBoostersStorage();
      this.permissionBoosters = new HashMap<>();
      var1.getConfig("config")
         .getConfiguration()
         .getConfigurationSection("permission-boosters")
         .getValues(false)
         .forEach(
            (var1x, var2x) -> {
               ConfigurationSection var3 = (ConfigurationSection)var2x;
               BoostersController.Booster var4 = new BoostersController.Booster(
                  "permission",
                  var3.getDouble("percent"),
                  var3.getString("affects"),
                  Long.MAX_VALUE,
                  BoostersController.BoosterType.valueOf(var3.getString("type").toUpperCase()),
                  BoostersController.BoosterTarget.PLAYER
               );
               var1x = "advancedjobs.booster.".concat(var1x);
               this.permissionBoosters.putIfAbsent(var4.getType(), new HashMap<>());
               this.permissionBoosters.get(var4.getType()).put(var1x, var4);
            }
         );
   }

   public BoostersController(
      Core var1,
      Map<BoostersController.BoosterType, List<BoostersController.Booster>> var2,
      Map<UUID, Map<BoostersController.BoosterType, List<BoostersController.Booster>>> var3
   ) {
      this(var1);
      this.serverBoosters = var2;
      this.playerBoosters = var3;
   }

   public void refresh(BoostersController var1) {
      this.refresh(var1.getServerBoosters(), var1.getPlayerBoosters());
   }

   public void refresh(
      Map<BoostersController.BoosterType, List<BoostersController.Booster>> var1,
      Map<UUID, Map<BoostersController.BoosterType, List<BoostersController.Booster>>> var2
   ) {
      if (!var1.equals(this.serverBoosters) || !var2.equals(this.playerBoosters)) {
         this.runnables.stream().forEach(BukkitTask::cancel);
         this.runnables.clear();
         Map var3 = this.serverBoosters;
         Map var4 = this.playerBoosters;
         this.serverBoosters = var1;
         this.playerBoosters = var2;
         var3.forEach(
            (var1x, var2x) -> var2x.forEach(
               var2xx -> {
                  if (!this.serverBoosters.getOrDefault(var1x, new ArrayList<>()).contains(var2xx)) {
                     if (var2xx.getExpires() < System.currentTimeMillis()) {
                        return;
                     }

                     this.lang
                        .toAll(
                           "boosters.serverEnded",
                           var2xxx -> var2xxx.set("percent", var2xx.getPercent())
                              .set("type", this.lang.getString("boosters." + var2xx.getType().name().toLowerCase()))
                              .set("affects", this.formatBoosterAffects(var2xx))
                        );
                  }
               }
            )
         );
         var4.forEach(
            (var1x, var2x) -> var2x.forEach(
               (var2xx, var3x) -> var3x.forEach(
                  var3xx -> {
                     if (!this.playerBoosters.getOrDefault(var1x, new HashMap<>()).getOrDefault(var2xx, new ArrayList<>()).contains(var3xx)) {
                        Player var4x = Bukkit.getPlayer(var1x);
                        if (var4x != null) {
                           if (var3xx.getExpires() < System.currentTimeMillis()) {
                              return;
                           }

                           this.lang
                              .to(
                                 "boosters.playerEnded",
                                 var2xxxx -> var2xxxx.set("percent", var3xx.getPercent())
                                    .set("type", this.lang.getString("boosters." + var3xx.getType().name().toLowerCase()))
                                    .set("time", this.getFormattedExpirationDate(var3xx))
                                    .set("affects", this.formatBoosterAffects(var3xx)),
                                 var4x
                              );
                        }
                     }
                  }
               )
            )
         );
         this.serverBoosters
            .forEach(
               (var2x, var3x) -> var3x.forEach(
                  var3xx -> {
                     if (!var3.getOrDefault(var2x, new ArrayList()).contains(var3xx)) {
                        if (var3xx.getExpires() < System.currentTimeMillis()) {
                           return;
                        }

                        this.lang
                           .toAll(
                              "boosters.serverStarted",
                              var2xxx -> var2xxx.set("percent", var3xx.getPercent())
                                 .set("type", this.lang.getString("boosters." + var3xx.getType().name().toLowerCase()))
                                 .set("time", this.getFormattedExpirationDate(var3xx))
                                 .set("affects", this.formatBoosterAffects(var3xx))
                           );
                     }
                  }
               )
            );
         this.playerBoosters
            .forEach(
               (var2x, var3x) -> var3x.forEach(
                  (var3xx, var4x) -> var4x.forEach(
                     var4xx -> {
                        if (!var4.getOrDefault(var2x, new HashMap()).getOrDefault(var3xx, new ArrayList()).contains(var4xx)) {
                           Player var5 = Bukkit.getPlayer(var2x);
                           if (var5 != null) {
                              if (var4xx.getExpires() < System.currentTimeMillis()) {
                                 return;
                              }

                              this.lang
                                 .to(
                                    "boosters.playerStarted",
                                    var2xxxx -> var2xxxx.set("percent", var4xx.getPercent())
                                       .set("type", this.lang.getString("boosters." + var4xx.getType().name().toLowerCase()))
                                       .set("time", this.getFormattedExpirationDate(var4xx))
                                       .set("affects", this.formatBoosterAffects(var4xx)),
                                    var5
                                 );
                           }
                        }
                     }
                  )
               )
            );
         this.serverBoosters.forEach((var1x, var2x) -> var2x.removeIf(var1xx -> {
            if (var1xx.getExpires() <= System.currentTimeMillis()) {
               return true;
            } else {
               this.createEndRunnable(var1xx.getTarget(), var1xx, null);
               return false;
            }
         }));
         this.playerBoosters.forEach((var1x, var2x) -> var2x.forEach((var2xx, var3x) -> var3x.removeIf(var2xxx -> {
            if (var2xxx.getExpires() <= System.currentTimeMillis()) {
               return true;
            } else {
               this.createEndRunnable(var2xxx.getTarget(), var2xxx, var1x);
               return false;
            }
         })));
      }
   }

   public void addServerBooster(BoostersController.Booster var1) {
      this.addBooster(var1, BoostersController.BoosterTarget.SERVER, null);
   }

   public void addBooster(BoostersController.Booster var1, BoostersController.BoosterTarget var2, UUID var3) {
      if (var2 == BoostersController.BoosterTarget.SERVER) {
         this.serverBoosters.putIfAbsent(var1.getType(), new ArrayList<>());
         this.serverBoosters.get(var1.getType()).add(var1);
         this.lang
            .toAll(
               "boosters.serverStarted",
               var2x -> var2x.set("percent", var1.getPercent())
                  .set("type", this.lang.getString("boosters." + var1.getType().name().toLowerCase()))
                  .set("time", this.getFormattedExpirationDate(var1))
                  .set("affects", this.formatBoosterAffects(var1))
            );
      } else {
         this.playerBoosters.putIfAbsent(var3, new HashMap<>());
         this.playerBoosters.get(var3).putIfAbsent(var1.getType(), new ArrayList<>());
         this.playerBoosters.get(var3).get(var1.getType()).add(var1);
         Player var4 = Bukkit.getPlayer(var3);
         if (var4 != null) {
            this.lang
               .to(
                  "boosters.playerStarted",
                  var2x -> var2x.set("percent", var1.getPercent())
                     .set("type", this.lang.getString("boosters." + var1.getType().name().toLowerCase()))
                     .set("time", this.getFormattedExpirationDate(var1))
                     .set("affects", this.formatBoosterAffects(var1)),
                  var4
               );
         }
      }

      this.createEndRunnable(var2, var1, var3);
      this.storage.save("boosters-data", this);
   }

   public String getMenuLore(Player var1, IBoostable var2) {
      return this.getMenuLore(var1, var2, null);
   }

   public String getMenuLore(Player var1, IBoostable var2, BoostersController.BoosterType var3) {
      HashMap var4 = new HashMap();
      if (var2 instanceof JobReward) {
         double var5 = this.calculateFinalBooster(var1.getUniqueId(), BoostersController.BoosterType.REWARDS, var2);
         if (var5 > 0.0) {
            var4.put(BoostersController.BoosterType.REWARDS, var5);
         }
      } else {
         double var9 = this.calculateFinalBooster(var1.getUniqueId(), BoostersController.BoosterType.PROGRESS, var2);
         double var7 = this.calculateFinalBooster(var1.getUniqueId(), BoostersController.BoosterType.POINTS, var2);
         if (var9 > 0.0) {
            var4.put(BoostersController.BoosterType.PROGRESS, var9);
         }

         if (var7 > 0.0) {
            var4.put(BoostersController.BoosterType.POINTS, var7);
         }
      }

      ArrayList var10 = new ArrayList();
      var4.forEach(
         (var3x, var4x) -> {
            if (var3 == null || var3x == var3) {
               var10.add(
                  Text.modify(
                     this.lang.getString("boosters.menu"),
                     var3xx -> var3xx.set("booster", var4x).set("type", this.lang.getString("boosters." + var3x.name().toLowerCase()))
                  )
               );
            }
         }
      );
      return String.join("<new>", var10);
   }

   public void clearBoosters(BoostersController.BoosterTarget var1, UUID var2) {
      if (var1 == BoostersController.BoosterTarget.SERVER) {
         this.serverBoosters
            .forEach(
               (var1x, var2x) -> var2x.forEach(
                  var1xx -> this.lang
                     .toAll(
                        "boosters.serverEnded",
                        var2xx -> var2xx.set("percent", var1xx.getPercent())
                           .set("type", this.lang.getString("boosters." + var1xx.getType().name().toLowerCase()))
                           .set("affects", this.formatBoosterAffects(var1xx))
                     )
               )
            );
         this.serverBoosters.clear();
      } else {
         Player var3 = Bukkit.getPlayer(var2);
         if (var3 != null) {
            this.playerBoosters
               .getOrDefault(var2, new HashMap<>())
               .forEach(
                  (var2x, var3x) -> var3x.forEach(
                     var2xx -> this.lang
                        .to(
                           "boosters.playerEnded",
                           var2xxx -> var2xxx.set("percent", var2xx.getPercent())
                              .set("type", this.lang.getString("boosters." + var2xx.getType().name().toLowerCase()))
                              .set("time", this.getFormattedExpirationDate(var2xx))
                              .set("affects", this.formatBoosterAffects(var2xx)),
                           var3
                        )
                  )
               );
         }

         this.playerBoosters.get(var2).clear();
      }

      this.storage.save("boosters-data", this);
   }

   public double calculateFinalBooster(UUID var1, BoostersController.BoosterType var2, IBoostable var3) {
      double[] var4 = new double[]{0.0};
      switch (this.strategy) {
         case SUM:
            this.forEachBooster(var1, var2, var3, var1x -> var4[0] += var1x.getPercent());
            break;
         case SUM_DIFFERENT:
            double[] var5 = new double[]{0.0};
            double[] var6 = new double[]{0.0};
            this.forEachBooster(var1, var2, var3, var2x -> {
               if (var2x.getTarget() == BoostersController.BoosterTarget.SERVER) {
                  var5[0] = Math.max(var5[0], var2x.getPercent());
               } else {
                  var6[0] = Math.max(var6[0], var2x.getPercent());
               }
            });
            var4[0] = var6[0] + var5[0];
            break;
         case HIGHEST:
            this.forEachBooster(var1, var2, var3, var1x -> var4[0] = Math.max(var4[0], var1x.getPercent()));
      }

      return var4[0];
   }

   private void createEndRunnable(final BoostersController.BoosterTarget var1, final BoostersController.Booster var2, final UUID var3) {
      long var4 = var2.getExpires() - System.currentTimeMillis();
      BukkitTask var6 = (new BukkitRunnable() {
            public void run() {
               if (var1 == BoostersController.BoosterTarget.SERVER) {
                  BoostersController.this.serverBoosters.putIfAbsent(var2.getType(), new ArrayList<>());
                  BoostersController.this.serverBoosters.get(var2.getType()).remove(var2);
                  BoostersController.this.lang
                     .toAll(
                        "boosters.serverEnded",
                        var2xx -> var2xx.set("percent", var2.getPercent())
                           .set("type", BoostersController.this.lang.getString("boosters." + var2.getType().name().toLowerCase()))
                           .set("affects", BoostersController.this.formatBoosterAffects(var2))
                     );
               } else {
                  BoostersController.this.playerBoosters.get(var3).putIfAbsent(var2.getType(), new ArrayList<>());
                  BoostersController.this.playerBoosters.get(var3).get(var2.getType()).remove(var2);
                  Player var1x = Bukkit.getPlayer(var3);
                  if (var1x != null) {
                     BoostersController.this.lang
                        .to(
                           "boosters.playerEnded",
                           var2xx -> var2xx.set("percent", var2.getPercent())
                              .set("type", BoostersController.this.lang.getString("boosters." + var2.getType().name().toLowerCase()))
                              .set("time", BoostersController.this.getFormattedExpirationDate(var2))
                              .set("affects", BoostersController.this.formatBoosterAffects(var2)),
                           var1x
                        );
                  }
               }
            }
         })
         .runTaskLater(this.plugin, var4 / 50L);
      this.runnables.add(var6);
   }

   public String formatBoosterAffects(BoostersController.Booster var1) {
      ArrayList var2 = new ArrayList();
      String[] var3 = var1.getAffects().split(",");

      for (String var7 : var3) {
         switch (var7) {
            case "all":
            case "free":
            case "premium":
               var2.add(this.lang.getString("boosters.affects." + var7));
               break;
            default:
               String[] var10 = var7.split("-");
               Job var11 = null;
               if (var10.length == 2) {
                  var11 = this.jobCache.getJob(var10[1]);
               }

               if (var11 != null) {
                  var2.add(var11.getName());
               }

               if (var1.getType() == BoostersController.BoosterType.REWARDS && var10.length == 2 && var10[0].equalsIgnoreCase("reward")) {
                  this.rewardCache.get(var10[1]).ifPresent(var1x -> var2.add(var1x.getName(1, null, "", 0.0)));
               }
         }
      }

      return String.join(this.lang.getString("boosters.affects.join"), var2);
   }

   private void forEachBooster(UUID var1, BoostersController.BoosterType var2, IBoostable var3, Consumer<BoostersController.Booster> var4) {
      this.serverBoosters.getOrDefault(var2, new ArrayList<>()).stream().filter(var2x -> this.checkIfAffects(var2x, var3)).forEach(var4);
      this.playerBoosters
         .getOrDefault(var1, new HashMap<>())
         .getOrDefault(var2, new ArrayList<>())
         .stream()
         .filter(var2x -> this.checkIfAffects(var2x, var3))
         .forEach(var4);
      Player var5 = Bukkit.getPlayer(var1);
      if (var5 != null) {
         this.permissionBoosters.getOrDefault(var2, new HashMap<>()).forEach((var4x, var5x) -> {
            if (var5.hasPermission(var4x)) {
               if (this.checkIfAffects(var5x, var3)) {
                  var4.accept(var5x);
               }
            }
         });
      }
   }

   public String getFormattedExpirationDate(BoostersController.Booster var1) {
      return Simple.time()
         .format(
            this.resetHandler.getTimeFormat(),
            TimeUnit.SECONDS,
            ChronoUnit.SECONDS
               .between(
                  ZonedDateTime.now().withZoneSameInstant(this.resetHandler.getTimeZone()),
                  ZonedDateTime.ofInstant(Instant.ofEpochMilli(var1.getExpires()), this.resetHandler.getTimeZone())
               )
         );
   }

   private boolean checkIfAffects(BoostersController.Booster var1, IBoostable var2) {
      if (var1.getExpires() < System.currentTimeMillis()) {
         return false;
      } else {
         String var3 = var1.getAffects().replaceAll(" ", "");
         if (!var3.isEmpty() && !var3.equalsIgnoreCase("all")) {
            List var4 = Arrays.asList(var3.split(","));
            if (var2 instanceof Job || var2 instanceof JobReward) {
               Job var5 = var2 instanceof Job ? (Job)var2 : ((JobReward)var2).getJob();
               if (var5.isPremiumJob() && var4.contains("premium")) {
                  return true;
               }

               if (!var5.isPremiumJob() && var4.contains("free")) {
                  return true;
               }

               if (var4.contains("job-" + var5.getId())) {
                  return true;
               }
            }

            return var2 instanceof JobReward var6 ? var4.contains("reward-" + var6.getReward().getId()) : false;
         } else {
            return true;
         }
      }
   }

   public Map<BoostersController.BoosterType, List<BoostersController.Booster>> getServerBoosters() {
      return this.serverBoosters;
   }

   public Map<UUID, Map<BoostersController.BoosterType, List<BoostersController.Booster>>> getPlayerBoosters() {
      return this.playerBoosters;
   }

   public Map<BoostersController.BoosterType, Map<String, BoostersController.Booster>> getPermissionBoosters() {
      return this.permissionBoosters;
   }

   public static class Booster {
      private final String from;
      private final double percent;
      private final String affects;
      private final long expires;
      private final BoostersController.BoosterType type;
      private final BoostersController.BoosterTarget target;

      public String getFrom() {
         return this.from;
      }

      public double getPercent() {
         return this.percent;
      }

      public String getAffects() {
         return this.affects;
      }

      public long getExpires() {
         return this.expires;
      }

      public BoostersController.BoosterType getType() {
         return this.type;
      }

      public BoostersController.BoosterTarget getTarget() {
         return this.target;
      }

      public Booster(String var1, double var2, String var4, long var5, BoostersController.BoosterType var7, BoostersController.BoosterTarget var8) {
         this.from = var1;
         this.percent = var2;
         this.affects = var4;
         this.expires = var5;
         this.type = var7;
         this.target = var8;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof BoostersController.Booster var2)) {
            return false;
         } else if (!var2.canEqual(this)) {
            return false;
         } else if (Double.compare(this.getPercent(), var2.getPercent()) != 0) {
            return false;
         } else if (this.getExpires() != var2.getExpires()) {
            return false;
         } else {
            String var3 = this.getFrom();
            String var4 = var2.getFrom();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getAffects();
               String var6 = var2.getAffects();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  BoostersController.BoosterType var7 = this.getType();
                  BoostersController.BoosterType var8 = var2.getType();
                  if (var7 == null ? var8 == null : var7.equals(var8)) {
                     BoostersController.BoosterTarget var9 = this.getTarget();
                     BoostersController.BoosterTarget var10 = var2.getTarget();
                     return var9 == null ? var10 == null : var9.equals(var10);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof BoostersController.Booster;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         long var3 = Double.doubleToLongBits(this.getPercent());
         var2 = var2 * 59 + (int)(var3 >>> 32 ^ var3);
         long var5 = this.getExpires();
         var2 = var2 * 59 + (int)(var5 >>> 32 ^ var5);
         String var7 = this.getFrom();
         var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
         String var8 = this.getAffects();
         var2 = var2 * 59 + (var8 == null ? 43 : var8.hashCode());
         BoostersController.BoosterType var9 = this.getType();
         var2 = var2 * 59 + (var9 == null ? 43 : var9.hashCode());
         BoostersController.BoosterTarget var10 = this.getTarget();
         return var2 * 59 + (var10 == null ? 43 : var10.hashCode());
      }
   }

   public static enum BoosterTarget {
      PLAYER,
      SERVER;
   }

   public static enum BoosterType {
      PROGRESS,
      POINTS,
      REWARDS;
   }

   public static enum BoostersStrategy {
      SUM,
      SUM_DIFFERENT,
      HIGHEST;
   }
}
