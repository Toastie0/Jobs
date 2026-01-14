package net.advancedplugins.jobs.impl.utils.protection.internal;

import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.protection.ProtectionType;
import net.advancedplugins.jobs.impl.utils.protection.events.FakeAdvancedBlockBreakEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

public class GlobalProtCheck implements ProtectionType, Listener {
   public GlobalProtCheck() {
      Bukkit.getPluginManager().registerEvents(this, ASManager.getInstance());
   }

   @Override
   public String getName() {
      return "vanilla";
   }

   @Override
   public boolean canAttack(Player var1, Player var2) {
      return true;
   }

   @Override
   public boolean canBreak(Player var1, Location var2) {
      Block var3 = var2.getBlock();
      var3.setMetadata("blockbreakevent-ignore", new FixedMetadataValue(ASManager.getInstance(), true));
      FakeAdvancedBlockBreakEvent var4 = new FakeAdvancedBlockBreakEvent(var3, var1);
      Bukkit.getPluginManager().callEvent(var4);
      boolean var5 = var4.isCancelled() && !var3.hasMetadata("ae-fake-cancel");
      if (var3.hasMetadata("blockbreakevent-ignore")) {
         var3.removeMetadata("blockbreakevent-ignore", ASManager.getInstance());
      }

      if (var3.hasMetadata("ae-fake-cancel")) {
         var3.removeMetadata("ae-fake-cancel", ASManager.getInstance());
      }

      return !var5;
   }

   @Override
   public boolean isProtected(Location var1) {
      return false;
   }

   @EventHandler(
      priority = EventPriority.NORMAL,
      ignoreCancelled = true
   )
   private void onFakeBlockBreak(FakeAdvancedBlockBreakEvent var1) {
      var1.setCancelled(true);
      var1.getBlock().setMetadata("ae-fake-cancel", new FixedMetadataValue(ASManager.getInstance(), true));
   }
}
