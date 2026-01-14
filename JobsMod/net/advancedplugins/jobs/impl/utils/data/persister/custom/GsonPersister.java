package net.advancedplugins.jobs.impl.utils.data.persister.custom;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.LongStringType;
import net.advancedplugins.jobs.impl.utils.gson.GsonAdapterController;

public class GsonPersister extends LongStringType {
   private static final GsonPersister SINGLETON = new GsonPersister();
   private final String NONE_TAG = "NONE";

   public GsonPersister() {
      super(SqlType.LONG_STRING, new Class[0]);
   }

   public static GsonPersister getSingleton() {
      return SINGLETON;
   }

   public Object javaToSqlArg(FieldType var1, Object var2) {
      String var3 = "NONE";
      if (var2 != null) {
         var3 = var2.getClass().getName();
      }

      String var4 = GsonAdapterController.getInstance().getGson().toJson(var2);
      return var3 + " " + var4;
   }

   public Object sqlArgToJava(FieldType var1, Object var2, int var3) {
      String[] var4 = ((String)var2).split(" ", 2);
      if (var4.length < 2) {
         return null;
      } else if (var4[0].equalsIgnoreCase("NONE")) {
         return null;
      } else {
         try {
            return GsonAdapterController.getInstance().getGson().fromJson(var4[1], Class.forName(var4[0]));
         } catch (Exception var6) {
            return null;
         }
      }
   }
}
