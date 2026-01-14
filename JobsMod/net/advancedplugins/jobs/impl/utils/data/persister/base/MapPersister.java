package net.advancedplugins.jobs.impl.utils.data.persister.base;

import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.LongStringType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import net.advancedplugins.jobs.impl.utils.collections.CollectionUtil;
import net.advancedplugins.jobs.impl.utils.gson.GsonAdapterController;

public class MapPersister extends LongStringType {
   private static final MapPersister SINGLETON = new MapPersister();
   private final String NONE_TAG = "NONE";

   public MapPersister() {
      super(SqlType.LONG_STRING, new Class[]{Map.class});
   }

   public static MapPersister getSingleton() {
      return SINGLETON;
   }

   public Object javaToSqlArg(FieldType var1, Object var2) {
      Map var3 = (Map)var2;
      String var4 = "NONE";
      String var5 = "NONE";
      if (!var3.isEmpty()) {
         var4 = var3.keySet().toArray()[0].getClass().getName();
         var5 = CollectionUtil.getObjectTypes(var3.values().toArray()[0]);
      }

      String var6 = GsonAdapterController.getInstance().getGson().toJson(var3);
      return var4 + " " + var5 + " " + var6;
   }

   public Object sqlArgToJava(FieldType var1, Object var2, int var3) {
      String[] var4 = ((String)var2).split(" ", 3);
      if (var4.length < 3) {
         return new HashMap();
      } else if (!var4[0].equalsIgnoreCase("NONE") && !var4[1].equalsIgnoreCase("NONE")) {
         try {
            Class var5 = Class.forName(var4[0]);
            Type var6 = CollectionUtil.getTypesFromString(var4[1])[0];
            Type var7 = TypeToken.getParameterized(Map.class, new Type[]{var5, var6}).getType();
            return GsonAdapterController.getInstance().getGson().fromJson(var4[2], var7);
         } catch (Exception var8) {
            var8.printStackTrace();
            return new HashMap();
         }
      } else {
         return new HashMap();
      }
   }
}
