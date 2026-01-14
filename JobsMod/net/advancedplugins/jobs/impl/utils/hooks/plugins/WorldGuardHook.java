package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardHook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return "WorldGuardHook";
   }

   public boolean canBuild(Player var1, Location var2) {
      if (var1.isOp()) {
         return true;
      } else {
         try {
            return WorldGuard.getInstance()
                  .getPlatform()
                  .getRegionContainer()
                  .createQuery()
                  .testState(BukkitAdapter.adapt(var2), WorldGuardPlugin.inst().wrapPlayer(var1), new StateFlag[]{Flags.BUILD})
               || WorldGuard.getInstance()
                  .getPlatform()
                  .getRegionContainer()
                  .createQuery()
                  .testState(BukkitAdapter.adapt(var2), WorldGuardPlugin.inst().wrapPlayer(var1), new StateFlag[]{Flags.BLOCK_BREAK});
         } catch (Exception var4) {
            var4.printStackTrace();
            ASManager.getInstance()
               .getLogger()
               .warning(
                  "Error with WorldGuard v(" + Bukkit.getPluginManager().getPlugin("WorldGuard").getDescription().getVersion() + ") - Version unsupported"
               );
            return false;
         }
      }
   }

   public boolean isProtected(Location var1) {
      try {
         return WorldGuard.getInstance()
               .getPlatform()
               .getRegionContainer()
               .get(BukkitAdapter.adapt(var1.getWorld()))
               .getApplicableRegions(BukkitAdapter.asBlockVector(var1))
               .size()
            > 0;
      } catch (Exception var3) {
         return false;
      }
   }

   public boolean canMobsSpawn(Location var1) {
      for (ProtectedRegion var3 : WorldGuard.getInstance()
         .getPlatform()
         .getRegionContainer()
         .get(BukkitAdapter.adapt(var1.getWorld()))
         .getApplicableRegions(BukkitAdapter.asBlockVector(var1))) {
         if (var3.getFlag(Flags.MOB_SPAWNING) == State.DENY) {
            return false;
         }
      }

      return true;
   }
}
