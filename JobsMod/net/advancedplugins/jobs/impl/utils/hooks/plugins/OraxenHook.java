package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.utils.drops.Loot;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class OraxenHook extends PluginHookInstance implements Listener {
   private final Plugin plugin;

   public OraxenHook(Plugin var1) {
      this.plugin = var1;
   }

   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.ITEMSADDER.getPluginName();
   }

   public boolean isCustomBlock(Block var1) {
      return OraxenBlocks.isOraxenBlock(var1);
   }

   public boolean isCustomStringBlock(Block var1) {
      return OraxenBlocks.isOraxenStringBlock(var1);
   }

   public boolean isCustomNoteBlock(Block var1) {
      return OraxenBlocks.isOraxenNoteBlock(var1);
   }

   @EventHandler(
      priority = EventPriority.LOW,
      ignoreCancelled = true
   )
   public void onCustomBlockBreak(OraxenNoteBlockBreakEvent var1) {
      Block var2 = var1.getBlock();
      if (var2 != null) {
         if (var2.hasMetadata("ae-oraxen-cancel")) {
            var2.removeMetadata("ae-oraxen-cancel", this.plugin);
            var1.setCancelled(true);
         }
      }
   }

   public boolean canBeBrokenWith(ItemStack var1, Block var2) {
      if (this.isCustomNoteBlock(var2)) {
         return OraxenBlocks.getNoteBlockMechanic(var2).getDrop().isToolEnough(var1);
      } else {
         return this.isCustomStringBlock(var2) ? OraxenBlocks.getStringMechanic(var2).getDrop().isToolEnough(var1) : false;
      }
   }

   public List<ItemStack> getLootForCustomBlock(Block var1) {
      if (this.isCustomNoteBlock(var1)) {
         return OraxenBlocks.getNoteBlockMechanic(var1).getDrop().getLoots().stream().<ItemStack>map(Loot::getItemStack).collect(Collectors.toList());
      } else {
         return (List<ItemStack>)(this.isCustomStringBlock(var1)
            ? OraxenBlocks.getStringMechanic(var1).getDrop().getLoots().stream().<ItemStack>map(Loot::getItemStack).collect(Collectors.toList())
            : new ArrayList<>());
      }
   }

   public boolean removeBlock(Block var1) {
      return !this.isCustomBlock(var1) ? false : OraxenBlocks.remove(var1.getLocation(), null);
   }
}
