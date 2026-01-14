package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.perms.Relation;
import net.advancedplugins.jobs.impl.utils.hooks.factions.FactionsPluginHook;
import org.bukkit.entity.Player;

public class FactionsMCoreHook extends FactionsPluginHook {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return "FactionsMCore";
   }

   @Override
   public String getRelation(Player var1, Player var2) {
      FPlayer var3 = FPlayers.getInstance().getByPlayer(var1);
      FPlayer var4 = FPlayers.getInstance().getByPlayer(var2);
      return var3.getRelationTo(var4).toString();
   }

   @Override
   public String getRelationOfLand(Player var1) {
      FPlayer var2 = FPlayers.getInstance().getByPlayer(var1);
      Relation var3 = var2.getRelationToLocation();
      return var3.toString();
   }
}
