package net.advancedplugins.jobs.bstats.charts;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.advancedplugins.jobs.bstats.json.JsonObjectBuilder;

public class DrilldownPie extends CustomChart {
   private final Callable<Map<String, Map<String, Integer>>> callable;

   public DrilldownPie(String var1, Callable<Map<String, Map<String, Integer>>> var2) {
      super(var1);
      this.callable = var2;
   }

   @Override
   public JsonObjectBuilder.JsonObject getChartData() {
      JsonObjectBuilder var1 = new JsonObjectBuilder();
      Map var2 = this.callable.call();
      if (var2 != null && !var2.isEmpty()) {
         boolean var3 = true;

         for (Entry var5 : var2.entrySet()) {
            JsonObjectBuilder var6 = new JsonObjectBuilder();
            boolean var7 = true;

            for (Entry var9 : ((Map)var2.get(var5.getKey())).entrySet()) {
               var6.appendField((String)var9.getKey(), (Integer)var9.getValue());
               var7 = false;
            }

            if (!var7) {
               var3 = false;
               var1.appendField((String)var5.getKey(), var6.build());
            }
         }

         return var3 ? null : new JsonObjectBuilder().appendField("values", var1.build()).build();
      } else {
         return null;
      }
   }
}
