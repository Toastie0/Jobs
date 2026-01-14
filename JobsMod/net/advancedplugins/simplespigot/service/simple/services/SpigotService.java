package net.advancedplugins.simplespigot.service.simple.services;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpigotService {
   public void giveItem(Player var1, Collection<ItemStack> var2) {
      HashMap var3 = var1.getInventory().addItem(var2.toArray(new ItemStack[0]));
      if (!var3.isEmpty()) {
         Location var4 = var1.getLocation();
         World var5 = var4.getWorld();

         for (ItemStack var7 : var3.values()) {
            var5.dropItemNaturally(var4, var7);
         }
      }
   }

   public void giveItem(Player var1, ItemStack var2) {
      this.giveItem(var1, Sets.newHashSet(new ItemStack[]{var2}));
   }

   public boolean isPluginEnabledByAuthor(String var1, String var2) {
      return Bukkit.getPluginManager().isPluginEnabled(var1) ? Bukkit.getPluginManager().getPlugin(var1).getDescription().getAuthors().contains(var2) : false;
   }

   public boolean isPluginEnabled(String var1, String var2) {
      if (Bukkit.getPluginManager().isPluginEnabled(var1)) {
         try {
            Class.forName(var2);
            return true;
         } catch (ClassNotFoundException var4) {
            return false;
         }
      } else {
         return false;
      }
   }
}
