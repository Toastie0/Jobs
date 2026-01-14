package net.advancedplugins.jobs.impl.utils.protection.external;

import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.HooksHandler;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.SlimeFunHook;
import net.advancedplugins.jobs.impl.utils.protection.ProtectionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SlimeFunCheck implements ProtectionType {
   @Override
   public String getName() {
      return "SlimeFun";
   }

   @Override
   public boolean canBreak(Player var1, Location var2) {
      PluginHookInstance var3 = HooksHandler.getHook(HookPlugin.SLIMEFUN);
      return !var3.isEnabled() ? true : ((SlimeFunHook)var3).canBuild(var1, var2);
   }

   @Override
   public boolean canAttack(Player var1, Player var2) {
      return true;
   }

   @Override
   public boolean isProtected(Location var1) {
      return false;
   }
}
