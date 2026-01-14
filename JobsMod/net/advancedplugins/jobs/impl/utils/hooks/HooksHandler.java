package net.advancedplugins.jobs.impl.utils.hooks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import net.advancedplugins.jobs.impl.utils.hooks.holograms.CMIHologramHandler;
import net.advancedplugins.jobs.impl.utils.hooks.holograms.DecentHologramsHandler;
import net.advancedplugins.jobs.impl.utils.hooks.holograms.HologramHandler;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.AdvancedChestsHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.AdvancedEnchantmentsHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.AdvancedSkillsHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.AuraSkillsHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.BeaconsPlus3Hook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.CMIHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.DiscordSRVHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.DynmapHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.EssentialsHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.FactionsMCoreHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.FactionsUUIDHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.GeyserHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.GriefDefenderHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.GriefPreventionHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.ItemsAdderHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.LandsHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.LuckPermsHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.McMMOHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.MythicMobsHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.OraxenHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.PlaceholderAPIHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.PremiumVanishHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.ProtectionStonesHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.ResidenceHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.SlimeFunHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.SuperVanishHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.SuperiorSkyblock2Hook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.TabHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.TownyChatHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.TownyHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.VaultHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.ViaVersionHook;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.WorldGuardHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class HooksHandler {
   private static HologramHandler holograms;
   private static JavaPlugin plugin;
   private static ImmutableMap<HookPlugin, PluginHookInstance> pluginHookMap = ImmutableMap.builder().build();

   public static void hook(JavaPlugin var0) {
      if (!pluginHookMap.isEmpty()) {
         pluginHookMap = ImmutableMap.builder().build();
      }

      plugin = var0;
      holograms();
      if (isPluginEnabled(HookPlugin.PROTOCOLLIB.getPluginName())) {
         registerNew(HookPlugin.PROTOCOLLIB, new PluginHookInstance());
      }

      if (isPluginEnabled(HookPlugin.AURASKILLS.getPluginName())) {
         registerNew(HookPlugin.AURASKILLS, new AuraSkillsHook(), true);
      }

      if (isPluginEnabled(HookPlugin.MCMMO.getPluginName())) {
         registerNew(HookPlugin.MCMMO, new McMMOHook(), true);
      }

      if (isPluginEnabled(HookPlugin.ADVANCEDENCHANTMENTS.getPluginName())) {
         registerNew(HookPlugin.ADVANCEDENCHANTMENTS, new AdvancedEnchantmentsHook());
      }

      if (isPluginEnabled(HookPlugin.ADVANCEDSKILLS.getPluginName())) {
         registerNew(HookPlugin.ADVANCEDSKILLS, new AdvancedSkillsHook());
      }

      if (isPluginEnabled(HookPlugin.WORLDGUARD.getPluginName())) {
         registerNew(HookPlugin.WORLDGUARD, new WorldGuardHook());
      }

      if (isPluginEnabled(HookPlugin.GRIEFPREVENTION.getPluginName())) {
         registerNew(HookPlugin.GRIEFPREVENTION, new GriefPreventionHook());
      }

      if (isPluginEnabled(HookPlugin.GRIEFDEFENDER.getPluginName())) {
         registerNew(HookPlugin.GRIEFDEFENDER, new GriefDefenderHook());
      }

      if (isPluginEnabled(HookPlugin.PLACEHOLDERAPI.getPluginName())) {
         registerNew(HookPlugin.PLACEHOLDERAPI, new PlaceholderAPIHook());
      }

      if (isPluginEnabled(HookPlugin.SLIMEFUN.getPluginName())) {
         registerNew(HookPlugin.SLIMEFUN, new SlimeFunHook());
      }

      if (isPluginEnabled(HookPlugin.MYTHICMOBS.getPluginName())) {
         registerNew(HookPlugin.MYTHICMOBS, new MythicMobsHook(), true);
      }

      if (isPluginEnabled(HookPlugin.TOWNY.getPluginName())) {
         registerNew(HookPlugin.TOWNY, new TownyHook());
      }

      if (isPluginEnabled(HookPlugin.TOWNYCHAT.getPluginName())) {
         registerNew(HookPlugin.TOWNYCHAT, new TownyChatHook());
      }

      if (isPluginEnabled(HookPlugin.LANDS.getPluginName())) {
         registerNew(HookPlugin.LANDS, new LandsHook());
      }

      if (isPluginEnabled(HookPlugin.SUPERIORSKYBLOCK2.getPluginName())) {
         registerNew(HookPlugin.SUPERIORSKYBLOCK2, new SuperiorSkyblock2Hook());
      }

      if (isPluginEnabled(HookPlugin.ORAXEN.getPluginName())) {
         registerNew(HookPlugin.ORAXEN, new OraxenHook(var0), true);
      }

      if (isPluginEnabled(HookPlugin.PROTECTIONSTONES.getPluginName())) {
         registerNew(HookPlugin.PROTECTIONSTONES, new ProtectionStonesHook());
      }

      if (isPluginEnabled(HookPlugin.RESIDENCE.getPluginName())) {
         registerNew(HookPlugin.RESIDENCE, new ResidenceHook());
      }

      if (isPluginEnabled(HookPlugin.GEYSER.getPluginName())) {
         registerNew(HookPlugin.GEYSER, new GeyserHook());
      }

      if (isPluginEnabled(HookPlugin.DYNMAP.getPluginName())) {
         registerNew(HookPlugin.DYNMAP, new DynmapHook());
      }

      if (isPluginEnabled(HookPlugin.ESSENTIALS.getPluginName())) {
         registerNew(HookPlugin.ESSENTIALS, new EssentialsHook());
      }

      if (isPluginEnabled(HookPlugin.CMI.getPluginName())) {
         registerNew(HookPlugin.CMI, new CMIHook());
      }

      if (isPluginEnabled(HookPlugin.BEACONPLUS3.getPluginName())) {
         registerNew(HookPlugin.BEACONPLUS3, new BeaconsPlus3Hook());
      }

      if (isPluginEnabled(HookPlugin.VAULT.getPluginName())) {
         registerNew(HookPlugin.VAULT, new VaultHook());
      }

      if (isPluginEnabled(HookPlugin.LUCKPERMS.getPluginName())) {
         registerNew(HookPlugin.LUCKPERMS, new LuckPermsHook());
      }

      if (isPluginEnabled(HookPlugin.VIAVERSION.getPluginName())) {
         registerNew(HookPlugin.VIAVERSION, new ViaVersionHook());
      }

      if (isPluginEnabled(HookPlugin.TAB.getPluginName())) {
         registerNew(HookPlugin.TAB, new TabHook());
      }

      FoliaScheduler.runTaskLater(var0, () -> {
         if (isPluginEnabled(HookPlugin.FACTIONS.getPluginName())) {
            if (isPluginEnabled("MassiveCore")) {
               registerNew(HookPlugin.FACTIONS, new FactionsMCoreHook());
            } else {
               registerNew(HookPlugin.FACTIONS, new FactionsUUIDHook());
            }
         }

         if (isPluginEnabled(HookPlugin.ITEMSADDER.getPluginName())) {
            registerNew(HookPlugin.ITEMSADDER, new ItemsAdderHook(var0), true);
         }

         if (isPluginEnabled(HookPlugin.ADVANCEDCHESTS.getPluginName())) {
            registerNew(HookPlugin.ADVANCEDCHESTS, new AdvancedChestsHook());
         }

         if (isPluginEnabled(HookPlugin.PREMIUMVANISH.getPluginName())) {
            registerNew(HookPlugin.PREMIUMVANISH, new PremiumVanishHook());
         }

         if (isPluginEnabled(HookPlugin.DISCORDSRV.getPluginName())) {
            registerNew(HookPlugin.DISCORDSRV, new DiscordSRVHook());
         }

         if (isPluginEnabled(HookPlugin.SUPERVANISH.getPluginName())) {
            registerNew(HookPlugin.SUPERVANISH, new SuperVanishHook());
         }

         sendHookMessage(var0);
      }, 10L);
   }

   private static void sendHookMessage(JavaPlugin var0) {
      if (!pluginHookMap.isEmpty()) {
         StringBuilder var1 = new StringBuilder();
         UnmodifiableIterator var2 = pluginHookMap.keySet().iterator();

         while (var2.hasNext()) {
            HookPlugin var3 = (HookPlugin)var2.next();
            var1.append(var3.getPluginName()).append(", ");
         }

         var0.getLogger().info("Successfully hooked into " + var1.substring(0, var1.length() - 2) + ".");
      }
   }

   private static void registerNew(HookPlugin var0, PluginHookInstance var1) {
      registerNew(var0, var1, false);
   }

   private static void registerNew(HookPlugin var0, PluginHookInstance var1, boolean var2) {
      pluginHookMap = ImmutableMap.builder().putAll(pluginHookMap).put(var0, var1).build();
      if (var2) {
         plugin.getServer().getPluginManager().registerEvents((Listener)var1, plugin);
      }
   }

   public static PluginHookInstance getHook(HookPlugin var0) {
      return (PluginHookInstance)pluginHookMap.get(var0);
   }

   private static void holograms() {
      if (isPluginEnabled("CMI")) {
         holograms = new CMIHologramHandler(plugin);
      } else if (isPluginEnabled("DecentHolograms")) {
         holograms = new DecentHologramsHandler(plugin);
      } else if (isPluginEnabled("HolographicDisplays")) {
         holograms = new DecentHologramsHandler(plugin);
      } else {
         holograms = new HologramHandler(plugin);
      }
   }

   private static boolean isPluginEnabled(String var0) {
      return Bukkit.getPluginManager().isPluginEnabled(var0);
   }

   public static boolean isEnabled(HookPlugin var0) {
      return pluginHookMap.containsKey(var0) && isPluginEnabled(var0.getPluginName());
   }

   public static boolean isPlayerVanished(Player var0) {
      return getVanishHook() != null && getVanishHook().isPlayerVanished(var0);
   }

   @Nullable
   public static VanishHook getVanishHook() {
      if (isEnabled(HookPlugin.PREMIUMVANISH)) {
         return (PremiumVanishHook)getHook(HookPlugin.PREMIUMVANISH);
      } else if (isEnabled(HookPlugin.SUPERVANISH)) {
         return (SuperVanishHook)getHook(HookPlugin.SUPERVANISH);
      } else if (isEnabled(HookPlugin.CMI)) {
         return (CMIHook)getHook(HookPlugin.CMI);
      } else {
         return isEnabled(HookPlugin.ESSENTIALS) ? (EssentialsHook)getHook(HookPlugin.ESSENTIALS) : null;
      }
   }

   @Nullable
   public static PermissionHook getPermissionHook() {
      return (PermissionHook)(isEnabled(HookPlugin.LUCKPERMS) ? (LuckPermsHook)getHook(HookPlugin.LUCKPERMS) : (VaultHook)getHook(HookPlugin.VAULT));
   }

   public static HologramHandler getHolograms() {
      return holograms;
   }
}
