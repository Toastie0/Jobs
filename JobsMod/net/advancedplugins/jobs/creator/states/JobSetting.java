package net.advancedplugins.jobs.creator.states;

import java.util.Arrays;
import net.advancedplugins.jobs.creator.requests.ChatInputType;
import org.bukkit.Material;

public enum JobSetting {
   TYPE(
      "Job's type",
      0,
      "Controls when job activates",
      ChatInputType.STRING,
      "type",
      Material.matchMaterial("COMMAND_BLOCK"),
      "https://battlepass.advancedplugins.net/features/quests",
      "block-break"
   ),
   VARIABLE(
      "Job's variable",
      9,
      "Filter to specific req. job activates, set none to disable",
      ChatInputType.OBJECT,
      "variable",
      Material.matchMaterial("HOPPER"),
      "https://battlepass.advancedplugins.net/features/setting-up-and-using-variables",
      "stone"
   ),
   NAME("Job's name", 1, "The job's name", ChatInputType.STRING, "name", Material.matchMaterial("NAME_TAG"), null, "New Job"),
   PREMIUM(
      "Job Exclusivity",
      10,
      "Is job only for premium? (true/false)",
      ChatInputType.BOOL,
      "premium",
      Material.matchMaterial("EMERALD"),
      "https://battlepass.advancedplugins.net/tutorials/linking-rewards-to-tiers",
      "false"
   ),
   COOLDOWN("Progress cooldown", 2, "Cooldown between progress", ChatInputType.INT, "cooldown", Material.CLOCK, "", 0),
   REQUIRED_PROGRESS(
      "Required Progress",
      3,
      "Times job type activates to complete it",
      ChatInputType.STRING,
      "required-progress",
      Material.matchMaterial("REPEATER"),
      "https://battlepass.advancedplugins.net/features/quests",
      10
   ),
   POINTS(
      "Rewarded Points",
      12,
      "Points player is rewarded for completing job",
      ChatInputType.STRING,
      "points-rewarded",
      Material.matchMaterial("EXPERIENCE_BOTTLE"),
      "https://battlepass.advancedplugins.net/tutorials/linking-rewards-to-tiers",
      "%level%"
   ),
   REQ_POINTS(
      "Required Points",
      4,
      "Total points that player needs to have to join that job",
      ChatInputType.INT,
      "required-points",
      Material.matchMaterial("EXPERIENCE_BOTTLE"),
      "https://battlepass.advancedplugins.net/tutorials/linking-rewards-to-tiers",
      "%level%"
   ),
   NOTIFY(
      "Notify Percents",
      13,
      "Percents to notify at",
      ChatInputType.INT_LIST,
      "notify-at-percentages",
      Material.matchMaterial("PAPER"),
      null,
      Arrays.asList(10, 25, 50, 75, 100)
   ),
   BOTH(
      "Both Rewards",
      5,
      "Should player get both rewards (level + default)? (true/false)",
      ChatInputType.BOOL,
      "both-rewards",
      Material.matchMaterial("EMERALD"),
      "https://battlepass.advancedplugins.net/tutorials/linking-rewards-to-tiers",
      "false"
   ),
   DEF_REWARDS(
      "Default Rewards", 6, "Configure default rewards", ChatInputType.LIST, "default-rewards", Material.matchMaterial("CHEST"), null, Arrays.asList(2, 4)
   ),
   LEVEL_REWARDS(
      "Level Rewards", 15, "Configure rewards for specific level", ChatInputType.REW_LIST, "level-rewards", Material.matchMaterial("ENDER_CHEST"), null, null
   ),
   ITEM_MATERIAL(
      "Display item's material",
      7,
      "Configure displayed item's material",
      ChatInputType.MATERIAL,
      "item.material",
      Material.matchMaterial("PAPER"),
      "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html",
      "DIAMOND_PICKAXE"
   ),
   ITEM_NAME(
      "Display item's name",
      8,
      "Configure displayed item's name",
      ChatInputType.STRING,
      "item.name",
      Material.matchMaterial("PAPER"),
      null,
      "&eFreshly Created Job"
   ),
   ITEM_LORE(
      "Display item's lore",
      17,
      "Configure displayed item's lore",
      ChatInputType.LIST,
      "item.lore",
      Material.matchMaterial("PAPER"),
      null,
      Arrays.asList(
         "&fLevel: &f%level%",
         "&fPoints: &f%points%&7/&f%req_points%",
         "&fActive: %active%",
         "",
         "%action%",
         "",
         "&a%progress_bar% &7(&f%total_progress%&7/&f%required_progress%&7)"
      )
   );

   private final String name;
   private final String desc;
   private final String link;
   private final String path;
   private final Object def;
   private final ChatInputType type;
   private final Material material;
   private final int slot;

   private JobSetting(
      String nullxx, int nullxxx, String nullxxxx, ChatInputType nullxxxxx, String nullxxxxxx, Material nullxxxxxxx, String nullxxxxxxxx, Object nullxxxxxxxxx
   ) {
      this.name = nullxx;
      this.slot = nullxxx;
      this.path = nullxxxxxx;
      this.desc = nullxxxx;
      this.type = nullxxxxx;
      this.material = nullxxxxxxx;
      this.link = nullxxxxxxxx;
      this.def = nullxxxxxxxxx;
   }

   public String getName() {
      return this.name;
   }

   public String getDesc() {
      return this.desc;
   }

   public String getLink() {
      return this.link;
   }

   public String getPath() {
      return this.path;
   }

   public Object getDef() {
      return this.def;
   }

   public ChatInputType getType() {
      return this.type;
   }

   public Material getMaterial() {
      return this.material;
   }

   public int getSlot() {
      return this.slot;
   }
}
