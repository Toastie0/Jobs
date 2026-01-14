package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.LandWorld;
import me.angeschossen.lands.api.player.LandPlayer;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LandsHook extends PluginHookInstance {
   LandsIntegration landsIntegration = LandsIntegration.of(ASManager.getInstance());

   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.LANDS.getPluginName();
   }

   public boolean canBuild(Player var1, Location var2) {
      LandWorld var3 = this.landsIntegration.getWorld(var1.getWorld());
      LandPlayer var4 = this.landsIntegration.getLandPlayer(var1.getUniqueId());
      if (var3 == null) {
         return true;
      } else {
         return var4 == null ? true : var3.hasFlag(var1, var2, var1.getInventory().getItemInMainHand().getType(), Flags.BLOCK_BREAK, false);
      }
   }

   public boolean canAttack(Player var1, Player var2) {
      return this.landsIntegration.canPvP(var1, var2, var2.getLocation(), false, false);
   }

   public boolean canMobsSpawn(Location var1) {
      try {
         LandWorld var2 = this.landsIntegration.getWorld(var1.getWorld());
         if (var2 == null) {
            return true;
         } else {
            Area var3 = var2.getArea(var1);
            return var3 == null ? true : var3.hasNaturalFlag(Flags.MONSTER_SPAWN);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
         return true;
      }
   }

   public boolean isProtected(Location var1) {
      return this.landsIntegration.getArea(var1) != null;
   }
}
