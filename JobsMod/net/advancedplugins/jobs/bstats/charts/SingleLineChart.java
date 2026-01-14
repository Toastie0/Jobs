package net.advancedplugins.jobs.bstats.charts;

import java.util.concurrent.Callable;
import net.advancedplugins.jobs.bstats.json.JsonObjectBuilder;

public class SingleLineChart extends CustomChart {
   private final Callable<Integer> callable;

   public SingleLineChart(String var1, Callable<Integer> var2) {
      super(var1);
      this.callable = var2;
   }

   @Override
   protected JsonObjectBuilder.JsonObject getChartData() {
      int var1 = this.callable.call();
      return var1 == 0 ? null : new JsonObjectBuilder().appendField("value", var1).build();
   }
}
