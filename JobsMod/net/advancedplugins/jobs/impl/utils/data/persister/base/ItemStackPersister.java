package net.advancedplugins.jobs.impl.utils.data.persister.base;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.LongStringType;
import net.advancedplugins.jobs.impl.utils.gson.GsonAdapterController;
import org.bukkit.inventory.ItemStack;

public class ItemStackPersister extends LongStringType {
   private static final ItemStackPersister SINGLETON = new ItemStackPersister();

   public ItemStackPersister() {
      super(SqlType.LONG_STRING, new Class[]{ItemStack.class});
   }

   public static ItemStackPersister getSingleton() {
      return SINGLETON;
   }

   public Object javaToSqlArg(FieldType var1, Object var2) {
      return GsonAdapterController.getInstance().getGson().toJson(var2);
   }

   public Object sqlArgToJava(FieldType var1, Object var2, int var3) {
      return GsonAdapterController.getInstance().getGson().fromJson((String)var2, ItemStack.class);
   }
}
