package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefPreventionHook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.GRIEFPREVENTION.getPluginName();
   }

   public boolean canBuild(Player var1, Location var2) {
      Claim var3 = GriefPrevention.instance.dataStore.getClaimAt(var2, false, null);
      return var3 == null ? true : var3.allowAccess(var1) == null;
   }
}
