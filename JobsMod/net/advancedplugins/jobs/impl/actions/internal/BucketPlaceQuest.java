package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BucketPlaceQuest extends ActionContainer {
   private final JavaPlugin plugin;

   public BucketPlaceQuest(JavaPlugin var1) {
      super(var1);
      this.plugin = var1;
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGHEST
   )
   public void onBlockPlace(PlayerBucketEmptyEvent var1) {
      Player var2 = var1.getPlayer();
      Material var3 = var1.getBucket();
      this.executionBuilder("bucket-place").player(var2).root(new ItemStack(var3)).progressSingle().buildAndExecute();
   }
}
