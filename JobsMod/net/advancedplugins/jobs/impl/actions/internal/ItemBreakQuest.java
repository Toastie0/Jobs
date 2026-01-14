package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemBreakQuest extends ActionContainer {
   public ItemBreakQuest(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onItemBreak(PlayerItemBreakEvent var1) {
      Player var2 = var1.getPlayer();
      ItemStack var3 = var1.getBrokenItem();
      var3.setDurability((short)0);
      this.executionBuilder("item-break").player(var2).root(var3).progressSingle().buildAndExecute();
   }
}
