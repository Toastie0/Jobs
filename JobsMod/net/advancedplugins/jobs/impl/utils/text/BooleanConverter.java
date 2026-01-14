package net.advancedplugins.jobs.impl.utils.text;

public class BooleanConverter {
   private BooleanConverter() {
   }

   public static String booleanToYesNo(boolean var0) {
      return var0 ? "yes" : "no";
   }
}
