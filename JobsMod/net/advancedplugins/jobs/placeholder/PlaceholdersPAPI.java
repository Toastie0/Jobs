package net.advancedplugins.jobs.placeholder;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.controller.BoostersController;
import net.advancedplugins.jobs.controller.JobController;
import net.advancedplugins.jobs.controller.LeaderboardController;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.objects.users.User;
import net.advancedplugins.jobs.util.Services;
import net.advancedplugins.simplespigot.service.simple.Simple;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholdersPAPI extends PlaceholderExpansion {
   private final Core core;
   private final LeaderboardController.LeaderboardObject DEFAULT_LEADERBOARD = new LeaderboardController.LeaderboardObject("-", UUID.randomUUID(), 0.0);

   public PlaceholdersPAPI(Core var1) {
      this.core = var1;
   }

   @NotNull
   public String getIdentifier() {
      return "advancedjobs";
   }

   @NotNull
   public String getAuthor() {
      return "AdvancedPlugins";
   }

   @NotNull
   public String getVersion() {
      return "1.0.0";
   }

   public boolean persist() {
      return true;
   }

   public boolean canRegister() {
      return true;
   }

   @Nullable
   public String onRequest(@Nullable OfflinePlayer var1, @NotNull String var2) {
      if (var2 != null && !var2.isEmpty()) {
         LeaderboardController var3 = this.core.getLeaderboardController();
         if (var2.startsWith("top")) {
            if (var2.equalsIgnoreCase("top_time")) {
               long var20 = var3.getNextRefresh();
               if (var20 <= 0L) {
                  return "-";
               }

               return Simple.time()
                  .format(
                     this.core.getJobsResetHandler().getTimeFormat(),
                     TimeUnit.SECONDS,
                     ChronoUnit.SECONDS
                        .between(
                           ZonedDateTime.now().withZoneSameInstant(this.core.getJobsResetHandler().getTimeZone()),
                           ZonedDateTime.ofInstant(Instant.ofEpochMilli(var20), this.core.getJobsResetHandler().getTimeZone())
                        )
                  );
            }

            String[] var4 = var2.split("_");
            if (var4.length < 4) {
               return null;
            }

            int var5 = var4[3].equalsIgnoreCase("me") ? -1 : Integer.parseInt(var4[3]);
            String var6 = var4[1].equalsIgnoreCase("level") && var4.length > 4 ? var4[4] : "";
            Job var7 = this.core.getJobCache().getJob(var6);
            String var8 = var4[1];
            label232:
            switch (var8) {
               case "points":
                  String var33 = var4[2];
                  switch (var33) {
                     case "name":
                        if (var5 == -1) {
                           return null;
                        }

                        return var3.getPointsLeaderboardAt(var5).orElse(this.DEFAULT_LEADERBOARD).getPlayerName();
                     case "value":
                        if (var5 == -1 && var1 != null) {
                           return String.valueOf(var3.getPlayerValueInPointsLeaderboard(var1.getUniqueId()));
                        }

                        return String.valueOf(var3.getPointsLeaderboardAt(var5).orElse(this.DEFAULT_LEADERBOARD).getValue());
                     case "place":
                        if (var5 == -1 && var1 != null) {
                           return String.valueOf(var3.getPlayerPlaceInPointsLeaderboard(var1.getUniqueId()));
                        }

                        return null;
                     default:
                        break label232;
                  }
               case "level":
                  if (var7 == null) {
                     return null;
                  }

                  String var10 = var4[2];
                  switch (var10) {
                     case "name":
                        if (var5 == -1) {
                           return null;
                        }

                        return var3.getJobLevelLeaderboardAt(var5, var7).orElse(this.DEFAULT_LEADERBOARD).getPlayerName();
                     case "value":
                        if (var5 == -1 && var1 != null) {
                           return String.valueOf(var3.getPlayerValueInJobLevelLeaderboard(var1.getUniqueId(), var7));
                        }

                        return String.valueOf(var3.getJobLevelLeaderboardAt(var5, var7).orElse(this.DEFAULT_LEADERBOARD).getValue());
                     case "place":
                        if (var5 == -1 && var1 != null) {
                           return String.valueOf(var3.getPlayerPlaceInJobLevelLeaderboard(var1.getUniqueId(), var7));
                        }

                        return null;
                  }
            }
         }

         if (var2.startsWith("has_booster")) {
            String[] var19 = var2.split("_", 6);
            if (var19.length >= 6 && var1 != null) {
               String var22 = var19[2].toUpperCase();
               String var24 = var19[3].toUpperCase();
               String var27 = var19[4].toUpperCase();
               String var30 = var19[5].toUpperCase();

               for (BoostersController.BoosterTarget var39 : BoostersController.BoosterTarget.values()) {
                  if (var22.equalsIgnoreCase("ANY") || var22.equalsIgnoreCase(var39.name())) {
                     Map var40 = (Map)(var39 == BoostersController.BoosterTarget.SERVER
                        ? this.core.getBoostersController().getServerBoosters()
                        : this.core.getBoostersController().getPlayerBoosters().getOrDefault(var1.getUniqueId(), new HashMap<>()));

                     for (BoostersController.BoosterType var17 : BoostersController.BoosterType.values()) {
                        if ((var24.equalsIgnoreCase("ANY") || var24.equalsIgnoreCase(var17.name()))
                           && var40.getOrDefault(var17, new ArrayList<>())
                              .stream()
                              .anyMatch(
                                 var2x -> (var30.equalsIgnoreCase("ANY") || var2x.getAffects().equalsIgnoreCase(var30))
                                    && (var27.equalsIgnoreCase("ANY") || var2x.getPercent() == Double.parseDouble(var27))
                              )) {
                           return "true";
                        }
                     }
                  }
               }

               return "false";
            } else {
               return null;
            }
         } else if (var1 != null && var1.isOnline()) {
            Player var18 = var1.getPlayer();
            User var21 = this.core.getUserCache().getSync(var18.getUniqueId()).orElse(null);
            JobController var23 = this.core.getJobController();
            if (var21 == null) {
               return null;
            } else if (var2.equalsIgnoreCase("active")) {
               return this.getActiveJobs(var21, this.core.getConfig("config").string("active-jobs-placeholder.default-delimiter", ", "));
            } else if (var2.startsWith("active")) {
               String[] var26 = var2.split("_", 2);
               String var29 = null;
               if (var26.length == 2) {
                  var29 = this.core.getConfig("config").string("active-jobs-placeholder.custom-delimiters." + var26[1]);
               }

               if (var29 == null) {
                  var29 = this.core.getConfig("config").string("active-jobs-placeholder.default-delimiter", ", ");
               }

               return this.getActiveJobs(var21, var29);
            } else if (var2.equalsIgnoreCase("limit")) {
               return String.valueOf(var23.getMaxJobs(var18));
            } else if (var2.equalsIgnoreCase("in_jobs")) {
               return String.valueOf(var23.getActiveJobs(var21).size());
            } else if (var2.equalsIgnoreCase("current_points")) {
               return String.valueOf(var21.getPoints());
            } else if (var2.equalsIgnoreCase("daily_refresh_time")) {
               return this.core.getJobsResetHandler().asString();
            } else {
               String[] var25 = var2.split("_", 2);
               String var28 = var25[0];
               String var31 = var25.length == 2 ? var25[1] : "";
               Job var34 = this.core.getJobCache().getJob(var28);
               String var37 = this.core.getLocale().getString("job-placeholder-null", "");
               if (var34 == null) {
                  if (!NumberUtils.isNumber(var28)) {
                     return null;
                  }

                  int var12 = Integer.parseInt(var28);
                  if (var12 >= var23.getActiveJobs(var21).size()) {
                     return var37;
                  }

                  var34 = this.core.getJobCache().getJob((String)var23.getActiveJobs(var21).keySet().toArray()[var12]);
                  if (var34 == null) {
                     return var37;
                  }
               }

               switch (var31) {
                  case "":
                  case "level":
                     return String.valueOf(var23.getFixedJobInfo(var21, var34).getLevel());
                  case "progress":
                     return String.valueOf(var23.getJobProgress(var21, var34).doubleValue());
                  case "required_progress":
                     return String.valueOf(var23.getRequiredProgress(var21, var34).doubleValue());
                  case "name":
                     return var34.getName();
                  case "active":
                     return String.valueOf(var23.getActiveJobs(var21).containsKey(var28));
                  case "percentage_progress":
                     return Services.getPercentageString(var23.getJobProgress(var21, var34), var23.getRequiredProgress(var21, var34)).concat("%");
                  case "required_points":
                     return String.valueOf(var34.getRequiredPoints());
                  case "required_points_left":
                     return String.valueOf(Math.max(var34.getRequiredPoints() - var21.getPoints(), 0.0));
                  default:
                     return null;
               }
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private String getActiveJobs(User var1, String var2) {
      Map var3 = this.core.getJobController().getActiveJobs(var1);
      String var4 = var3.values().stream().map(var1x -> this.core.getJobCache().getJob(var1x.getJob())).map(Job::getName).collect(Collectors.joining(var2));
      if (var4.isEmpty()) {
         var4 = "-";
      }

      return var4;
   }
}
