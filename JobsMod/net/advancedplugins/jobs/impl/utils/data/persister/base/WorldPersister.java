package net.advancedplugins.jobs.impl.utils.data.persister.base;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import net.advancedplugins.jobs.impl.utils.gson.GsonAdapterController;
import org.bukkit.World;

public class WorldPersister extends StringType {
   private static final WorldPersister SINGLETON = new WorldPersister();

   public WorldPersister() {
      super(SqlType.STRING, new Class[]{World.class});
   }

   public static WorldPersister getSingleton() {
      return SINGLETON;
   }

   public Object javaToSqlArg(FieldType var1, Object var2) {
      return GsonAdapterController.getInstance().getGson().toJson(var2);
   }

   public Object sqlArgToJava(FieldType var1, Object var2, int var3) {
      return GsonAdapterController.getInstance().getGson().fromJson((String)var2, World.class);
   }
}
