package net.advancedplugins.jobs.impl.actions.external;

import net.Indyuce.mmoitems.api.event.PlayerUseCraftingStationEvent;
import net.Indyuce.mmoitems.api.event.item.ItemCustomRepairEvent;
import net.advancedplugins.jobs.impl.actions.ActionExecutionBuilder;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class MMOItemsQuests extends ActionQuestExecutor {
   private final JavaPlugin plugin;

   public MMOItemsQuests(JavaPlugin var1) {
      super(var1, "MMOItems");
      this.plugin = var1;
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerUseCraftingStation(PlayerUseCraftingStationEvent var1) {
      ActionExecutionBuilder var2 = this.executionBuilder("use_station")
         .root(var1.getRecipe().getId())
         .subRoot("station", var1.getStation().getId())
         .subRoot("interaction", var1.getInteraction().name());
      if (var1.hasResult()) {
         var2.subRoot(var1.getResult());
      }

      var2.buildAndExecute();
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onItemCustomRepair(ItemCustomRepairEvent var1) {
      FoliaScheduler.runTask(
         this.plugin,
         () -> this.executionBuilder("repair")
            .root(var1.getSourceItem().toItem())
            .progress(var1.getDurabilityIncrease())
            .subRoot("type", var1.getSourceItem().getNBTItem().hasType() ? var1.getSourceItem().getNBTItem().getType() : "")
            .canBeAsync()
            .buildAndExecute()
      );
   }
}
