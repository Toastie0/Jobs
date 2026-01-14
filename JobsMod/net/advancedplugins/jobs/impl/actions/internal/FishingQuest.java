package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class FishingQuest extends ActionContainer {
   public FishingQuest(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGHEST
   )
   public void onFishCaught(PlayerFishEvent var1) {
      Player var2 = var1.getPlayer();
      Entity var3 = var1.getCaught();
      State var4 = var1.getState();
      if (var4.equals(State.CAUGHT_FISH) && var3 instanceof Item) {
         ItemStack var5 = ((Item)var3).getItemStack();
         this.executionBuilder("fish").player(var2).progressSingle().root(var5).buildAndExecute();
      }
   }
}
