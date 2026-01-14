package net.advancedplugins.jobs.impl.actions;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.plotsquared.core.api.PlotAPI;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import net.advancedplugins.jobs.impl.actions.abuse.BlockPlaceAntiAbuse;
import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import net.advancedplugins.jobs.impl.actions.containers.ExternalActionContainer;
import net.advancedplugins.jobs.impl.actions.external.ASkyblockQuests;
import net.advancedplugins.jobs.impl.actions.external.AdvancedChatQuests;
import net.advancedplugins.jobs.impl.actions.external.AdvancedEnchantmentsQuests;
import net.advancedplugins.jobs.impl.actions.external.AdvancedMobsQuests;
import net.advancedplugins.jobs.impl.actions.external.AuctionHouseKludgeQuests;
import net.advancedplugins.jobs.impl.actions.external.AutoSellQuests;
import net.advancedplugins.jobs.impl.actions.external.BedWars1058Quests;
import net.advancedplugins.jobs.impl.actions.external.BenzimmerKothQuests;
import net.advancedplugins.jobs.impl.actions.external.BuildBattleTigerQuests;
import net.advancedplugins.jobs.impl.actions.external.ChatReactionQuests;
import net.advancedplugins.jobs.impl.actions.external.CitizensQuests;
import net.advancedplugins.jobs.impl.actions.external.ClansQuests;
import net.advancedplugins.jobs.impl.actions.external.ClueScrollsQuests;
import net.advancedplugins.jobs.impl.actions.external.CrateReloadedQuests;
import net.advancedplugins.jobs.impl.actions.external.CratesPlusQuests;
import net.advancedplugins.jobs.impl.actions.external.CrazyCratesQuests;
import net.advancedplugins.jobs.impl.actions.external.CrazyCratesQuestsNew;
import net.advancedplugins.jobs.impl.actions.external.CrazyEnvoyQuests;
import net.advancedplugins.jobs.impl.actions.external.DiscordMinecraftQuests;
import net.advancedplugins.jobs.impl.actions.external.EconomyShopGUIQuests;
import net.advancedplugins.jobs.impl.actions.external.ExcellentCratesQuest;
import net.advancedplugins.jobs.impl.actions.external.JobsQuests;
import net.advancedplugins.jobs.impl.actions.external.LandsQuests;
import net.advancedplugins.jobs.impl.actions.external.LobbyPresentsPoompkQuests;
import net.advancedplugins.jobs.impl.actions.external.MBedwarsQuests;
import net.advancedplugins.jobs.impl.actions.external.MMOCoreQuests;
import net.advancedplugins.jobs.impl.actions.external.MMOItemsQuests;
import net.advancedplugins.jobs.impl.actions.external.MoneyHuntersProQuests;
import net.advancedplugins.jobs.impl.actions.external.MoneyHuntersQuests;
import net.advancedplugins.jobs.impl.actions.external.MythicMobsQuests;
import net.advancedplugins.jobs.impl.actions.external.PlaceholderApiQuests;
import net.advancedplugins.jobs.impl.actions.external.PlotSquaredQuests;
import net.advancedplugins.jobs.impl.actions.external.ProCosmeticsQuests;
import net.advancedplugins.jobs.impl.actions.external.RivalHoeQuests;
import net.advancedplugins.jobs.impl.actions.external.ShopGuiPlusQuests;
import net.advancedplugins.jobs.impl.actions.external.ShopkeepersQuests;
import net.advancedplugins.jobs.impl.actions.external.SkillApiQuests;
import net.advancedplugins.jobs.impl.actions.external.StrikePracticeQuests;
import net.advancedplugins.jobs.impl.actions.external.SubsideKothQuests;
import net.advancedplugins.jobs.impl.actions.external.SuperiorSkyblockQuests;
import net.advancedplugins.jobs.impl.actions.external.TheLabQuests;
import net.advancedplugins.jobs.impl.actions.external.TokenEnchantQuests;
import net.advancedplugins.jobs.impl.actions.external.USkyBlockQuests;
import net.advancedplugins.jobs.impl.actions.external.UltraSkyWarsQuests;
import net.advancedplugins.jobs.impl.actions.external.VotifierQuests;
import net.advancedplugins.jobs.impl.actions.external.chestshop.ChestShopQuests;
import net.advancedplugins.jobs.impl.actions.external.mcmmo.McMMOQuests;
import net.advancedplugins.jobs.impl.actions.internal.BlockBreakAction;
import net.advancedplugins.jobs.impl.actions.internal.BlockPlaceQuest;
import net.advancedplugins.jobs.impl.actions.internal.BrewingQuest;
import net.advancedplugins.jobs.impl.actions.internal.BucketPlaceQuest;
import net.advancedplugins.jobs.impl.actions.internal.ChatQuest;
import net.advancedplugins.jobs.impl.actions.internal.ClickQuest;
import net.advancedplugins.jobs.impl.actions.internal.ConsumeQuest;
import net.advancedplugins.jobs.impl.actions.internal.CraftQuest;
import net.advancedplugins.jobs.impl.actions.internal.DamageQuest;
import net.advancedplugins.jobs.impl.actions.internal.DeathAction;
import net.advancedplugins.jobs.impl.actions.internal.EnchantQuests;
import net.advancedplugins.jobs.impl.actions.internal.EntityBreedQuest;
import net.advancedplugins.jobs.impl.actions.internal.ExecuteCommandQuest;
import net.advancedplugins.jobs.impl.actions.internal.FishingQuest;
import net.advancedplugins.jobs.impl.actions.internal.GainExpQuest;
import net.advancedplugins.jobs.impl.actions.internal.HarvestCropsQuest;
import net.advancedplugins.jobs.impl.actions.internal.HoneyExtractQuests;
import net.advancedplugins.jobs.impl.actions.internal.ItemBreakQuest;
import net.advancedplugins.jobs.impl.actions.internal.KillMobQuest;
import net.advancedplugins.jobs.impl.actions.internal.KillPlayerQuest;
import net.advancedplugins.jobs.impl.actions.internal.LoginQuest;
import net.advancedplugins.jobs.impl.actions.internal.MilkQuest;
import net.advancedplugins.jobs.impl.actions.internal.MovementQuests;
import net.advancedplugins.jobs.impl.actions.internal.PlantCropsQuest;
import net.advancedplugins.jobs.impl.actions.internal.PlayTimeQuest;
import net.advancedplugins.jobs.impl.actions.internal.ProjectileQuest;
import net.advancedplugins.jobs.impl.actions.internal.RegenerateQuest;
import net.advancedplugins.jobs.impl.actions.internal.RideMobQuest;
import net.advancedplugins.jobs.impl.actions.internal.ShearSheepQuest;
import net.advancedplugins.jobs.impl.actions.internal.SmeltQuest;
import net.advancedplugins.jobs.impl.actions.internal.TameQuest;
import net.advancedplugins.jobs.impl.actions.utils.Instantiator;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import net.advancedplugins.jobs.impl.utils.PluginVersion;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import net.advancedplugins.jobs.impl.utils.tuple.ImmutablePair;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ActionRegistry {
   public static final String NONE_ROOT = UUID.randomUUID().toString();
   private static ActionRegistry registry;
   private static boolean ignoreBlockCache = false;
   private static BlockPlaceAntiAbuse blockPlaceAntiAbuse;
   private final JavaPlugin plugin;
   private final PluginManager manager;
   private final Set<String> registeredHooks = Sets.newHashSet();
   private final Map<String, ImmutablePair<AtomicInteger, FoliaScheduler.Task>> attempts = Maps.newHashMap();
   private final ActionsReader reader;
   private List<String> disabledHooks = new LinkedList<>();
   private boolean enablePlaytime = true;
   private boolean brewingProtection = true;
   private boolean breakProtection = true;

   public ActionRegistry(JavaPlugin var1, ActionsReader var2, boolean var3, Set<String> var4) {
      this.plugin = var1;
      this.manager = Bukkit.getPluginManager();
      this.reader = var2;
      ignoreBlockCache = var3;
      blockPlaceAntiAbuse = new BlockPlaceAntiAbuse(var1);
      registry = this;
      this.placeholderAPI(var4);
   }

   public void unload() {
   }

   public void register() {
      this.quest(
         BlockBreakAction::new,
         BlockPlaceQuest::new,
         ChatQuest::new,
         ClickQuest::new,
         ConsumeQuest::new,
         BucketPlaceQuest::new,
         CraftQuest::new,
         DamageQuest::new,
         DeathAction::new,
         EnchantQuests::new,
         ExecuteCommandQuest::new,
         FishingQuest::new,
         GainExpQuest::new,
         HoneyExtractQuests::new,
         ItemBreakQuest::new,
         KillMobQuest::new,
         KillPlayerQuest::new,
         LoginQuest::new,
         MilkQuest::new,
         MovementQuests::new,
         ProjectileQuest::new,
         RegenerateQuest::new,
         RideMobQuest::new,
         ShearSheepQuest::new,
         SmeltQuest::new,
         TameQuest::new
      );
      if (MinecraftVersion.isNew()) {
         this.quest(EntityBreedQuest::new, HarvestCropsQuest::new, PlantCropsQuest::new, BrewingQuest::new);
      }

      if (this.enablePlaytime) {
         new PlayTimeQuest(this.plugin);
      }

      FoliaScheduler.runTaskLater(this.plugin, this::registerExternalHooks, 10L);
   }

   private void registerExternalHooks() {
      this.hook("AdvancedChat", AdvancedChatQuests::new);
      this.hook("AdvancedEnchantments", AdvancedEnchantmentsQuests::new);
      this.hook("McMMO", McMMOQuests::new, var0 -> var0.getMajor() >= 2);
      this.hook("ASkyblock", ASkyblockQuests::new);
      this.hook("AuctionHouse", AuctionHouseKludgeQuests::new, "klugemonkey");
      this.hook("AutoSell", AutoSellQuests::new, "extended_clip");
      this.hook("BedWars1058", BedWars1058Quests::new);
      this.hook("KOTH", BenzimmerKothQuests::new, "benzimmer123");
      this.hook("BuildBattle", BuildBattleTigerQuests::new, "Tigerpanzer", var0 -> var0.getMajor() < 5);
      this.hook("ChatReaction", ChatReactionQuests::new);
      this.hook(
         "ChestShop",
         ChestShopQuests::new,
         "https://github.com/ChestShop-authors/ChestShop-3/contributors",
         var0 -> var0.getMajor() >= 3 && var0.getMinor() > 9
      );
      this.hook("Citizens", CitizensQuests::new);
      this.hook("Clans", ClansQuests::new);
      this.hook("ClueScrolls", ClueScrollsQuests::new);
      this.hook("CrateReloaded", CrateReloadedQuests::new);
      this.hook("CratesPlus", CratesPlusQuests::new);
      this.hook(
         "CrazyCrates",
         CrazyCratesQuests::new,
         var0 -> var0.getMajor() == 1
               && (var0.getMinor() < 21 || var0.getMinor() == 21 && var0.getBugfix() == 0 || var0.getMinor() == 22 && var0.getBugfix() == 0)
            || var0.getMajor() > 1 && (var0.getMajor() < 4 || var0.getMajor() == 4 && var0.getMinor() < 5)
      );
      this.hook(
         "CrazyCrates",
         CrazyCratesQuestsNew::new,
         var0 -> var0.getMajor() == 4 && var0.getMinor() >= 5
            || var0.getMajor() > 4
            || var0.getMajor() == 1
               && (var0.getMinor() > 22 || var0.getMinor() == 22 && var0.getBugfix() != 0 || var0.getMinor() == 21 && var0.getBugfix() >= 5)
      );
      this.hook("DiscordMinecraft", DiscordMinecraftQuests::new);
      this.hook("ExcellentCrates", ExcellentCratesQuest::new, "NightExpress");
      this.hook("Jobs", JobsQuests::new);
      this.hook("Lands", LandsQuests::new);
      this.hook("LobbyPresents", LobbyPresentsPoompkQuests::new, "poompk");
      this.hook("KoTH", SubsideKothQuests::new, "SubSide");
      this.hook("AdvancedMobs", AdvancedMobsQuests::new);
      this.hook("RivalHarvesterHoes", RivalHoeQuests::new);
      this.hook("EconomyShopGUI", EconomyShopGUIQuests::new);
      this.hook("EconomyShopGUI-Premium", EconomyShopGUIQuests::new);
      if (Bukkit.getPluginManager().isPluginEnabled("MoneyHunters")) {
         if (Bukkit.getPluginManager().getPlugin("MoneyHunters").getDescription().getVersion().endsWith("Pro")) {
            this.hook("MoneyHunters", MoneyHuntersProQuests::new);
         } else {
            this.hook("MoneyHunters", MoneyHuntersQuests::new);
         }
      }

      this.hook("MythicMobs", MythicMobsQuests::new, var0 -> var0.getMajor() >= 5);
      this.hook("PlotSquared", PlotSquaredQuests::new);
      this.hook("ProCosmetics", ProCosmeticsQuests::new);
      this.hook("ShopGuiPlus", ShopGuiPlusQuests::new);
      this.hook("CrazyEnvoy", null, var0 -> false);
      this.hook("CrazyEnvoys", CrazyEnvoyQuests::new, var0 -> var0.getMajor() >= 1 && var0.getMinor() >= 5);
      this.hook("Shopkeepers", ShopkeepersQuests::new, "nisovin");
      this.hook("SkillAPI", SkillApiQuests::new);
      this.hook("StrikePractice", StrikePracticeQuests::new);
      this.hook("SuperiorSkyblock2", SuperiorSkyblockQuests::new);
      this.hook("TheLab", TheLabQuests::new);
      this.hook("TokenEnchant", TokenEnchantQuests::new);
      this.hook("UltraSkyWars", UltraSkyWarsQuests::new, "Leonardo0013YT");
      this.hook("uSkyBlock", USkyBlockQuests::new);
      this.hook("Votifier", VotifierQuests::new);
      this.hook("VotifierPlus", VotifierQuests::new);
      this.hook("MMOCore", MMOCoreQuests::new);
      this.hook("MMOItems", MMOItemsQuests::new);
      this.hook("MBedwars", MBedwarsQuests::new);
   }

   public void disableHook(String var1) {
      this.disabledHooks.add(var1.toLowerCase());
   }

   @SafeVarargs
   public final void quest(Instantiator<ActionContainer>... var1) {
      for (Instantiator var5 : var1) {
         Bukkit.getPluginManager().registerEvents(var5.init(this.plugin), this.plugin);
      }
   }

   public boolean hook(String var1, Instantiator<ExternalActionContainer> var2, String var3) {
      if (this.isHookDisabled(var1)) {
         return false;
      } else {
         Plugin var4 = this.manager.getPlugin(var1);
         if (var4 != null && var4.isEnabled()) {
            if (!this.isHookDisabled(var1) && (var3.isEmpty() || var4.getDescription().getAuthors().contains(var3))) {
               if (var1.equalsIgnoreCase("PlotSquared")) {
                  int var5 = Integer.parseInt(var4.getDescription().getVersion().replaceAll("[^0-9]", "").substring(0, 1));
                  if (var5 >= 6) {
                     try {
                        Class var6 = Class.forName("com.plotsquared.core.PlotAPI");
                        Object var7 = var6.newInstance();
                        Method var8 = var7.getClass().getMethod("registerListener", Object.class);
                        var8.invoke(var7, var2.init(this.plugin));
                     } catch (Exception var9) {
                        var9.printStackTrace();
                     }
                  } else if (var5 >= 5) {
                     PlotAPI var10 = new PlotAPI();
                     var10.registerListener(var2.init(this.plugin));
                  } else {
                     this.manager.registerEvents(var2.init(this.plugin), this.plugin);
                  }
               } else {
                  this.manager.registerEvents(var2.init(this.plugin), this.plugin);
               }

               var4.getLogger().info("Hooked into ".concat(var1));
               this.registeredHooks.add(var1);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean hook(String var1, Instantiator<ExternalActionContainer> var2, String var3, Predicate<PluginVersion> var4) {
      if (this.isHookDisabled(var1)) {
         return false;
      } else {
         Plugin var5 = this.manager.getPlugin(var1);
         if (var5 != null && var5.isEnabled()) {
            if (!this.isHookDisabled(var1) && (var3.isEmpty() || var5.getDescription().getAuthors().contains(var3))) {
               PluginVersion var6 = this.extractVersion(var5);
               var5.getLogger().info("Using internal version as " + var6.toString() + " for loading " + var1 + ".");
               if (var4.test(var6)) {
                  this.manager.registerEvents(var2.init(this.plugin), this.plugin);
                  var5.getLogger().info("Hooked into ".concat(var1));
                  this.registeredHooks.add(var1);
               } else {
                  var5.getLogger().info(var1.concat(" was present but its version is not supported."));
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean hook(String var1, Instantiator<ExternalActionContainer> var2, Predicate<PluginVersion> var3) {
      return this.hook(var1, var2, "", var3);
   }

   public boolean placeholderAPI(Set<String> var1) {
      if (this.isHookDisabled("placeholderapi")) {
         return false;
      } else if (Bukkit.getPluginManager().isPluginEnabled(this.plugin)) {
         this.registeredHooks.add("PlaceholderAPI");
         new PlaceholderApiQuests(this.plugin, var1);
         return true;
      } else {
         this.runRepeatingCheck("PlaceholderAPI", () -> {
            if (this.placeholderAPI(var1)) {
               this.attempts.get("PlaceholderAPI").getValue().cancel();
            }
         });
         return false;
      }
   }

   private void runRepeatingCheck(String var1, Runnable var2) {
      if (!this.attempts.containsKey(var1)) {
         FoliaScheduler.Task var3 = FoliaScheduler.runTaskTimer(this.plugin, () -> {
            int var3x = this.attempts.get(var1).getKey().incrementAndGet();
            if (var3x > 60) {
               this.attempts.get(var1).getValue().cancel();
            }

            var2.run();
         }, 200L, 200L);
         this.attempts.put(var1, ImmutablePair.of(new AtomicInteger(), var3));
      }
   }

   public boolean isHookDisabled(String var1) {
      return this.getDisabledHooks().contains(var1.toLowerCase());
   }

   private PluginVersion extractVersion(Plugin var1) {
      String var2 = var1.getDescription().getVersion().split(" ")[0];
      String[] var3 = var2.split("\\.");
      int var5 = 0;
      int var6 = 0;
      int var4;
      switch (var3.length) {
         case 1:
            var4 = this.entryToVersionNumber(var3[0]);
            break;
         case 2:
            var4 = this.entryToVersionNumber(var3[0]);
            var5 = this.entryToVersionNumber(var3[1]);
            break;
         default:
            var4 = this.entryToVersionNumber(var3[0]);
            var5 = this.entryToVersionNumber(var3[1]);
            var6 = this.entryToVersionNumber(var3[2]);
      }

      return new PluginVersion().setMajor(var4).setMinor(var5).setBugfix(var6);
   }

   public void hook(String var1, Instantiator<ExternalActionContainer> var2) {
      this.hook(var1, var2, "");
   }

   private int entryToVersionNumber(String var1) {
      StringBuilder var2 = new StringBuilder();

      for (char var6 : var1.toCharArray()) {
         if (!Character.isDigit(var6)) {
            break;
         }

         var2.append(var6);
      }

      return var2.toString().isEmpty() ? 0 : Integer.parseInt(var2.toString());
   }

   public static ActionRegistry getRegistry() {
      return registry;
   }

   public static boolean isIgnoreBlockCache() {
      return ignoreBlockCache;
   }

   public static BlockPlaceAntiAbuse getBlockPlaceAntiAbuse() {
      return blockPlaceAntiAbuse;
   }

   public Set<String> getRegisteredHooks() {
      return this.registeredHooks;
   }

   public ActionsReader getReader() {
      return this.reader;
   }

   public List<String> getDisabledHooks() {
      return this.disabledHooks;
   }

   public boolean isEnablePlaytime() {
      return this.enablePlaytime;
   }

   public void setEnablePlaytime(boolean var1) {
      this.enablePlaytime = var1;
   }

   public boolean isBrewingProtection() {
      return this.brewingProtection;
   }

   public void setBrewingProtection(boolean var1) {
      this.brewingProtection = var1;
   }

   public boolean isBreakProtection() {
      return this.breakProtection;
   }

   public void setBreakProtection(boolean var1) {
      this.breakProtection = var1;
   }
}
