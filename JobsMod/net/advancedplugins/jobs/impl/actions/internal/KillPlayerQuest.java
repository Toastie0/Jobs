package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class KillPlayerQuest extends ActionContainer {
   public KillPlayerQuest(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onDamage(PlayerDeathEvent var1) {
      Player var2 = var1.getEntity().getKiller();
      Player var3 = var1.getEntity();
      if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
         NPCRegistry var4 = CitizensAPI.getNPCRegistry();
         if (var4.isNPC(var2) || var4.isNPC(var3)) {
            return;
         }
      }

      this.executionBuilder("kill-player").player(var2).root(var3.getName()).canBeAsync().progressSingle().buildAndExecute();
   }
}
