package net.advancedplugins.jobs.impl.utils.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import net.advancedplugins.jobs.impl.utils.commands.argument.ArgumentHandler;
import net.advancedplugins.jobs.impl.utils.commands.argument.ArgumentType;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

public class CommandBase implements CommandExecutor, TabCompleter {
   private final JavaPlugin plugin;
   private Set<SimpleCommand<? extends CommandSender>> commands = Sets.newHashSet();

   public CommandBase(JavaPlugin var1) {
      this.plugin = var1;
      this.registerArgumentTypes();
   }

   public void registerCommand(SimpleCommand<? super CommandSender> var1) {
      PluginCommand var2 = this.plugin.getCommand(var1.getCommand());
      if (var2 == null) {
         Bukkit.getLogger().log(Level.WARNING, "Failed to load the command " + var1.getCommand());
      } else {
         var2.setExecutor(this);
         this.commands.add(var1);
      }
   }

   public void registerCommandOverride(SimpleCommand<? super CommandSender> var1) {
      try {
         Field var2 = Bukkit.getServer().getClass().getDeclaredField("commandMap");
         var2.setAccessible(true);
         CommandMap var3 = (CommandMap)var2.get(Bukkit.getServer());
         var3.getCommand(var1.getCommand()).unregister(var3);
      } catch (Exception var4) {
      }

      PluginCommand var5 = this.plugin.getCommand(var1.getCommand());
      if (var5 == null) {
         Bukkit.getLogger().log(Level.WARNING, "Failed to load the command " + var1.getCommand());
      } else {
         var5.setExecutor(this);
         this.commands.add(var1);
      }
   }

   public void registerCommand(SimpleCommand<? super CommandSender> var1, List<String> var2) {
      PluginCommand var3 = this.plugin.getCommand(var1.getCommand());
      if (var3 == null) {
         Bukkit.getLogger().log(Level.WARNING, "Failed to load the command " + var1.getCommand());
      } else {
         var3.setAliases(var2);
         var3.setExecutor(this);
         this.commands.add(var1);
      }
   }

   public CommandBase registerArgumentType(Class<?> var1, ArgumentType<?> var2) {
      ArgumentHandler.register(var1, var2);
      return this;
   }

   public synchronized boolean onCommand(CommandSender var1, org.bukkit.command.Command var2, String var3, String[] var4) {
      String var5 = var2.getName();

      for (SimpleCommand var7 : this.commands) {
         if (var7.getCommand().equalsIgnoreCase(var5)) {
            if (var7.getPermission() != null && !var7.getPermission().isEmpty() && !var1.hasPermission(var7.getPermission())) {
               Text.sendMessage(var1, var7.getNoPermissionLang(var1));
               return true;
            }

            if (!var7.isConsole() && var1 instanceof ConsoleCommandSender) {
               var1.sendMessage("The console can not execute this command.");
               return true;
            }

            if (var7.getSubCommands().isEmpty() && var7.getArgumentsSize() > var4.length) {
               var7.sendUsage(var1);
               return true;
            }

            if (var4.length == 0) {
               var7.middleMan(var1, var4);
               return true;
            }

            SubCommand var8 = null;

            for (SubCommand var10 : var7.getSubCommands()) {
               if (var4.length > var10.getArgumentsSize() && var10.isEndless() || var10.getArgumentsSize() <= var4.length && var10.isMatch(var4)) {
                  var8 = var10;
                  break;
               }
            }

            if (var8 == null) {
               var7.middleMan(var1, var4);
               return true;
            }

            if (!var8.doesInheritPermission()
               && var8.getPermission() != null
               && !var1.hasPermission(var8.getPermission())
               && var7.getPermission() != null
               && !var7.getPermission().isEmpty()) {
               Text.sendMessage(var1, var8.getNoPermissionLang(var1));
               return true;
            }

            if (!var8.isConsole() && var1 instanceof ConsoleCommandSender) {
               var1.sendMessage("The console can not execute this command.");
               return true;
            }

            var8.middleMan(var1, var4);
         }
      }

      return true;
   }

   public List<String> onTabComplete(CommandSender var1, org.bukkit.command.Command var2, String var3, String[] var4) {
      ArrayList var5 = Lists.newArrayList();
      String var6 = var2.getName();

      for (SimpleCommand var8 : this.commands) {
         if (var8.getCommand().equalsIgnoreCase(var6)
            && (var8.getPermission() == null || var8.getPermission().isEmpty() || var1.hasPermission(var8.getPermission()))
            && (var8.isConsole() || !(var1 instanceof ConsoleCommandSender))
            && var4.length != 0) {
            if (var8.getSubCommands().isEmpty()) {
               var5.addAll(var8.tabCompletionSuggestion(var1, var4.length - 1));
            } else {
               HashSet var9 = Sets.newHashSet();

               for (SubCommand var11 : var8.getSubCommands()) {
                  if (var11.isMatchUntilIndex(var4, var4.length - 1)) {
                     var9.add(var11);
                  }
               }

               if (!var9.isEmpty()) {
                  for (SubCommand var14 : var9) {
                     if ((
                           var14.doesInheritPermission()
                              || var14.getPermission() == null
                              || var1.hasPermission(var14.getPermission())
                              || var8.getPermission() == null
                              || var8.getPermission().isEmpty()
                        )
                        && (var14.isConsole() || !(var1 instanceof ConsoleCommandSender))) {
                        var5.addAll(var14.tabCompletionSuggestion(var1, var4.length - 1));
                     }
                  }
               }
            }
         }
      }

      ArrayList var12 = new ArrayList();
      StringUtil.copyPartialMatches(var4[var4.length - 1], var5, var12);
      Collections.sort(var12);
      return var12;
   }

   private void registerArgumentTypes() {
      this.registerArgumentType(String.class, var0 -> var0)
         .registerArgumentType(Player.class, Bukkit::getPlayerExact)
         .registerArgumentType(OfflinePlayer.class, Bukkit::getOfflinePlayer)
         .registerArgumentType(Integer.class, var0 -> NumberUtils.isNumber(var0) ? Integer.parseInt(var0) : 0)
         .registerArgumentType(Boolean.class, var0 -> var0.equalsIgnoreCase("true") || var0.equalsIgnoreCase("false") ? false : null);
   }

   public Set<SimpleCommand<? extends CommandSender>> getCommands() {
      return this.commands;
   }
}
