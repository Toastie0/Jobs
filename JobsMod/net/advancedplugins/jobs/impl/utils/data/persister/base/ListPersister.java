package net.advancedplugins.jobs.impl.utils.data.persister.base;

import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.LongStringType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import net.advancedplugins.jobs.impl.utils.collections.CollectionUtil;
import net.advancedplugins.jobs.impl.utils.gson.GsonAdapterController;

public class ListPersister extends LongStringType {
   private static final ListPersister SINGLETON = new ListPersister();
   private final String NONE_TAG = "NONE";

   public ListPersister() {
      super(SqlType.LONG_STRING, new Class[]{List.class});
   }

   public static ListPersister getSingleton() {
      return SINGLETON;
   }

   public Object javaToSqlArg(FieldType var1, Object var2) {
      List var3 = (List)var2;
      String var4 = "NONE";
      if (!var3.isEmpty()) {
         var4 = CollectionUtil.getObjectTypes(var3.get(0));
      }

      String var5 = GsonAdapterController.getInstance().getGson().toJson(var3);
      return var4 + " " + var5;
   }

   public Object sqlArgToJava(FieldType var1, Object var2, int var3) {
      String[] var4 = ((String)var2).split(" ", 2);
      if (var4.length < 2) {
         return new ArrayList();
      } else if (var4[0].equalsIgnoreCase("NONE")) {
         return new ArrayList();
      } else {
         try {
            Type var5 = CollectionUtil.getTypesFromString(var4[0])[0];
            Type var6 = TypeToken.getParameterized(List.class, new Type[]{var5}).getType();
            return GsonAdapterController.getInstance().getGson().fromJson(var4[1], var6);
         } catch (Exception var7) {
            return new ArrayList();
         }
      }
   }
}
