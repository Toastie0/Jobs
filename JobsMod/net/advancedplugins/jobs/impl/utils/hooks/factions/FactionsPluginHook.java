package net.advancedplugins.jobs.impl.utils.hooks.factions;

import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.entity.Player;

public class FactionsPluginHook extends PluginHookInstance {
   public String getRelation(Player var1, Player var2) {
      return "NEUTRAL";
   }

   public String getRelationOfLand(Player var1) {
      return "null";
   }
}
