package net.advancedplugins.jobs.impl.utils.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.impl.utils.commands.argument.Argument;
import net.advancedplugins.jobs.impl.utils.commands.argument.ArgumentHandler;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class SimpleCommand<T extends CommandSender> extends Command<T> {
   private final String command;
   private Integer pageCount;
   private final int COMMANDS_PER_PAGE = 9;
   private LinkedList<SubCommand<? extends CommandSender>> subCommands = new LinkedList<>();
   private List<Argument<?>> arguments = new ArrayList<>();
   private LinkedList<SimpleCommand<T>.ShowcaseCommand> showcaseCommands = new LinkedList<>();

   public SimpleCommand(JavaPlugin var1, String var2, String var3, boolean var4) {
      super(var1, var3, var4);
      this.command = var2;
   }

   public SimpleCommand(JavaPlugin var1, String var2, boolean var3) {
      this(var1, var2, "", var3);
   }

   public SimpleCommand(JavaPlugin var1, String var2, String var3) {
      this(var1, var2, var3, true);
   }

   public SimpleCommand(JavaPlugin var1, String var2) {
      this(var1, var2, true);
   }

   public void setSubCommands(LinkedList<SubCommand<? extends CommandSender>> var1) {
      this.subCommands = var1;
   }

   public void addShowcaseCommand(String var1, String var2) {
      this.showcaseCommands.add(new SimpleCommand.ShowcaseCommand(var1, var2));
   }

   protected void setSubCommands(SubCommand<? extends CommandSender>... var1) {
      this.subCommands.addAll(Arrays.asList(var1));
   }

   public void sendHelpMessage(Plugin var1, CommandSender var2) {
      PluginDescriptionFile var3 = var1.getDescription();
      Text.sendMessage(var2, "&f".concat(var3.getName()).concat(" &7v").concat(var3.getVersion()));
      Text.sendMessage(var2, "&7Use &f&n".concat(this.command).concat(" to view usage information."));
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

      PluginDescriptionFile var6 = super.plugin.getDescription();
      Text.sendMessage(
         var1, var2 + "[<] &8+-------< " + var2 + "&l" + var6.getName().concat(" &7Page " + (var4 + 1) + "/" + this.pageCount) + " &8>-------+ " + var2 + "[>]"
      );
      Text.sendMessage(var1, " ");
      if (var4 == 0 && !this.showcaseCommands.isEmpty()) {
         for (SimpleCommand.ShowcaseCommand var8 : this.showcaseCommands) {
            Text.sendMessage(var1, "  /" + var8.name + " &8-&e " + var8.description);
         }
      }

      for (SubCommand var11 : this.subCommands.subList(var4 * 9, Math.min(var5, (var4 + 1) * 9))) {
         Text.sendMessage(var1, "  " + var11.getFormatted(this.command));
      }

      Text.sendMessage(var1, " ");
      Text.sendMessage(var1, "  &2<> &f- Required Arguments&7; &9[] &f- Optional Arguments");
      Text.sendMessage(var1, var2 + "[<] &8+-------< " + var2 + "&l" + var6.getName().concat(" &7v" + var6.getVersion() + " &8>-------+ " + var2 + "[>]"));
   }

   public void sendUsage(CommandSender var1) {
      Text.sendMessage(var1, Text.modify("&cUsage: " + this.getFormatted()));
   }

   public String getFormatted() {
      StringBuilder var1 = new StringBuilder().append("/").append(this.command).append(" ");

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

   public void setArguments(List<Argument<?>> var1) {
      this.arguments = var1;
   }

   public void addFlat(String var1) {
      this.arguments.add(new Argument(null, var1));
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

   private boolean isArgumentValid(String[] var1, int var2) {
      if (this.getArgumentsSize() - 1 < var2) {
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

   public String getCommand() {
      return this.command;
   }

   public LinkedList<SubCommand<? extends CommandSender>> getSubCommands() {
      return this.subCommands;
   }

   public List<Argument<?>> getArguments() {
      return this.arguments;
   }

   class ShowcaseCommand {
      private String name;
      private String description;

      public ShowcaseCommand(final String nullx, final String nullxx) {
         this.name = nullx;
         this.description = nullxx;
      }
   }
}
