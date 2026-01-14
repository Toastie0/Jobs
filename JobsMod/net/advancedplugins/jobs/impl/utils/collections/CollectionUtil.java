package net.advancedplugins.jobs.impl.utils.collections;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class CollectionUtil {
   public static final String NEXT_IDENTIFIER = ">";
   public static final String MAP_ENTRY_SPLIT = "___";

   public static String getObjectTypes(Object var0) {
      StringBuilder var1 = new StringBuilder();

      while (var0 != null) {
         var1.append(var0.getClass().getName());
         if (var0 instanceof Collection var4) {
            if (var4.isEmpty()) {
               break;
            }

            var0 = var4.toArray()[0];
            var1.append(">");
         } else {
            if (!(var0 instanceof Map var2) || var2.isEmpty()) {
               break;
            }

            String var3 = var2.keySet().toArray()[0].getClass().getName();
            var0 = var2.values().toArray()[0];
            var1.append(">").append(var3).append("___");
         }
      }

      return var1.toString();
   }

   public static Type[] getTypesFromString(String var0) {
      return getTypesFromString(var0.split(">"), 0);
   }

   public static Type[] getTypesFromString(String[] var0, int var1) {
      String[] var2 = var0[var1].split("___", 2);
      boolean var3 = var2.length == 2;
      if (var1 + 1 == var0.length) {
         return var3 ? new Class[]{Class.forName(var2[0]), Class.forName(var2[1])} : new Class[]{Class.forName(var0[var1])};
      } else {
         return var3
            ? new Type[]{Class.forName(var2[0]), TypeToken.getParameterized(Class.forName(var2[1]), getTypesFromString(var0, ++var1)).getType()}
            : new Type[]{TypeToken.getParameterized(Class.forName(var0[var1]), getTypesFromString(var0, ++var1)).getType()};
      }
   }
}
