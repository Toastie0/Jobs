package net.advancedplugins.jobs.bstats.charts;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.advancedplugins.jobs.bstats.json.JsonObjectBuilder;

public class AdvancedPie extends CustomChart {
   private final Callable<Map<String, Integer>> callable;

   public AdvancedPie(String var1, Callable<Map<String, Integer>> var2) {
      super(var1);
      this.callable = var2;
   }

   @Override
   protected JsonObjectBuilder.JsonObject getChartData() {
      JsonObjectBuilder var1 = new JsonObjectBuilder();
      Map var2 = this.callable.call();
      if (var2 != null && !var2.isEmpty()) {
         boolean var3 = true;

         for (Entry var5 : var2.entrySet()) {
            if ((Integer)var5.getValue() != 0) {
               var3 = false;
               var1.appendField((String)var5.getKey(), (Integer)var5.getValue());
            }
         }

         return var3 ? null : new JsonObjectBuilder().appendField("values", var1.build()).build();
      } else {
         return null;
      }
   }
}
