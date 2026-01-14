package net.advancedplugins.jobs.impl.utils.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharSource;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.impl.utils.DataHandler;
import net.advancedplugins.jobs.impl.utils.commands.argument.Argument;
import net.advancedplugins.jobs.impl.utils.commands.argument.ArgumentHandler;
import net.advancedplugins.jobs.impl.utils.commands.argument.ConfigArgument;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ConfigCommand<T extends CommandSender> {
   private static final int COMMANDS_PER_PAGE = 9;
   private Integer pageCount;
   private LinkedList<ConfigSubCommand<? extends CommandSender>> subCommands = new LinkedList<>();
   private List<Argument<?>> arguments = new ArrayList<>();
   private final LinkedList<ConfigCommand.ShowcaseCommand> showcaseCommands = new LinkedList<>();
   private final ConfigCommand.Config config;
   protected final JavaPlugin plugin;
   private final boolean console;
   private Function<T, String> noPermission = var0 -> Text.modify("&cYou do not have permission to do this.");
   private Function<T, String> notOnline = var0 -> Text.modify("&cPlayer is not online.");

   public ConfigCommand(JavaPlugin var1, ConfigCommand.Config var2, boolean var3) {
      this.config = var2;
      this.plugin = var1;
      this.console = var3;
   }

   public static ConfigCommand.Config getConfig(DataHandler var0, String var1) {
      try {
         if (!var0.isPath(var1)) {
            InputStream var2 = var0.getInstance().getResource(var0.getFileName() + ".yml");
            byte[] var3 = ByteStreams.toByteArray(var2);
            Reader var4 = CharSource.wrap(new String(var3)).openStream();
            YamlConfiguration var5 = YamlConfiguration.loadConfiguration(var4);
            if (!var5.contains(var1)) {
               throw new IllegalArgumentException("Command " + var1 + " not found in default config.");
            }

            var0.getConfig().set(var1 + ".enabled", var5.getBoolean(var1 + ".enabled", true));
            var0.getConfig().set(var1 + ".command", var5.getString(var1 + ".command"));
            var0.getConfig().set(var1 + ".permission", var5.getString(var1 + ".permission"));
            var0.getConfig().set(var1 + ".description", var5.getString(var1 + ".description"));
            var0.getConfig().set(var1 + ".aliases", var5.getStringList(var1 + ".aliases"));
            var0.save();
            var0.getInstance().getLogger().info("Added command/subcommand " + var1 + " to config " + var0.getFileName() + ".yml");
            var0.reloadConfig();
            var4.close();
         }

         return new ConfigCommand.Config(
            var0.getBoolean(var1 + ".enabled", true),
            var0.getString(var1 + ".command"),
            var0.getString(var1 + ".permission"),
            var0.getString(var1 + ".description"),
            var0.getStringList(var1 + ".aliases")
         );
      } catch (Throwable var6) {
         throw var6;
      }
   }

   public static ConfigCommand.ArgConfig getArgConfig(DataHandler var0, String var1, String var2) {
      try {
         if (var0.getString(var1 + ".args." + var2) == null) {
            InputStream var3 = var0.getInstance().getResource(var0.getFileName() + ".yml");
            byte[] var4 = ByteStreams.toByteArray(var3);
            Reader var5 = CharSource.wrap(new String(var4)).openStream();
            YamlConfiguration var6 = YamlConfiguration.loadConfiguration(var5);
            if (!var6.contains(var1)) {
               throw new IllegalArgumentException("Command " + var1 + " not found in default config.");
            }

            var0.getConfig().set(var1 + ".args." + var2, var2);
            var0.save();
            var0.reloadConfig();
            var5.close();
         }

         return new ConfigCommand.ArgConfig(var2, var0.getString(var1 + ".args." + var2));
      } catch (Throwable var7) {
         throw var7;
      }
   }

   public abstract void onExecute(T var1, String[] var2);

   public void middleMan(CommandSender var1, String[] var2) {
      this.onExecute((T)var1, var2);
   }

   public void addShowcaseCommand(String var1, String var2) {
      this.showcaseCommands.add(new ConfigCommand.ShowcaseCommand(var1, var2));
   }

   protected void setSubCommands(ConfigSubCommand<? extends CommandSender>... var1) {
      this.subCommands = new LinkedList<>(Arrays.asList(var1));
   }

   public void sendHelpMessage(Plugin var1, CommandSender var2) {
      PluginDescriptionFile var3 = var1.getDescription();
      Text.sendMessage(var2, "&f".concat(var3.getName()).concat(" &7v").concat(var3.getVersion()));
      Text.sendMessage(var2, "&7Use &f&n".concat(this.config.getCommand()).concat(" to view usage information."));
   }

   public void sendHelpPage(CommandSender var1, String var2, String[] var3) {
      int var4 = (var3.length == 0 ? 0 : (StringUtils.isNumeric(var3[0]) ? Math.max(0, Integer.parseInt(var3[0])) : 1)) - 1;
      int var5 = this.subCommands.size();
      if (this.pageCount == null) {
         this.pageCount = (int)Math.ceil(var5 / 9.0F);
      }

      var4 = Math.min(Math.max(0, var4), this.pageCount);
      if (var4 + 1 > this.pageCount) {
         var4 = this.pageCount - 1;
      }

      PluginDescriptionFile var6 = this.plugin.getDescription();
      Text.sendMessage(
         var1, var2 + "[<] &8+-------< " + var2 + "&l" + var6.getName().concat(" &7Page " + (var4 + 1) + "/" + this.pageCount) + " &8>-------+ " + var2 + "[>]"
      );
      Text.sendMessage(var1, " ");
      if (var4 == 0 && !this.showcaseCommands.isEmpty()) {
         for (ConfigCommand.ShowcaseCommand var8 : this.showcaseCommands) {
            Text.sendMessage(var1, "  /" + var8.name + " &8-&e " + var8.description);
         }
      }

      for (ConfigSubCommand var11 : this.subCommands.subList(var4 * 9, Math.min(var5, (var4 + 1) * 9))) {
         Text.sendMessage(var1, "  " + var11.getFormatted(this.getConfig().getCommand()));
      }

      Text.sendMessage(var1, " ");
      Text.sendMessage(var1, "  &2<> &f- Required Arguments&7; &9[] &f- Optional Arguments");
      Text.sendMessage(var1, var2 + "[<] &8+-------< " + var2 + "&l" + var6.getName().concat(" &7v" + var6.getVersion() + " &8>-------+ " + var2 + "[>]"));
   }

   public void sendUsage(CommandSender var1) {
      Text.sendMessage(var1, Text.modify("&cUsage: " + this.getFormatted()));
   }

   public String getFormatted() {
      StringBuilder var1 = new StringBuilder().append("/").append(this.getConfig().getCommand()).append(" ");

      for (Argument var3 : this.arguments) {
         if (var3.getType() == null) {
            var1.append(var3.getArgument());
         } else if (var3.isOptional()) {
            var1.append("[").append(var3.getArgument()).append("]");
         } else {
            var1.append("<").append(var3.getArgument()).append(">");
         }

         var1.append(" ");
      }

      return var1.toString();
   }

   public void addFlat(String var1) {
      this.arguments.add(new Argument(null, var1));
   }

   public void addFlatWithAliases(String var1, List<String> var2) {
      this.addFlatWithAliases(var1, var2.toArray(new String[0]));
   }

   public void addFlatWithAliases(String var1, String... var2) {
      this.arguments.add(new Argument(null, var1, var2));
   }

   public void addFlats(String... var1) {
      for (String var5 : var1) {
         this.addFlat(var5);
      }
   }

   protected <S> Argument<S> addArgument(Class<S> var1, String var2, String... var3) {
      if (var2.equalsIgnoreCase("player")) {
         return this.addArgument(var1, var2, null, var3);
      } else {
         Argument var4 = new Argument(ArgumentHandler.getArgumentType(var1), var2, var3);
         this.arguments.add(var4);
         return var4;
      }
   }

   protected <S> Argument<S> addArgument(Class<S> var1, String var2, Function<CommandSender, Collection<String>> var3, String... var4) {
      if (var2.equalsIgnoreCase("player")) {
         var3 = var0 -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
      }

      Argument var5 = new Argument(ArgumentHandler.getArgumentType(var1), var2, var3, var4);
      this.arguments.add(var5);
      return var5;
   }

   protected <S> Argument<S> addArgument(Class<S> var1, ConfigCommand.ArgConfig var2, String... var3) {
      if (var2.getArg().equalsIgnoreCase("player")) {
         return this.addArgument(var1, var2, null, var3);
      } else {
         ConfigArgument var4 = new ConfigArgument(var2, ArgumentHandler.getArgumentType(var1), var3);
         this.arguments.add(var4);
         return var4;
      }
   }

   protected <S> Argument<S> addArgument(Class<S> var1, ConfigCommand.ArgConfig var2, Function<CommandSender, Collection<String>> var3, String... var4) {
      if (var2.getArg().equalsIgnoreCase("player")) {
         var3 = var0 -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
      }

      ConfigArgument var5 = new ConfigArgument(var2, ArgumentHandler.getArgumentType(var1), var3, var4);
      this.arguments.add(var5);
      return var5;
   }

   public int getArgumentsSize() {
      return (int)this.arguments.stream().filter(var0 -> !var0.isOptional()).count();
   }

   public int getArgumentsSizeReal() {
      return this.arguments.size();
   }

   public <U> U parseArgument(String[] var1, int var2) {
      return this.parseArgument(var1, var2, null);
   }

   public <U> U parseArgument(String[] var1, int var2, U var3) {
      String var4 = var1.length - 1 < var2 ? null : var1[var2];
      return (U)(var4 == null ? var3 : this.arguments.get(var2).getType().parse(var4));
   }

   public boolean isMatch(String[] var1) {
      return this.isMatchUntilIndex(var1, var1.length);
   }

   public String[] getEnd(String[] var1) {
      LinkedHashSet var2 = Sets.newLinkedHashSet();

      for (int var3 = 0; var3 < var1.length; var3++) {
         if (var3 >= this.arguments.size() - 1) {
            var2.add(var1[var3]);
         }
      }

      return var2.toArray(new String[0]);
   }

   public boolean isMatchUntilIndex(String[] var1, int var2) {
      for (int var3 = 0; var3 < var2; var3++) {
         if (!this.isArgumentValid(var1, var3)) {
            return false;
         }
      }

      return true;
   }

   public Collection<String> tabCompletionSuggestion(CommandSender var1, int var2) {
      return (Collection<String>)(var2 > this.arguments.size() - 1 ? Lists.newArrayList() : this.arguments.get(var2).getOnTabComplete().apply(var1));
   }

   public boolean isArgumentValid(String[] var1, int var2) {
      if (this.getArgumentsSizeReal() - 1 < var2) {
         return false;
      } else {
         Argument var3 = this.arguments.get(var2);
         if (var3.getType() == null) {
            String var4 = var1[var2];

            for (String var6 : var3.getAliases()) {
               if (var4.equalsIgnoreCase(var6)) {
                  return true;
               }
            }

            return var1[var2].equalsIgnoreCase(var3.getArgument());
         } else {
            return true;
         }
      }
   }

   public boolean isPlayerOnline(CommandSender var1, Player var2) {
      if (var2 != null && var2.isOnline()) {
         return true;
      } else {
         var1.sendMessage(this.notOnline.apply((T)var1));
         return false;
      }
   }

   public String getNoPermissionLang(CommandSender var1) {
      return this.noPermission.apply((T)var1);
   }

   public String getNotOnlineLang(CommandSender var1) {
      return this.notOnline.apply((T)var1);
   }

   public Integer getPageCount() {
      return this.pageCount;
   }

   public LinkedList<ConfigSubCommand<? extends CommandSender>> getSubCommands() {
      return this.subCommands;
   }

   public List<Argument<?>> getArguments() {
      return this.arguments;
   }

   public LinkedList<ConfigCommand.ShowcaseCommand> getShowcaseCommands() {
      return this.showcaseCommands;
   }

   public ConfigCommand.Config getConfig() {
      return this.config;
   }

   public JavaPlugin getPlugin() {
      return this.plugin;
   }

   public boolean isConsole() {
      return this.console;
   }

   public Function<T, String> getNoPermission() {
      return this.noPermission;
   }

   public Function<T, String> getNotOnline() {
      return this.notOnline;
   }

   public void setArguments(List<Argument<?>> var1) {
      this.arguments = var1;
   }

   public void setNoPermission(Function<T, String> var1) {
      this.noPermission = var1;
   }

   public void setNotOnline(Function<T, String> var1) {
      this.notOnline = var1;
   }

   public static class ArgConfig {
      private final String arg;
      private final String displayArg;

      public String getArg() {
         return this.arg;
      }

      public String getDisplayArg() {
         return this.displayArg;
      }

      public ArgConfig(String var1, String var2) {
         this.arg = var1;
         this.displayArg = var2;
      }
   }

   public static class Config {
      private final boolean enabled;
      private final String command;
      private final String permission;
      private final String description;
      private final List<String> aliases;

      public boolean isEnabled() {
         return this.enabled;
      }

      public String getCommand() {
         return this.command;
      }

      public String getPermission() {
         return this.permission;
      }

      public String getDescription() {
         return this.description;
      }

      public List<String> getAliases() {
         return this.aliases;
      }

      public Config(boolean var1, String var2, String var3, String var4, List<String> var5) {
         this.enabled = var1;
         this.command = var2;
         this.permission = var3;
         this.description = var4;
         this.aliases = var5;
      }
   }

   static class ShowcaseCommand {
      private String name;
      private String description;

      public ShowcaseCommand(String var1, String var2) {
         this.name = var1;
         this.description = var2;
      }
   }
}
