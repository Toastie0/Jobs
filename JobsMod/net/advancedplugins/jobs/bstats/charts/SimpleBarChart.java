package net.advancedplugins.jobs.bstats.charts;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.advancedplugins.jobs.bstats.json.JsonObjectBuilder;

public class SimpleBarChart extends CustomChart {
   private final Callable<Map<String, Integer>> callable;

   public SimpleBarChart(String var1, Callable<Map<String, Integer>> var2) {
      super(var1);
      this.callable = var2;
   }

   @Override
   protected JsonObjectBuilder.JsonObject getChartData() {
      JsonObjectBuilder var1 = new JsonObjectBuilder();
      Map var2 = this.callable.call();
      if (var2 != null && !var2.isEmpty()) {
         for (Entry var4 : var2.entrySet()) {
            var1.appendField((String)var4.getKey(), new int[]{(Integer)var4.getValue()});
         }

         return new JsonObjectBuilder().appendField("values", var1.build()).build();
      } else {
         return null;
      }
   }
}
