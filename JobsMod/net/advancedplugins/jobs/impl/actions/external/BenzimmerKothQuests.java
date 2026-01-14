package net.advancedplugins.jobs.impl.actions.external;

import com.benzimmer123.koth.api.events.KothLoseCapEvent;
import com.benzimmer123.koth.api.events.KothWinEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class BenzimmerKothQuests extends ActionQuestExecutor {
   public BenzimmerKothQuests(JavaPlugin var1) {
      super(var1, "koth");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onKothCapture(KothWinEvent var1) {
      Player var2 = var1.getCapper();
      int var3 = var1.getCaptureTime();
      String var4 = var1.getKOTH().getName(false);
      this.execute("capture", var2, var3, var1x -> var1x.root(var4), var1x -> var1x.set("koth_name", var4));
      this.execute("win_cap", var2, var1x -> var1x.root(var4), var1x -> var1x.set("koth_name", var4));
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onKothEnd(KothLoseCapEvent var1) {
      Player var2 = var1.getCapper();
      int var3 = var1.getCaptureTime();
      String var4 = var1.getKOTH().getName(false);
      this.execute("capture", var2, var3, var1x -> var1x.root(var4), var1x -> var1x.set("koth_name", var4));
   }
}
