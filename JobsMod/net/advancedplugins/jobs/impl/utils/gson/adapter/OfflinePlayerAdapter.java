package net.advancedplugins.jobs.impl.utils.gson.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class OfflinePlayerAdapter extends TypeAdapter<OfflinePlayer> {
   public void write(JsonWriter var1, OfflinePlayer var2) {
      var1.beginObject();
      var1.name("uuid").value(var2.getUniqueId().toString());
      var1.endObject();
   }

   public OfflinePlayer read(JsonReader var1) {
      var1.beginObject();
      OfflinePlayer var2 = null;
      if (var1.hasNext()) {
         var1.nextName();
         var2 = Bukkit.getOfflinePlayer(UUID.fromString(var1.nextString()));
      }

      var1.endObject();
      return var2;
   }
}
