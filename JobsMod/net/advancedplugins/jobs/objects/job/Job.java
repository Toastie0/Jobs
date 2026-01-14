package net.advancedplugins.jobs.objects.job;

import com.google.common.collect.Sets;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.impl.actions.objects.variable.Variable;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.objects.IBoostable;
import net.advancedplugins.simplespigot.config.Config;
import net.advancedplugins.simplespigot.item.SpigotItem;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

public class Job implements IBoostable {
   private String id;
   private String name;
   private String progressMenuTitle;
   private Map<String, Job.JobTypeInfo> types;
   private long cooldown;
   private String requiredPermission;
   private String requiredProgress;
   private String pointsRewarded;
   private Map<Integer, Double> specialRequiredProgress;
   private List<String> progressCommands;
   private Map<String, String> progressCommandsVariables;
   private Set<String> progressRewards;
   private String overrideMessage;
   private double requiredPoints;
   private ItemStack guiItem;
   private Set<String> defaultRewards;
   private Map<Integer, Set<String>> levelRewards;
   private boolean bothRewards;
   private boolean premiumJob;
   private Set<Integer> notifyAt;
   private final Set<String> whitelistedWorlds;
   private final Set<String> blacklistedWorlds;
   private final Set<String> blacklistedRegions;
   private final Set<String> whitelistedRegions;
   private Config config;

   public Job(String var1, Config var2) {
      this(
         var1,
         var2.string("name"),
         var2.string("progress-menu-title", ""),
         new HashMap<>(),
         var2.integer("cooldown") * 1000L,
         var2.string("permission"),
         String.valueOf(var2.get("required-progress")),
         var2.string("points-rewarded"),
         new HashMap<>(),
         var2.list("progress-actions.commands"),
         new HashMap<>(),
         Sets.newHashSet(var2.list("progress-actions.rewards")).stream().map(String::valueOf).collect(Collectors.toSet()),
         var2.string("progress-actions.override-message"),
         var2.doubl("required-points"),
         SpigotItem.toItem(var2, "item", null),
         Sets.newHashSet(var2.list("default-rewards")).stream().map(String::valueOf).collect(Collectors.toSet()),
         new HashMap<>(),
         var2.bool("both-rewards"),
         var2.bool("premium"),
         new HashSet<>(),
         Sets.newHashSet(var2.stringList("whitelisted-worlds")),
         Sets.newHashSet(var2.stringList("blacklisted-worlds")),
         Sets.newHashSet(var2.stringList("blacklisted-regions")),
         Sets.newHashSet(var2.stringList("whitelisted-regions")),
         var2
      );
      if (var2.has("progress-actions.variables")) {
         var2.getConfiguration()
            .getConfigurationSection("progress-actions.variables")
            .getValues(false)
            .forEach((var1x, var2x) -> this.progressCommandsVariables.put(var1x, var2x));
      }

      if (var2.keys("level-rewards", false) != null && !var2.keys("level-rewards", false).isEmpty()) {
         var2.keys("level-rewards", false)
            .forEach(
               var2x -> this.levelRewards
                  .put(Integer.valueOf(var2x), Sets.newHashSet(var2.list("level-rewards." + var2x)).stream().map(String::valueOf).collect(Collectors.toSet()))
            );
      }

      if (var2.keys("special-required-progress", false) != null && !var2.keys("special-required-progress", false).isEmpty()) {
         var2.keys("special-required-progress", false)
            .forEach(var2x -> this.specialRequiredProgress.put(Integer.valueOf(var2x), var2.doubl("special-required-progress." + var2x)));
      }

      this.notifyAt = Sets.newHashSet(var2.list("notify-at-percentages"));
      if (var2.has("type")) {
         String var3 = var2.string("type").toLowerCase();
         this.types.put(var3, new Job.JobTypeInfo(var3, Variable.of(var2.getConfiguration(), ""), 1.0, this.getSpecialProgressFrom("")));
      }

      if (var2.has("types")) {
         var2.getConfiguration()
            .getConfigurationSection("types")
            .getValues(false)
            .forEach(
               (var2x, var3x) -> {
                  MemorySection var4 = (MemorySection)var3x;
                  String var5 = var4.getString("type").toLowerCase();
                  this.types
                     .put(
                        var5,
                        new Job.JobTypeInfo(
                           var5,
                           Variable.of(var2.getConfiguration(), var4.getCurrentPath() + "."),
                           var4.getDouble("multiplier", 1.0),
                           this.getSpecialProgressFrom(var4.getCurrentPath() + ".")
                        )
                     );
               }
            );
      }
   }

