package net.advancedplugins.jobs.objects.reward;

import com.google.common.collect.Multiset;
import java.util.Map;
import net.advancedplugins.jobs.impl.utils.text.Replacer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandReward extends Reward<String> {
   public CommandReward(String var1, String var2, Map<String, String> var3, Multiset<String> var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public void reward(Player var1, String var2, int var3, String var4, double var5) {
      for (String var8 : this.set) {
         try {
            Bukkit.dispatchCommand(
               Bukkit.getConsoleSender(),
               Replacer.to(
                  this.fillVariables(var8, var3, var1, var4, var5),
                  var5x -> var5x.set("player", var1.getName()).set("level", var3).set("job_name", var2).set("progress", var5)
               )
            );
         } catch (Exception var10) {
            Bukkit.getLogger().warning("Failed to execute AdvancedJob command reward: " + var8);
            var10.printStackTrace();
         }
      }
   }
}
