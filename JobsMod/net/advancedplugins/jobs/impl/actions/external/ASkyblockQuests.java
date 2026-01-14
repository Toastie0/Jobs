package net.advancedplugins.jobs.impl.actions.external;

import com.wasteofplastic.askyblock.events.IslandNewEvent;
import com.wasteofplastic.askyblock.events.WarpCreateEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import net.advancedplugins.jobs.impl.actions.objects.variable.ActionResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ASkyblockQuests extends ActionQuestExecutor {
   public ASkyblockQuests(JavaPlugin var1) {
      super(var1, "askyblock");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onNewIsland(IslandNewEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getSchematicName().getName();
      if (var3 != null) {
         this.execute("create_island", var2, var1x -> var1x.root(var3));
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onWarpCreate(WarpCreateEvent var1) {
      Player var2 = Bukkit.getPlayer(var1.getCreator());
      this.execute("warp", var2, ActionResult::none);
      this.execute("create_warp", var2, ActionResult::none);
   }
}
