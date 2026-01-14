package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.TownyPermission.ActionType;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TownyHook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.TOWNY.getPluginName();
   }

   public boolean canBuild(Player var1, Location var2) {
      return PlayerCacheUtil.getCachePermission(var1, var2, var2.getBlock().getType(), ActionType.BUILD);
   }

   public boolean canBreak(Player var1, Location var2) {
      return PlayerCacheUtil.getCachePermission(var1, var2, var2.getBlock().getType(), ActionType.DESTROY);
   }

   public boolean hasKeepInventory(Player var1) {
      Resident var2 = TownyAPI.getInstance().getResident(var1);
      if (var2 == null) {
         return false;
      } else {
         TownBlock var3 = TownyAPI.getInstance().getTownBlock(var1.getLocation());
         return this.getKeepInventoryValue(var2, var3);
      }
   }

   private boolean getKeepInventoryValue(Resident var1, TownBlock var2) {
      boolean var3 = TownySettings.getKeepInventoryInTowns() && var2 != null;
      if (var2 == null) {
         return var3;
      } else {
         if (var1.hasTown() && !var3) {
            Town var4 = var1.getTownOrNull();
            Town var5 = var2.getTownOrNull();
            if (TownySettings.getKeepInventoryInOwnTown() && var5.equals(var4)) {
               var3 = true;
            }

            if (TownySettings.getKeepInventoryInAlliedTowns() && !var3 && var5.isAlliedWith(var4)) {
               var3 = true;
            }
         }

         if (TownySettings.getKeepInventoryInArenas() && !var3 && var2.getType() == TownBlockType.ARENA) {
            var3 = true;
         }

         return var3;
      }
   }
}
