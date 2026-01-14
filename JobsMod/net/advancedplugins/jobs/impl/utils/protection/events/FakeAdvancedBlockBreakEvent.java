package net.advancedplugins.jobs.impl.utils.protection.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class FakeAdvancedBlockBreakEvent extends BlockBreakEvent {
   private final Player player;
   private boolean dropItems;
   private boolean cancel;

   public FakeAdvancedBlockBreakEvent(@NotNull Block var1, @NotNull Player var2) {
      super(var1, var2);
      this.player = var2;
      this.dropItems = true;
   }

   @NotNull
   public Player getPlayer() {
      return this.player;
   }

   public void setDropItems(boolean var1) {
      this.dropItems = var1;
   }

   public boolean isDropItems() {
      return this.dropItems;
   }

   public boolean isCancelled() {
      return this.cancel;
   }

   public void setCancelled(boolean var1) {
      this.cancel = var1;
   }
}
