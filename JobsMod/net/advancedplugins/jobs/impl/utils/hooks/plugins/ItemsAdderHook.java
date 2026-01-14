package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import java.util.Collections;
import java.util.List;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.SchedulerUtils;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ItemsAdderHook extends PluginHookInstance implements Listener {
   private final Plugin plugin;

   public ItemsAdderHook(Plugin var1) {
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

   public boolean isCustomItem(ItemStack var1) {
      return CustomStack.byItemStack(var1) != null;
   }

   public boolean isCustomBlock(Block var1) {
      return var1 == null ? false : CustomBlock.byAlreadyPlaced(var1) != null;
   }

   public boolean removeBlock(Block var1) {
      return !this.isCustomBlock(var1) ? false : CustomBlock.byAlreadyPlaced(var1).remove();
   }

   public List<ItemStack> getLootForCustomBlock(Block var1) {
      return !this.isCustomBlock(var1) ? Collections.emptyList() : CustomBlock.byAlreadyPlaced(var1).getLoot();
   }

   public List<ItemStack> getLootForCustomBlock(ItemStack var1, Block var2) {
      return !this.isCustomBlock(var2) ? Collections.emptyList() : CustomBlock.byAlreadyPlaced(var2).getLoot(var1, false);
   }

   public ItemStack setCustomItemDurability(ItemStack var1, int var2) {
      CustomStack var3 = CustomStack.byItemStack(var1);
      var3.setDurability(var2);
      return var3.getItemStack();
   }

   public boolean canBeBrokenWith(ItemStack var1, Block var2) {
      return !CustomBlock.byAlreadyPlaced(var2).getLoot(var1, false).isEmpty();
   }

   public ItemStack getByName(String var1) {
      CustomStack var2 = CustomStack.getInstance(var1);
      return var2 == null ? null : var2.getItemStack();
   }

   public int getCustomItemDurability(ItemStack var1) {
      return ItemsAdder.getCustomItemDurability(var1);
   }

   public int getCustomItemMaxDurability(ItemStack var1) {
      return ItemsAdder.getCustomItemMaxDurability(var1);
   }

   @EventHandler(
      priority = EventPriority.LOW,
      ignoreCancelled = true
   )
   public void onCustomBlockBreak(CustomBlockBreakEvent var1) {
      Player var2 = var1.getPlayer();
      Block var3 = var1.getBlock();
      if (var3 != null) {
         if (var3.hasMetadata("telepathy-broken-itemsadder")) {
            var3.removeMetadata("telepathy-broken-itemsadder", this.plugin);
            var1.setCancelled(true);
            SchedulerUtils.runTaskLater(() -> {
               CustomBlock var2x = CustomBlock.byAlreadyPlaced(var3);
               if (var2x != null) {
                  ItemStack[] var3x = var2x.getLoot(var2.getEquipment().getItemInMainHand(), false).toArray(new ItemStack[0]);
                  if (var2x.remove()) {
                     ASManager.giveItem(var2, var3x);
                  }
               }
            });
         }
      }
   }

   public String replaceFontImages(String var1) {
      return FontImageWrapper.replaceFontImages(var1);
   }

   public String replaceFontImages(Player var1, String var2) {
      return FontImageWrapper.replaceFontImages(var1, var2);
   }
}
