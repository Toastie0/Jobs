package net.advancedplugins.jobs.impl.actions.external;

import com.vk2gpz.tokenenchant.event.TEEnchantEvent;
import com.vk2gpz.tokenenchant.event.TETokenChangeEvent;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import net.advancedplugins.jobs.impl.actions.objects.variable.ActionResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class TokenEnchantQuests extends ActionQuestExecutor {
   public TokenEnchantQuests(JavaPlugin var1) {
      super(var1, "tokenenchant");
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onTokensChange(TETokenChangeEvent var1) {
      double var2 = var1.getNewTokenValue() - var1.getOldTokenValue();
      if (!(var2 < 1.0)) {
         Player var4 = var1.getOfflinePlayer().getPlayer();
         this.execute("gain", var4, (int)var2, ActionResult::none);
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onItemEnchant(TEEnchantEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getCEHandler().getDisplayName();
      int var4 = var1.getNewLevel() - var1.getOldLevel();
      this.execute("enchant", var2, var4, var1x -> var1x.root(var3));
   }
}
