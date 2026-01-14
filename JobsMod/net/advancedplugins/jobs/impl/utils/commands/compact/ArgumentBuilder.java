package net.advancedplugins.jobs.impl.utils.commands.compact;

import com.google.common.collect.Lists;
import java.util.List;
import net.advancedplugins.jobs.impl.utils.commands.argument.Argument;
import net.advancedplugins.jobs.impl.utils.commands.argument.ArgumentHandler;

public class ArgumentBuilder {
   private final List<Argument<?>> arguments = Lists.newArrayList();

   public List<Argument<?>> getArguments() {
      return this.arguments;
   }

   public ArgumentBuilder addFlat(String var1) {
      this.arguments.add(new Argument(null, var1));
      return this;
   }

   public ArgumentBuilder addFlatWithAliases(String var1, String... var2) {
      this.arguments.add(new Argument(null, var1, var2));
      return this;
   }

   public ArgumentBuilder addFlats(String... var1) {
      for (String var5 : var1) {
         this.addFlat(var5);
      }

      return this;
   }

   public <T> ArgumentBuilder addArgument(Class<T> var1, String var2) {
      this.arguments.add(new Argument(ArgumentHandler.getArgumentType(var1), var2));
      return this;
   }
}
