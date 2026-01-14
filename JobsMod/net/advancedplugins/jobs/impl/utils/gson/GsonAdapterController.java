package net.advancedplugins.jobs.impl.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import java.lang.reflect.Type;
import net.advancedplugins.jobs.impl.utils.gson.adapter.ItemStackAdapter;
import net.advancedplugins.jobs.impl.utils.gson.adapter.LocationAdapter;
import net.advancedplugins.jobs.impl.utils.gson.adapter.OfflinePlayerAdapter;
import net.advancedplugins.jobs.impl.utils.gson.adapter.WorldAdapter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class GsonAdapterController {
   private static final GsonAdapterController INSTANCE = new GsonAdapterController();
   private final GsonBuilder gsonBuilder = new GsonBuilder();

   private GsonAdapterController() {
      this.registerDefaultAdapters();
   }

   private void registerDefaultAdapters() {
      this.registerAdapter(ItemStack.class, new ItemStackAdapter());
      this.registerAdapter(Location.class, new LocationAdapter());
      this.registerAdapter(OfflinePlayer.class, new OfflinePlayerAdapter());
      this.registerAdapter(World.class, new WorldAdapter());
   }

   public GsonAdapterController registerAdapter(Type var1, TypeAdapter<?> var2) {
      this.gsonBuilder.registerTypeAdapter(var1, var2);
      return INSTANCE;
   }

   public Gson getGson() {
      return this.gsonBuilder.create();
   }

   public static GsonAdapterController getInstance() {
      return INSTANCE;
   }
}
