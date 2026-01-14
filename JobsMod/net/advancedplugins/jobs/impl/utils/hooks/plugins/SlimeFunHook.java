package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.util.Collection;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SlimeFunHook extends PluginHookInstance {
   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.SLIMEFUN.getPluginName();
   }

   public boolean canBuild(Player var1, Location var2) {
      return BlockStorage.check(var2.getBlock()) == null;
   }

   public boolean isSlimefunItem(Location var1) {
      return BlockStorage.check(var1) != null;
   }

   public boolean isSlimefunItem(Block var1) {
      return BlockStorage.check(var1) != null;
   }

   public boolean hasSoulbound(ItemStack var1, World var2) {
      return SlimefunUtils.isSoulbound(var1, var2);
   }

   public Collection<ItemStack> getDrops(Location var1) {
      return BlockStorage.check(var1).getDrops();
   }
}
