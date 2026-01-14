package net.advancedplugins.jobs.bstats.charts;

import java.util.concurrent.Callable;
import net.advancedplugins.jobs.bstats.json.JsonObjectBuilder;

public class SimplePie extends CustomChart {
   private final Callable<String> callable;

   public SimplePie(String var1, Callable<String> var2) {
      super(var1);
      this.callable = var2;
   }

   @Override
   protected JsonObjectBuilder.JsonObject getChartData() {
      String var1 = this.callable.call();
      return var1 != null && !var1.isEmpty() ? new JsonObjectBuilder().appendField("value", var1).build() : null;
   }
}
