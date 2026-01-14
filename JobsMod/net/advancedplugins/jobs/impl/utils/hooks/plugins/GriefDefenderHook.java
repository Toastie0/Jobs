package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.permission.flag.Flag;
import java.util.HashSet;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefDefenderHook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.GRIEFDEFENDER.getPluginName();
   }

   public boolean canBuild(Player var1, Location var2) {
      Claim var3 = GriefDefender.getCore().getClaimAt(var2);
      return var3 == null ? true : true;
   }

   public boolean canIceGenerate(Location var1) {
      try {
         Claim var2 = GriefDefender.getCore().getClaimAt(var1);
         return var2 == null ? true : var2.getFlagPermissionValue(Flag.builder().name("ice-growth").build(), new HashSet()).asBoolean();
      } catch (Exception var3) {
         return true;
      }
   }
}
