package net.advancedplugins.jobs.impl.utils.protection.external;

import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.HooksHandler;
import net.advancedplugins.jobs.impl.utils.hooks.factions.FactionsPluginHook;
import net.advancedplugins.jobs.impl.utils.protection.ProtectionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FactionsCheck implements ProtectionType {
   @Override
   public String getName() {
      return "Factions";
   }

   @Override
   public boolean canBreak(Player var1, Location var2) {
      FactionsPluginHook var3 = (FactionsPluginHook)HooksHandler.getHook(HookPlugin.FACTIONS);
      if (!var3.isEnabled()) {
         return true;
      } else {
         String var4 = var3.getRelationOfLand(var1);
         return var4.equalsIgnoreCase("Wilderness") || var4.equalsIgnoreCase("null") || var4.equalsIgnoreCase("neutral") || var4.equalsIgnoreCase("member");
      }
   }

   @Override
   public boolean isProtected(Location var1) {
      return false;
   }

   @Override
   public boolean canAttack(Player var1, Player var2) {
      return true;
   }
}
