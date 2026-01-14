package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class HoneyExtractQuests extends ActionContainer {
   public HoneyExtractQuests(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onHoneyExtract(PlayerInteractEvent var1) {
      Player var2 = var1.getPlayer();
      Block var3 = var1.getClickedBlock();
      if (var1.hasItem()
         && var1.hasBlock()
         && var3 != null
         && var1.getAction() == Action.RIGHT_CLICK_BLOCK
         && !var2.isSneaking()
         && MinecraftVersion.getVersion().getVersionId() >= 1151) {
         if (var3.getType().equals(Material.BEE_NEST) || var3.getType().equals(Material.BEEHIVE)) {
            Beehive var4 = (Beehive)var3.getBlockData();
            if (var4.getHoneyLevel() >= 5) {
               if (var2.getItemInHand().getType().equals(Material.GLASS_BOTTLE)) {
                  this.executionBuilder("honey-extract").player(var2).root(var3).progressSingle().buildAndExecute();
               } else if (var2.getItemInHand().getType().equals(Material.SHEARS)) {
                  this.executionBuilder("honey-comb-extract").player(var2).root(var3).progressSingle().buildAndExecute();
               }
            }
         }
      }
   }
}
