package net.advancedplugins.jobs.impl.utils.protection.external;

import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.HooksHandler;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.WorldGuardHook;
import net.advancedplugins.jobs.impl.utils.protection.ProtectionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardCheck implements ProtectionType {
   @Override
   public String getName() {
      return "WorldGuard";
   }

   @Override
   public boolean canBreak(Player var1, Location var2) {
      PluginHookInstance var3 = HooksHandler.getHook(HookPlugin.WORLDGUARD);
      return !var3.isEnabled() ? true : ((WorldGuardHook)var3).canBuild(var1, var2);
   }

   @Override
   public boolean canAttack(Player var1, Player var2) {
      return true;
   }

   @Override
   public boolean isProtected(Location var1) {
      PluginHookInstance var2 = HooksHandler.getHook(HookPlugin.WORLDGUARD);
      return !var2.isEnabled() ? false : ((WorldGuardHook)var2).isProtected(var1);
   }
}
