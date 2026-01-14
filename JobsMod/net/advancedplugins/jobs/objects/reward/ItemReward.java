package net.advancedplugins.jobs.objects.reward;

import com.google.common.collect.Multiset;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.simplespigot.service.simple.Simple;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemReward extends Reward<Entry<ItemStack, String>> {
   public ItemReward(String var1, String var2, Map<String, String> var3, Multiset<Entry<ItemStack, String>> var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public void reward(Player var1, String var2, int var3, String var4, double var5) {
      ArrayList var7 = new ArrayList();
      this.set.forEach(var7x -> {
         ItemStack var8 = ((ItemStack)var7x.getKey()).clone();
         var8.setAmount((int)Math.round(ASManager.parseThroughCalculator(this.fillVariables((String)var7x.getValue(), var3, var1, var4, var5))));
         var7.add(var8);
      });
      Simple.spigot().giveItem(var1, var7);
   }
}
