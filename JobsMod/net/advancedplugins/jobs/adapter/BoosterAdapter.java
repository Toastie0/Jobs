package net.advancedplugins.jobs.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.advancedplugins.jobs.controller.BoostersController;

public class BoosterAdapter extends TypeAdapter<BoostersController.Booster> {
   public void write(JsonWriter var1, BoostersController.Booster var2) {
      var1.beginObject();
      var1.name("from").value(var2.getFrom());
      var1.name("percent").value(var2.getPercent());
      var1.name("affects").value(var2.getAffects());
      var1.name("expires").value(var2.getExpires());
      var1.name("type").value(var2.getType().name());
      var1.name("target").value(var2.getTarget().name());
      var1.endObject();
   }

   public BoostersController.Booster read(JsonReader var1) {
      String var2 = "";
      double var3 = 0.0;
      String var5 = "";
      long var6 = 0L;
      BoostersController.BoosterType var8 = null;
      BoostersController.BoosterTarget var9 = null;

      while (var1.hasNext()) {
         String var10 = var1.nextName();
         switch (var10) {
            case "from":
               var2 = var1.nextString();
               break;
            case "percent":
               var3 = var1.nextDouble();
               break;
            case "affects":
               var5 = var1.nextString();
               break;
            case "expires":
               var6 = var1.nextLong();
               break;
            case "type":
               var8 = BoostersController.BoosterType.valueOf(var1.nextString());
               break;
            case "target":
               var9 = BoostersController.BoosterTarget.valueOf(var1.nextString());
         }
      }

      var1.endObject();
      return new BoostersController.Booster(var2, var3, var5, var6, var8, var9);
   }
}
