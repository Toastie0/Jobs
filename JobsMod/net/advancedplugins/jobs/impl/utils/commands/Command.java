package net.advancedplugins.jobs.impl.utils.commands;

import java.util.function.Function;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public abstract class Command<T extends CommandSender> {
   protected final JavaPlugin plugin;
   private final String permission;
   private final boolean isConsole;
   private Function<T, String> noPermission = var0 -> "&cYou do not have permission to do this.";
   private Function<T, String> notOnline = var0 -> "&cPlayer is not online.";

   public Command(JavaPlugin var1, String var2, boolean var3) {
      this.plugin = var1;
      this.permission = var2;
      this.isConsole = var3;
   }

   public abstract void onExecute(T var1, String[] var2);

   public void middleMan(CommandSender var1, String[] var2) {
      this.onExecute((T)var1, var2);
   }

   @Nullable
   public String getPermission() {
      return this.permission;
   }

   public boolean isConsole() {
      return this.isConsole;
   }

   public void noPermissionLang(Function<T, String> var1) {
      this.noPermission = var1;
   }

   public void setNotOnlineLang(Function<T, String> var1) {
      this.notOnline = var1;
   }

   public String getNoPermissionLang(CommandSender var1) {
      return this.noPermission.apply((T)var1);
   }

   public String getNotOnlineLang(CommandSender var1) {
      return this.notOnline.apply((T)var1);
   }

   public boolean isPlayerOnline(CommandSender var1, Player var2) {
      if (var2 != null && var2.isOnline()) {
         return true;
      } else {
         var1.sendMessage(this.notOnline.apply((T)var1));
         return false;
      }
   }
}
