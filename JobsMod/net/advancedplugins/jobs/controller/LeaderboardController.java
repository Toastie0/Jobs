package net.advancedplugins.jobs.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.objects.users.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class LeaderboardController {
   private final Core core;
   private final Map<LeaderboardController.LeaderboardType, Map<String, List<LeaderboardController.LeaderboardObject>>> leaderboards = new HashMap<>();
   private long nextRefresh = 0L;

   public LeaderboardController(final Core var1) {
      this.core = var1;
      this.clean();
      if (var1.getConfig("config").bool("leaderboards.use-cache")) {
         final long var2 = (long)Math.floor(var1.getConfig("config").doubl("leaderboards.cache-refresh-minutes") * 60.0 * 20.0);
         if (var2 < 1L) {
            var2 = 12000L;
         }

         (new BukkitRunnable() {
               public void run() {
                  LeaderboardController.this.clean();
                  LeaderboardController.this.leaderboards
                     .get(LeaderboardController.LeaderboardType.POINTS)
                     .put("", LeaderboardController.this.getPointsLeaderboard());
                  var1.getJobCache()
                     .getAllJobs()
                     .forEach(
                        var1xx -> LeaderboardController.this.leaderboards
                           .get(LeaderboardController.LeaderboardType.LEVEL)
                           .put(var1xx.getId(), LeaderboardController.this.getJobLevelLeaderboard(var1xx))
                     );
                  LeaderboardController.this.nextRefresh = System.currentTimeMillis() + var2 * 50L;
               }
            })
            .runTaskTimerAsynchronously(var1, 0L, var2);
      }
   }

   private void clean() {
      for (LeaderboardController.LeaderboardType var4 : LeaderboardController.LeaderboardType.values()) {
         this.leaderboards.put(var4, new HashMap<>());
         switch (var4) {
            case POINTS:
               this.leaderboards.get(var4).put("", new ArrayList<>());
               break;
            case LEVEL:
               this.core.getJobCache().getAllJobs().forEach(var2 -> this.leaderboards.get(var4).put(var2.getId(), new ArrayList<>()));
         }
      }
   }

   public List<LeaderboardController.LeaderboardObject> getPointsLeaderboard() {
      return this.leaderboards.get(LeaderboardController.LeaderboardType.POINTS).get("").isEmpty()
         ? this.core
            .getUserCache()
            .getAll()
            .stream()
            .map(var1 -> new LeaderboardController.LeaderboardObject(this.getFixedName(var1.getOfflinePlayer()), var1.getUuid(), var1.getPoints()))
            .filter(var0 -> !var0.playerName.isEmpty())
            .sorted(Collections.reverseOrder(Comparator.comparingDouble(var0 -> var0.value)))
            .collect(Collectors.toList())
         : this.leaderboards.get(LeaderboardController.LeaderboardType.POINTS).get("");
   }

   public List<LeaderboardController.LeaderboardObject> getJobLevelLeaderboard(Job var1) {
      return this.leaderboards.get(LeaderboardController.LeaderboardType.LEVEL).get(var1.getId()).isEmpty()
         ? this.core
            .getUserCache()
            .getAll()
            .stream()
            .map(
               var2 -> new LeaderboardController.LeaderboardObject(
                  this.getFixedName(var2.getOfflinePlayer()), var2.getUuid(), this.core.getJobController().getFixedJobInfo(var2, var1).getLevel()
               )
            )
            .filter(var0 -> !var0.playerName.isEmpty())
            .sorted(Collections.reverseOrder(Comparator.comparingDouble(var0 -> var0.value)))
            .collect(Collectors.toList())
         : this.leaderboards.get(LeaderboardController.LeaderboardType.LEVEL).get(var1.getId());
   }

   public Optional<LeaderboardController.LeaderboardObject> getPointsLeaderboardAt(int var1) {
      List var2 = this.getPointsLeaderboard();
      if (var2.size() < var1) {
         return Optional.empty();
      } else {
         return var1 < 0 ? Optional.empty() : Optional.of((LeaderboardController.LeaderboardObject)var2.get(var1 - 1));
      }
   }

   public Optional<LeaderboardController.LeaderboardObject> getJobLevelLeaderboardAt(int var1, Job var2) {
      List var3 = this.getJobLevelLeaderboard(var2);
      if (var3.size() < var1) {
         return Optional.empty();
      } else {
         return var1 < 0 ? Optional.empty() : Optional.of((LeaderboardController.LeaderboardObject)var3.get(var1 - 1));
      }
   }

   public int getPlayerPlaceInPointsLeaderboard(UUID var1) {
      int var2 = -1;
      List var3 = this.getPointsLeaderboard();

      for (int var4 = 0; var4 < var3.size(); var4++) {
         if (((LeaderboardController.LeaderboardObject)var3.get(var4)).getUuid().equals(var1)) {
            var2 = var4 + 1;
            break;
         }
      }

      return var2;
   }

   public int getPlayerPlaceInJobLevelLeaderboard(UUID var1, Job var2) {
      int var3 = -1;
      List var4 = this.getJobLevelLeaderboard(var2);

      for (int var5 = 0; var5 < var4.size(); var5++) {
         if (((LeaderboardController.LeaderboardObject)var4.get(var5)).getUuid().equals(var1)) {
            var3 = var5 + 1;
            break;
         }
      }

      return var3;
   }

   public double getPlayerValueInPointsLeaderboard(UUID var1) {
      User var2 = this.core.getUserCache().getSync(var1).orElse(null);
      return var2 == null ? 0.0 : var2.getPoints();
   }

   public int getPlayerValueInJobLevelLeaderboard(UUID var1, Job var2) {
      User var3 = this.core.getUserCache().getSync(var1).orElse(null);
      return var3 == null ? 0 : this.core.getJobController().getFixedJobInfo(var3, var2).getLevel();
   }

   private String getFixedName(OfflinePlayer var1) {
      if (var1 == null) {
         return "";
      } else {
         String var2 = var1.getName();
         return var2 == null ? "" : var2;
      }
   }

   public long getNextRefresh() {
      return this.nextRefresh;
   }

   public static class LeaderboardObject {
      private final String playerName;
      private final UUID uuid;
      private final double value;

      public String getPlayerName() {
         return this.playerName;
      }

      public UUID getUuid() {
         return this.uuid;
      }

      public double getValue() {
         return this.value;
      }

      public LeaderboardObject(String var1, UUID var2, double var3) {
         this.playerName = var1;
         this.uuid = var2;
         this.value = var3;
      }
   }

   public static enum LeaderboardType {
      POINTS,
      LEVEL;
   }
}
