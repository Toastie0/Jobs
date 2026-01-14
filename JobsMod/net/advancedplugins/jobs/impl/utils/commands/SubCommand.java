package net.advancedplugins.jobs.impl.utils.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.impl.utils.commands.argument.Argument;
import net.advancedplugins.jobs.impl.utils.commands.argument.ArgumentHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class SubCommand<T extends CommandSender> extends Command<T> {
   private final boolean endless;
   private List<Argument<?>> arguments = Lists.newArrayList();
   private boolean inheritPermission;
   private String description;

   public SubCommand(JavaPlugin var1, String var2, boolean var3) {
      this(var1, var2, var3, false);
   }

   public SubCommand(JavaPlugin var1, String var2, boolean var3, boolean var4) {
      super(var1, var2, var3);
      this.endless = var4;
   }

   public void setDescription(String var1) {
      this.description = var1;
   }

   public String getDescription() {
      return this.description;
   }

   public SubCommand(JavaPlugin var1) {
      this(var1, "", true);
   }

   public SubCommand(JavaPlugin var1, String var2) {
      this(var1, var2, true);
   }

   public SubCommand(JavaPlugin var1, boolean var2) {
      this(var1, "", var2);
   }

   protected void inheritPermission() {
      this.inheritPermission = true;
   }

   public boolean doesInheritPermission() {
      return this.inheritPermission;
   }

   public boolean isEndless() {
      return this.endless;
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

   public <U> U parseArgument(String[] var1, int var2, Supplier<U> var3) {
      String var4 = var1.length - 1 < var2 ? null : var1[var2];
      if (var4 == null) {
         return (U)(var3 == null ? null : var3.get());
      } else {
         return (U)this.arguments.get(var2).getType().parse(var4);
      }
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
      if (this.getArgumentsSizeReal() - 1 < var2) {
         return this.endless;
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

   public String getFormatted(String var1) {
      StringBuilder var2 = new StringBuilder().append("/").append(var1).append(" ");

      for (Argument var4 : this.arguments) {
         if (var4.getType() == null) {
            var2.append(var4.getArgument());
         } else if (var4.isOptional()) {
            var2.append("&9[").append(var4.getArgument()).append("]&r");
         } else {
            var2.append("&2<").append(var4.getArgument()).append(">&r");
         }

         var2.append(" ");
      }

      var2.append("&8-&e ");
      var2.append(this.getDescription());
      return var2.toString();
   }
}
