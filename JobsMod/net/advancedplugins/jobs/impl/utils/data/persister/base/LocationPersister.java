package net.advancedplugins.jobs.impl.utils.data.persister.base;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import net.advancedplugins.jobs.impl.utils.gson.GsonAdapterController;
import org.bukkit.Location;

public class LocationPersister extends StringType {
   private static final LocationPersister SINGLETON = new LocationPersister();

   public LocationPersister() {
      super(SqlType.STRING, new Class[]{Location.class});
   }

   public static LocationPersister getSingleton() {
      return SINGLETON;
   }

   public Object javaToSqlArg(FieldType var1, Object var2) {
      return GsonAdapterController.getInstance().getGson().toJson(var2);
   }

   public Object sqlArgToJava(FieldType var1, Object var2, int var3) {
      return GsonAdapterController.getInstance().getGson().fromJson((String)var2, Location.class);
   }
}
