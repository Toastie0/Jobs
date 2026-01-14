package net.advancedplugins.jobs.impl.utils.commands.argument;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.function.Function;
import net.advancedplugins.jobs.impl.utils.commands.ConfigCommand;
import org.bukkit.command.CommandSender;

public class ConfigArgument<T> extends Argument<T> {
   private final ConfigCommand.ArgConfig argConfig;

   public ConfigArgument(ConfigCommand.ArgConfig var1, ArgumentType<T> var2, Function<CommandSender, Collection<String>> var3, String... var4) {
      super(var2, var1.getDisplayArg(), var3, var4);
      this.argConfig = var1;
   }

   public ConfigArgument(ConfigCommand.ArgConfig var1, ArgumentType<T> var2, String... var3) {
      this(var1, var2, var1x -> Lists.newArrayList(new String[]{var1.getDisplayArg()}), var3);
   }

   public ConfigCommand.ArgConfig getArgConfig() {
      return this.argConfig;
   }
}
