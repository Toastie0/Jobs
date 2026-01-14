package net.advancedplugins.jobs.impl.utils.hooks.plugins;

import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent.Cause;
import dev.aurelium.auraskills.api.event.mana.TerraformBlockBreakEvent;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.advancedplugins.jobs.impl.utils.abilities.DropsSettings;
import net.advancedplugins.jobs.impl.utils.abilities.SmeltMaterial;
import net.advancedplugins.jobs.impl.utils.hooks.HookPlugin;
import net.advancedplugins.jobs.impl.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class AuraSkillsHook extends PluginHookInstance implements Listener {
   private final ConcurrentHashMap<Vector, AuraSkillsHook.BrokenBlockInformation> brokenBlocksMap = new ConcurrentHashMap<>();
   private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public String getName() {
      return HookPlugin.AURASKILLS.getPluginName();
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onLoot(LootDropEvent var1) {
      if (!var1.getCause().equals(Cause.EPIC_CATCH)) {
         Player var2 = var1.getPlayer();
         ItemStack var3 = var1.getItem();
         Location var4 = var1.getLocation().clone();
         Vector var5 = var4.getBlock().getLocation().toVector();
         ItemStack var6 = var3;
         AuraSkillsHook.BrokenBlockInformation var7 = this.brokenBlocksMap.get(var5);
         if (var7 != null) {
            if (var2.equals(var7.player)) {
               if (var7.settings.isSmelt()) {
                  var6 = SmeltMaterial.material(var3);
               }

               var1.setItem(var6);
               var1.setToInventory(var7.settings.isAddToInventory());
            }
         }
      }
   }

   public boolean isTerraformEvent(BlockBreakEvent var1) {
      return var1 instanceof TerraformBlockBreakEvent;
   }

   public void addBrokenBlockToMap(Block var1, Player var2, DropsSettings var3) {
      Location var4 = var1.getLocation();
      this.brokenBlocksMap.put(var4.toVector(), new AuraSkillsHook.BrokenBlockInformation(var2, var3));
      this.executorService.schedule(() -> this.brokenBlocksMap.remove(var4.toVector()), 2000L, TimeUnit.MILLISECONDS);
   }

   static class BrokenBlockInformation {
      public final Player player;
      public final DropsSettings settings;

      public BrokenBlockInformation(Player var1, DropsSettings var2) {
         this.player = var1;
         this.settings = var2;
      }
   }
}
