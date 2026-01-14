package net.advancedplugins.jobs.menus.menus;

import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.text.Text;
import net.advancedplugins.jobs.menus.UserDependent;
import net.advancedplugins.jobs.menus.service.extensions.ConfigMenu;
import net.advancedplugins.jobs.objects.users.User;
import net.advancedplugins.simplespigot.config.Config;
import net.advancedplugins.simplespigot.item.SpigotItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LeaderboardMenu extends ConfigMenu implements UserDependent {
   private final User user;
   private final Config config;
   private final int[] slots;

   public LeaderboardMenu(Core var1, Config var2, Player var3) {
      super(var1, var2, var3);
      this.config = var2;
      this.user = var1.getUserCache().getOrThrow(var3.getUniqueId());
      this.slots = ASManager.getSlots(var2.string("leaderboard.slots"));
   }

   @Override
   public void redraw() {
      if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
         Bukkit.getLogger().warning("To enable leaderboard, please install PlaceholderAPI");
         this.player.sendMessage(Text.modify("Leaderboard is not supported on this server - install PlaceholderAPI!"));
      } else {
         int var1 = 1;
         this.drawConfigItems(var1x -> var1x.tryAddPapi(this.player));

         for (int var5 : this.slots) {
            int var6 = var1;
            ItemStack var7 = SpigotItem.toItem(this.config, "item", var2 -> var2.set("position", var6 + "").tryAddPapi(this.player));
            this.getInventory().setItem(var5, var7);
            var1++;
         }
      }
   }

   @Override
   public boolean isUserViable() {
      return this.user != null;
   }
}
