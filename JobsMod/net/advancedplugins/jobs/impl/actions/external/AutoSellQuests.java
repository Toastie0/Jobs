package net.advancedplugins.jobs.impl.actions.external;

import me.clip.autosell.events.AutoSellEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoSellQuests extends ActionQuestExecutor {
   public AutoSellQuests(JavaPlugin var1) {
      super(var1, "autosell");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onBlockBreak(AutoSellEvent var1) {
      Player var2 = var1.getPlayer();
      Block var3 = var1.getBlock();
      super.execute("break", var2, var1x -> var1x.root(var3), var1x -> var1x.set("block", var3.getType()));
   }
}
