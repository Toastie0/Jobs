package net.advancedplugins.jobs.impl.utils.gson.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.advancedplugins.jobs.impl.utils.ASManager;
import net.advancedplugins.jobs.impl.utils.trycatch.TryCatchUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemStackAdapter extends TypeAdapter<ItemStack> {
   public void write(JsonWriter var1, ItemStack var2) {
      var1.beginObject();
      var1.name("item").value(TryCatchUtil.tryAndReturn(() -> ASManager.serializeItem(var2)));
      var1.endObject();
   }

   public ItemStack read(JsonReader var1) {
      var1.beginObject();
      ItemStack var2 = new ItemStack(Material.AIR);
      if (var1.hasNext()) {
         var1.nextName();
         String var3 = var1.nextString();
         var2 = TryCatchUtil.tryAndReturn(() -> ASManager.deserializeItem(var3));
      }

      var1.endObject();
      return var2;
   }
}
