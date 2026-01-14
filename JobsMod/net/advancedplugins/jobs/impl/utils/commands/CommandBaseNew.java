package net.advancedplugins.jobs.impl.utils.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.advancedplugins.jobs.impl.utils.commands.argument.ArgumentHandler;
import net.advancedplugins.jobs.impl.utils.commands.argument.ArgumentType;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandBaseNew {
   private final JavaPlugin plugin;
   private final Set<ConfigCommand<? extends CommandSender>> commands = Sets.newHashSet();
   private static final Class<? extends Server> bukkitServerClass = Bukkit.getServer().getClass();

   public CommandBaseNew(JavaPlugin var1) {
      this.plugin = var1;
      this.registerArgumentTypes();
   }

   public void registerCommand(ConfigCommand<? extends CommandSender> var1) {
      if (var1.getConfig().isEnabled()) {
         try {
            Field var2 = bukkitServerClass.getDeclaredField("commandMap");
            var2.setAccessible(true);
            CommandMap var3 = (CommandMap)var2.get(Bukkit.getServer());
            org.bukkit.command.Command var4 = new org.bukkit.command.Command(var1.getConfig().getCommand()) {
               public boolean execute(@NotNull CommandSender var1, @NotNull String var2x, @NotNull String[] var3x) {
                  return CommandBaseNew.this.onCommand(var1, this, var2x, var3x);
               }

               @NotNull
               public List<String> tabComplete(@NotNull CommandSender var1, @NotNull String var2x, @NotNull String[] var3x, @Nullable Location var4x) {
                  return CommandBaseNew.this.onTabComplete(var1, this, var2x, var3x);
               }
            };
            var4.setAliases(var1.getConfig().getAliases());
            if (var1.getConfig().getDescription() != null) {
               var4.setDescription(var1.getConfig().getDescription());
            }

            var4.setPermission(var1.getConfig().getPermission());
            var3.register(var1.getConfig().getCommand(), var4);
            var2.setAccessible(false);
         } catch (IllegalAccessException | NoSuchFieldException var5) {
            throw new RuntimeException(var5);
         }

         this.commands.add(var1);
      }
   }

   public CommandBaseNew registerArgumentType(Class<?> var1, ArgumentType<?> var2) {
      ArgumentHandler.register(var1, var2);
      return this;
   }

   public synchronized boolean onCommand(@NotNull CommandSender var1, org.bukkit.command.Command var2, @NotNull String var3, String[] var4) {
      String var5 = var2.getName();

      for (ConfigCommand var7 : this.commands) {
         if (var7.getConfig().getCommand().equalsIgnoreCase(var5)) {
            if (var7.getConfig().getPermission() != null
               && !var7.getConfig().getPermission().isEmpty()
               && !var1.hasPermission(var7.getConfig().getPermission())) {
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

            ConfigSubCommand var8 = null;

            for (ConfigSubCommand var10 : var7.getSubCommands()) {
               if (var4.length > var10.getArgumentsSize() && var10.isEndless()
                  || var10.getArgumentsSize() <= var4.length && var10.isMatch(var4)
                  || var4.length == var10.getArgumentsSizeReal() && var10.isMatch(var4)) {
                  var8 = var10;
                  break;
               }
            }

            if (var8 == null) {
               var7.middleMan(var1, var4);
               return true;
            }

            if (var8.getConfig().getPermission() != null
               && !var1.hasPermission(var8.getConfig().getPermission())
               && var7.getConfig().getPermission() != null
               && !var7.getConfig().getPermission().isEmpty()) {
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

   public List<String> onTabComplete(@NotNull CommandSender var1, org.bukkit.command.Command var2, @NotNull String var3, String[] var4) {
      ArrayList var5 = Lists.newArrayList();
      String var6 = var2.getName();

      for (ConfigCommand var8 : this.commands) {
         if (var8.getConfig().getCommand().equalsIgnoreCase(var6)
            && (var8.getConfig().getPermission() == null || var8.getConfig().getPermission().isEmpty() || var1.hasPermission(var8.getConfig().getPermission()))
            && (var8.isConsole() || !(var1 instanceof ConsoleCommandSender))
            && var4.length != 0) {
            if (var8.getSubCommands().isEmpty()) {
               var5.addAll(var8.tabCompletionSuggestion(var1, var4.length - 1));
            } else {
               HashSet var9 = Sets.newHashSet();

               for (ConfigSubCommand var11 : var8.getSubCommands()) {
                  if (var11.isMatchUntilIndex(var4, var4.length - 1)) {
                     var9.add(var11);
                  }
               }

               if (!var9.isEmpty()) {
                  for (ConfigSubCommand var14 : var9) {
                     if ((
                           var14.getConfig().getPermission() == null
                              || var1.hasPermission(var14.getConfig().getPermission())
                              || var8.getConfig().getPermission() == null
                              || var8.getConfig().getPermission().isEmpty()
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

   public Set<ConfigCommand<? extends CommandSender>> getCommands() {
      return this.commands;
   }
}