   private Map<String, Double> getSpecialProgressFrom(String var1) {
      HashMap var2 = new HashMap();
      this.config.stringList(var1.concat("special-progress")).forEach(var1x -> {
         String[] var2x = var1x.split(" ", 2);
         if (var2x.length >= 2) {
            String var3 = var2x[0].toUpperCase();
            double var4 = 0.0;

            try {
               var4 = Double.parseDouble(var2x[1]);
            } catch (Exception var7) {
               return;
            }

            var2.put(var3, var4);
         }
      });
      return var2;
   }

   public BigDecimal getRequiredProgress() {
      return this.getRequiredProgress(1);
   }

   public BigDecimal getRequiredProgress(int var1) {
      return this.specialRequiredProgress.containsKey(var1)
         ? BigDecimal.valueOf(this.specialRequiredProgress.get(var1))
         : new BigDecimal(String.valueOf(ASManager.parseThroughCalculator(this.requiredProgress.replaceAll("%level%", String.valueOf(var1)))));
   }

   public BigDecimal getRewardedPoints() {
      return this.getRewardedPoints(1);
   }

   public BigDecimal getRewardedPoints(int var1) {
      return new BigDecimal(String.valueOf(ASManager.parseThroughCalculator(this.pointsRewarded.replaceAll("%level%", String.valueOf(var1)))));
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Job var2 = (Job)var1;
         return Objects.equals(this.id, var2.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.id);
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getProgressMenuTitle() {
      return this.progressMenuTitle;
   }

   public Map<String, Job.JobTypeInfo> getTypes() {
      return this.types;
   }

   public long getCooldown() {
      return this.cooldown;
   }

   public String getRequiredPermission() {
      return this.requiredPermission;
   }

   public String getPointsRewarded() {
      return this.pointsRewarded;
   }

   public Map<Integer, Double> getSpecialRequiredProgress() {
      return this.specialRequiredProgress;
   }

   public List<String> getProgressCommands() {
      return this.progressCommands;
   }

   public Map<String, String> getProgressCommandsVariables() {
      return this.progressCommandsVariables;
   }

   public Set<String> getProgressRewards() {
      return this.progressRewards;
   }

   public String getOverrideMessage() {
      return this.overrideMessage;
   }

   public double getRequiredPoints() {
      return this.requiredPoints;
   }

   public ItemStack getGuiItem() {
      return this.guiItem;
   }

   public Set<String> getDefaultRewards() {
      return this.defaultRewards;
   }

   public Map<Integer, Set<String>> getLevelRewards() {
      return this.levelRewards;
   }

   public boolean isBothRewards() {
      return this.bothRewards;
   }

   public boolean isPremiumJob() {
      return this.premiumJob;
   }

   public Set<Integer> getNotifyAt() {
      return this.notifyAt;
   }

   public Set<String> getWhitelistedWorlds() {
      return this.whitelistedWorlds;
   }

   public Set<String> getBlacklistedWorlds() {
      return this.blacklistedWorlds;
   }

   public Set<String> getBlacklistedRegions() {
      return this.blacklistedRegions;
   }

   public Set<String> getWhitelistedRegions() {
      return this.whitelistedRegions;
   }

   public Config getConfig() {
      return this.config;
   }

   public void setId(String var1) {
      this.id = var1;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setProgressMenuTitle(String var1) {
      this.progressMenuTitle = var1;
   }

   public void setTypes(Map<String, Job.JobTypeInfo> var1) {
      this.types = var1;
   }

   public void setCooldown(long var1) {
      this.cooldown = var1;
   }

   public void setRequiredPermission(String var1) {
      this.requiredPermission = var1;
   }

   public void setRequiredProgress(String var1) {
      this.requiredProgress = var1;
   }

   public void setPointsRewarded(String var1) {
      this.pointsRewarded = var1;
   }

   public void setSpecialRequiredProgress(Map<Integer, Double> var1) {
      this.specialRequiredProgress = var1;
   }

   public void setProgressCommands(List<String> var1) {
      this.progressCommands = var1;
   }

   public void setProgressCommandsVariables(Map<String, String> var1) {
      this.progressCommandsVariables = var1;
   }

   public void setProgressRewards(Set<String> var1) {
      this.progressRewards = var1;
   }

   public void setOverrideMessage(String var1) {
      this.overrideMessage = var1;
   }

   public void setRequiredPoints(double var1) {
      this.requiredPoints = var1;
   }

   public void setGuiItem(ItemStack var1) {
      this.guiItem = var1;
   }

   public void setDefaultRewards(Set<String> var1) {
      this.defaultRewards = var1;
   }

   public void setLevelRewards(Map<Integer, Set<String>> var1) {
      this.levelRewards = var1;
   }

   public void setBothRewards(boolean var1) {
      this.bothRewards = var1;
   }

   public void setPremiumJob(boolean var1) {
      this.premiumJob = var1;
   }

   public void setNotifyAt(Set<Integer> var1) {
      this.notifyAt = var1;
   }

   public void setConfig(Config var1) {
      this.config = var1;
   }

   public Job(
      String var1,
      String var2,
      String var3,
      Map<String, Job.JobTypeInfo> var4,
      long var5,
      String var7,
      String var8,
      String var9,
      Map<Integer, Double> var10,
      List<String> var11,
      Map<String, String> var12,
      Set<String> var13,
      String var14,
      double var15,
      ItemStack var17,
      Set<String> var18,
      Map<Integer, Set<String>> var19,
      boolean var20,
      boolean var21,
      Set<Integer> var22,
      Set<String> var23,
      Set<String> var24,
      Set<String> var25,
      Set<String> var26,
      Config var27
   ) {
      this.id = var1;
      this.name = var2;
      this.progressMenuTitle = var3;
      this.types = var4;
      this.cooldown = var5;
      this.requiredPermission = var7;
      this.requiredProgress = var8;
      this.pointsRewarded = var9;
      this.specialRequiredProgress = var10;
      this.progressCommands = var11;
      this.progressCommandsVariables = var12;
      this.progressRewards = var13;
      this.overrideMessage = var14;
      this.requiredPoints = var15;
      this.guiItem = var17;
      this.defaultRewards = var18;
      this.levelRewards = var19;
      this.bothRewards = var20;
      this.premiumJob = var21;
      this.notifyAt = var22;
      this.whitelistedWorlds = var23;
      this.blacklistedWorlds = var24;
      this.blacklistedRegions = var25;
      this.whitelistedRegions = var26;
      this.config = var27;
   }

   public class JobTypeInfo {
      private final String type;
      private final Variable variable;
      private final double multiplier;
      private final Map<String, Double> specialProgress;

      public JobTypeInfo(final String nullx, final Variable nullxx, final double nullxxx, final Map<String, Double> nullxxxx) {
         this.type = nullx;
         this.variable = nullxx;
         this.multiplier = nullxxx;
         this.specialProgress = nullxxxx;
      }

      public String getType() {
         return this.type;
      }

      public Variable getVariable() {
         return this.variable;
      }

      public double getMultiplier() {
         return this.multiplier;
      }

      public Map<String, Double> getSpecialProgress() {
         return this.specialProgress;
      }
   }
}
