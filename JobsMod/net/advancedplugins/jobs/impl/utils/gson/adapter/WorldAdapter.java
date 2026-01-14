package net.advancedplugins.jobs.impl.utils.gson.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldAdapter extends TypeAdapter<World> {
   public void write(JsonWriter var1, World var2) {
      var1.beginObject();
      var1.name("world").value(var2.getName());
      var1.endObject();
   }

   public World read(JsonReader var1) {
      var1.beginObject();
      World var2 = null;
      if (var1.hasNext()) {
         var1.nextName();
         var2 = Bukkit.getWorld(var1.nextString());
      }

      var1.endObject();
      return var2;
   }
}
