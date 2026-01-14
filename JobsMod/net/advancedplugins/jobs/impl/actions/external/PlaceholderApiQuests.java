package net.advancedplugins.jobs.impl.actions.external;

import com.google.common.collect.Sets;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;
import me.clip.placeholderapi.PlaceholderAPI;
import net.advancedplugins.jobs.impl.actions.external.executor.ActionQuestExecutor;
import net.advancedplugins.jobs.impl.actions.objects.variable.ActionResult;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlaceholderApiQuests extends ActionQuestExecutor {
   private JavaPlugin plugin;
   private final Set<String> integerPlaceholders = Sets.newHashSet();
   private final Set<String> matchPlaceholders = Sets.newHashSet();

   public PlaceholderApiQuests(JavaPlugin var1, Set<String> var2) {
      super(var1, "placeholderapi");
      this.plugin = var1;

      for (String var4 : var2) {
         String var5 = var4.substring(15);
         if (var5.startsWith("integer_")) {
            String var6 = var5.substring(8);
            ASManager.debug("(PlaceholderAPI Quests) Adding placeholderapi integer quest with placeholder ".concat(var6));
            this.integerPlaceholders.add(var6);
         } else if (var5.startsWith("match_")) {
            String var7 = var5.substring(6);
            ASManager.debug("(PlaceholderAPI Quests) Adding placeholderapi match quest with placeholder ".concat(var7));
            this.matchPlaceholders.add(var7);
         } else {
            var1.getLogger().warning("Failed to parse PlaceholderAPI quest variable: ".concat(var4));
         }
      }

      this.placeholderRun();
   }

   private void placeholderRun() {
      if (!this.matchPlaceholders.isEmpty() || !this.integerPlaceholders.isEmpty()) {
         FoliaScheduler.runTaskTimer(this.plugin, () -> {
            for (Player var2 : Bukkit.getOnlinePlayers()) {
               for (String var4 : this.matchPlaceholders) {
                  String var5 = PlaceholderAPI.setPlaceholders(var2, "%" + var4 + "%");
                  this.execute("match_".concat(var4), var2, var1 -> var1.root(var5));
               }

               for (String var11 : this.integerPlaceholders) {
                  String var12 = PlaceholderAPI.setPlaceholders(var2, "%" + var11 + "%");
                  String var6 = var12.replaceAll("[^\\d.]", "");
                  if (!var6.isEmpty()) {
                     BigInteger var7;
                     try {
                        var7 = new BigDecimal(var12).toBigInteger();
                     } catch (Exception var9) {
                        var7 = new BigInteger(var6);
                     }

                     this.execute("integer_".concat(var11), var2, var7, ActionResult::none, var1 -> var1.set("placeholder_value", var12), true);
                  }
               }
            }
         }, 40L, 40L);
      }
   }
}
