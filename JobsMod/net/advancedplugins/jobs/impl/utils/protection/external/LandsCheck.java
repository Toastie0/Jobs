package net.advancedplugins.jobs.impl.utils.protection.external;

import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.HooksHandler;
import net.advancedplugins.jobs.impl.utils.hooks.plugins.LandsHook;
import net.advancedplugins.jobs.impl.utils.protection.ProtectionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LandsCheck implements ProtectionType {
   @Override
   public String getName() {
      return "Lands";
   }

   @Override
   public boolean canBreak(Player var1, Location var2) {
      LandsHook var3 = (LandsHook)HooksHandler.getHook(HookPlugin.LANDS);
      return var3 == null ? true : var3.canBuild(var1, var2);
   }

   @Override
   public boolean canAttack(Player var1, Player var2) {
      LandsHook var3 = (LandsHook)HooksHandler.getHook(HookPlugin.LANDS);
      return var3 == null ? true : var3.canAttack(var1, var2);
   }

   @Override
   public boolean isProtected(Location var1) {
      LandsHook var2 = (LandsHook)HooksHandler.getHook(HookPlugin.LANDS);
      return var2 == null ? false : var2.isProtected(var1);
   }
}
