package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.block.Block;

public class SuperiorSkyblock2Hook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.SUPERIORSKYBLOCK2.getPluginName();
   }

   public int getStackedAmount(Block var1) {
      return SuperiorSkyblockAPI.getStackedBlocks().getStackedBlockAmount(var1);
   }

   public boolean isStackedBlock(Block var1) {
      return this.getStackedAmount(var1) > 1;
   }
}
