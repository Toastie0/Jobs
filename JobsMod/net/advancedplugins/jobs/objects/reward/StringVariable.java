package net.advancedplugins.jobs.objects.reward;

import java.util.Map;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.text.Replace;
import net.advancedplugins.jobs.impl.utils.text.Text;

public class StringVariable {
   public static double getVariableResult(Map<String, String> var0, String var1, Replace var2) {
      if (!var0.containsKey(var1)) {
         return 0.0;
      } else {
         String var3 = (String)var0.get(var1);
         return ASManager.parseThroughCalculator(Text.modify(var3, var2));
      }
   }

   public static String fillVariables(Map<String, String> var0, String var1, Replace var2) {
      return fillVariables(var0, var1, "", var2);
   }

   public static String fillVariables(Map<String, String> var0, String var1, String var2, Replace var3) {
      return Text.modify(var1, var3x -> {
         var0.keySet().forEach(var4 -> {
            double var5 = getVariableResult(var0, var4, var3);
            if (Math.floor(var5) == var5) {
               var3x.set(var2 + var4, (long)var5);
            } else {
               var3x.set(var2 + var4, var5);
            }
         });
         return var3x;
      }, false);
   }
}
