package net.advancedplugins.jobs.impl.actions.internal;

import net.advancedplugins.jobs.impl.actions.ActionRegistry;
import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import net.advancedplugins.jobs.impl.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;

public class ConsumeQuest extends ActionContainer {
   public ConsumeQuest(JavaPlugin var1) {
      super(var1);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onItemConsume(PlayerItemConsumeEvent var1) {
      Player var2 = var1.getPlayer();
      ItemStack var3 = var1.getItem();
      if (var3.getType() == Material.POTION) {
         PotionMeta var4 = (PotionMeta)var3.getItemMeta();
         PotionType var5 = MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R2) ? var4.getBasePotionType() : var4.getBasePotionData().getType();
         String var6 = var5 == null ? ActionRegistry.NONE_ROOT : var5.name();
         this.executionBuilder("consume").player(var2).root("potion").subRoot("type", var6).subRoot(var3).progressSingle().buildAndExecute();
      } else {
         this.executionBuilder("consume").player(var2).root(var3).progressSingle().buildAndExecute();
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onInteract(PlayerInteractEvent var1) {
      Player var2 = var1.getPlayer();
      Block var3 = var1.getClickedBlock();
      if (var3 != null && var3.getType().name().equalsIgnoreCase("CAKE") && var2.getFoodLevel() < 20 && var1.getAction() == Action.RIGHT_CLICK_BLOCK) {
         this.executionBuilder("consume").player(var2).root(var3).progressSingle().buildAndExecute();
      }
   }
}
