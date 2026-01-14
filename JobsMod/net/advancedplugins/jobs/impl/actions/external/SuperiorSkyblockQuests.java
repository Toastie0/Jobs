package net.advancedplugins.jobs.impl.actions.external;

import com.bgsoftware.superiorskyblock.api.events.IslandCreateEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandJoinEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandUpgradeEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import net.advancedplugins.jobs.impl.actions.objects.variable.ActionResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class SuperiorSkyblockQuests extends ActionQuestExecutor {
   public SuperiorSkyblockQuests(JavaPlugin var1) {
      super(var1, "superiorskyblock2");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onIslandCreate(IslandCreateEvent var1) {
      if (var1.getPlayer() != null) {
         Player var2 = Bukkit.getPlayer(var1.getPlayer().getUniqueId());
         this.execute("create", var2, ActionResult::none);
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onIslandJoin(IslandJoinEvent var1) {
      Player var2 = Bukkit.getPlayer(var1.getPlayer().getUniqueId());
      this.execute("join", var2, ActionResult::none);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onIslandUpgrade(IslandUpgradeEvent var1) {
      if (var1.getPlayer() != null) {
         Player var2 = Bukkit.getPlayer(var1.getPlayer().getUniqueId());
         String var3 = var1.getUpgradeName();
         if (var3 != null) {
            this.execute("upgrade", var2, var1x -> var1x.root(var3));
         }
      }
   }
}
