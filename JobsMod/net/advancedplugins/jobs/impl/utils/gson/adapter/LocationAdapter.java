package net.advancedplugins.jobs.impl.utils.gson.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationAdapter extends TypeAdapter<Location> {
   public void write(JsonWriter var1, Location var2) {
      var1.beginObject();
      var1.name("world").value(var2.getWorld().getName());
      var1.name("x").value(var2.getX());
      var1.name("y").value(var2.getY());
      var1.name("z").value(var2.getZ());
      var1.name("pitch").value(var2.getPitch());
      var1.name("yaw").value(var2.getYaw());
      var1.endObject();
   }

   public Location read(JsonReader var1) {
      var1.beginObject();
      World var2 = null;
      double var3 = 0.0;
      double var5 = 0.0;
      double var7 = 0.0;
      float var9 = 0.0F;
      float var10 = 0.0F;

      while (var1.hasNext()) {
         String var11 = var1.nextName();
         switch (var11) {
            case "world":
               var2 = Bukkit.getWorld(var1.nextString());
               break;
            case "x":
               var3 = var1.nextDouble();
               break;
            case "y":
               var5 = var1.nextDouble();
               break;
            case "z":
               var7 = var1.nextDouble();
               break;
            case "pitch":
               var9 = (float)var1.nextDouble();
               break;
            case "yaw":
               var10 = (float)var1.nextDouble();
         }
      }

      var1.endObject();
      return new Location(var2, var3, var5, var7, var10, var9);
   }
}
