package net.advancedplugins.jobs.impl.utils.commands.argument;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import org.bukkit.command.CommandSender;

public class Argument<T> {
   private final ArgumentType<T> type;
   private final String argument;
   private final Set<String> aliases;
   private final Function<CommandSender, Collection<String>> onTabComplete;
   private boolean optional;

   public Argument(ArgumentType<T> var1, String var2, Function<CommandSender, Collection<String>> var3, String... var4) {
      this.type = var1;
      this.argument = var2;
      this.aliases = Sets.newHashSet(var4);
      this.onTabComplete = var3;
   }

   public Argument(ArgumentType<T> var1, String var2, String... var3) {
      this(var1, var2, var1x -> Lists.newArrayList(new String[]{var2}), var3);
   }

   public Argument asOptional() {
      this.optional = true;
      return this;
   }

   public ArgumentType<T> getType() {
      return this.type;
   }

   public String getArgument() {
      return this.argument;
   }

   public Set<String> getAliases() {
      return this.aliases;
   }

   public Function<CommandSender, Collection<String>> getOnTabComplete() {
      return this.onTabComplete;
   }

   public boolean isOptional() {
      return this.optional;
   }
}
