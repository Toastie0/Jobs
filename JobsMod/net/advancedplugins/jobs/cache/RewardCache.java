package net.advancedplugins.jobs.cache;

import com.google.common.collect.HashMultiset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.objects.reward.CommandReward;
import net.advancedplugins.jobs.objects.reward.ItemReward;
import net.advancedplugins.jobs.objects.reward.MoneyReward;
import net.advancedplugins.jobs.objects.reward.Reward;
import net.advancedplugins.jobs.objects.reward.enums.RewardType;
import net.advancedplugins.simplespigot.cache.SimpleCache;
import net.advancedplugins.simplespigot.config.Config;
import net.advancedplugins.simplespigot.config.ConfigLoader;
import org.bukkit.Bukkit;

public class RewardCache extends SimpleCache<String, Reward<?>> {
   private final Core plugin;

   public RewardCache(Core var1) {
      this.plugin = var1;
   }

   public void cache() {
      Config var1 = this.plugin.getConfig("rewards");
      ConfigLoader.reader(var1)
         .readWrap(
            var1x -> var1x.keyLoop(
               var2 -> {
                  String var3 = var1x.string("type");
                  RewardType var4 = RewardType.getEnum(var3);
                  if (var4 == null) {
                     Bukkit.getLogger()
                        .severe("Reward type for reward (" + var2 + ") could not be identified. Type must be ITEM or COMMAND, but the set type is: " + var3);
                  } else {
                     String var5 = var1x.string("name");
                     HashMap var6 = new HashMap();
                     if (var1x.has("variables")) {
                        var1x.keyLoop(var2.concat(".variables"), var2x -> {
                           List var3x = var1x.list();
                           if (var3x.isEmpty()) {
                              var6.put(var2x, var1x.string());
                           } else {
                              var6.put(var2x, String.join("", var3x));
                           }
                        });
                     }

                     var1x.setCurrentPath(var2);
                     switch (var4) {
                        case COMMAND:
                           this.set(var2, new CommandReward(var2, var5, var6, HashMultiset.create(var1x.list("commands"))));
                           break;
                        case ITEM:
                           HashMultiset var7 = HashMultiset.create();
                           var1x.keyLoop(var2.concat(".items"), var2x -> var7.add(new SimpleEntry<>(var1x.getItem("", null, true), var1x.string("amount"))));
                           this.set(var2, new ItemReward(var2, var5, var6, var7));
                           break;
                        case MONEY:
                           if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                              this.plugin.getLogger().warning("To use 'money' reward, you need to install Vault");
                           } else {
                              this.set(var2, new MoneyReward(var2, var5, var6, HashMultiset.create(Arrays.asList(var1x.string("value")))));
                           }
                     }
                  }
               }
            )
         );
   }
}
