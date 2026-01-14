package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ResidenceHook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.RESIDENCE.getPluginName();
   }

   public boolean canBuild(Player var1, Location var2) {
      ClaimedResidence var3 = Residence.getInstance().getResidenceManager().getByLoc(var2);
      if (var3 == null) {
         return true;
      } else {
         ResidencePermissions var4 = var3.getPermissions();
         return var4.playerHas(var1, Flags.build, true);
      }
   }

   public boolean isProtected(Location var1) {
      ClaimedResidence var2 = Residence.getInstance().getResidenceManager().getByLoc(var1);
      return var2 != null;
   }
}
