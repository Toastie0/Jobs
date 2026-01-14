package net.advancedplugins.jobs.impl.utils.commands.argument;

import com.google.common.collect.Maps;
import java.util.Map;

public class ArgumentHandler {
   private static Map<Class<?>, ArgumentType<?>> argumentTypes = Maps.newHashMap();

   public static void register(Class<?> var0, ArgumentType<?> var1) {
      argumentTypes.put(var0, var1);
   }

   public static <T> ArgumentType<T> getArgumentType(Class<?> var0) {
      return (ArgumentType<T>)argumentTypes.get(var0);
   }
}
