package net.advancedplugins.jobs.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.math.BigDecimal;
import net.advancedplugins.jobs.objects.users.UserJobInfo;

public class JobInfoAdapter extends TypeAdapter<UserJobInfo> {
   public void write(JsonWriter var1, UserJobInfo var2) {
      var1.beginObject();
      var1.name("job").value(var2.getJob());
      var1.name("level").value(var2.getLevel());
      var1.name("progress").value(var2.getProgress().doubleValue());
      var1.name("active").value(var2.isActive());
      var1.endObject();
   }

   public UserJobInfo read(JsonReader var1) {
      var1.beginObject();
      String var2 = "";
      int var3 = 0;
      BigDecimal var4 = BigDecimal.ZERO;
      boolean var5 = false;

      while (var1.hasNext()) {
         String var6 = var1.nextName();
         switch (var6) {
            case "job":
               var2 = var1.nextString();
               break;
            case "level":
               var3 = var1.nextInt();
               break;
            case "progress":
               var4 = BigDecimal.valueOf(var1.nextDouble());
               break;
            case "active":
               var5 = var1.nextBoolean();
         }
      }

      var1.endObject();
      return new UserJobInfo(var2, var3, var4, var5);
   }
}
