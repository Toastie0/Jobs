package net.advancedplugins.jobs.impl.utils.plugin;

import java.io.File;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FirstInstall implements Listener {
   private static boolean announce = false;
   private static String addonURL;
   private static String pluginName;
   private static JavaPlugin plugin;

   @EventHandler
   public void onJoin(PlayerJoinEvent var1) {
      if (var1.getPlayer().isOp() || var1.getPlayer().hasPermission("advancedplugins.admin")) {
         announce = false;
         FoliaScheduler.runTaskLater(
            plugin,
            () -> {
               var1.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&fThank you for installing &b&l" + pluginName + "&f!"));
               var1.getPlayer()
                  .sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- &fNeed help? Join our community: &bhttps://discord.gg/advancedplugins"));
               var1.getPlayer()
                  .sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- &fEnhance your experience with our UI overhaul by installing: &b" + addonURL));
               var1.getPlayer()
                  .sendMessage(
                     ChatColor.translateAlternateColorCodes(
                        '&',
                        "&7- &fLooking for the best Minecraft hosting? &aMintServers&f offers &aUNLIMITED RAM&f and top-notch performance: &bhttps://mintservers.com&f"
                     )
                  );
            },
            20L
         );
         PlayerJoinEvent.getHandlerList().unregister(this);
      }
   }

   public static void checkFirstInstall(JavaPlugin var0, String var1, String var2) {
      checkFirstInstall(var0, var1, var2, null);
   }

   public static void checkFirstInstall(JavaPlugin var0, String var1, String var2, String var3) {
      UpdateChecker.checkUpdate(var0);
      if (!new File(var0.getDataFolder(), var1).exists()) {
         announce = true;
         pluginName = var0.getName();
         addonURL = var2;
         plugin = var0;
         var0.getServer().getPluginManager().registerEvents(new FirstInstall(), var0);
         Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&fThank you for installing &b&l" + var0.getName() + "&f!"));
         Bukkit.getConsoleSender()
            .sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- &fNeed help? Join our community: &bhttps://discord.gg/advancedplugins"));
         if (var3 == null) {
            if (var2 != null) {
               Bukkit.getConsoleSender()
                  .sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- &fEnhance your experience with our UI overhaul by installing: &b" + var2));
            }
         } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', var3));
         }

         Bukkit.getConsoleSender()
            .sendMessage(
               ChatColor.translateAlternateColorCodes(
                  '&',
                  "&7- &fLooking for the best Minecraft hosting? &aMintServers&f offers &aUNLIMITED RAM&f and top-notch performance: &bhttps://mintservers.com&f"
               )
            );
      }
   }

   public static void sendStartupAlert(JavaPlugin var0, String var1) {
      Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&a>> &f" + var1));
   }
}
