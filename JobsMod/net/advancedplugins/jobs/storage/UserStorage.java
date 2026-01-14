package net.advancedplugins.jobs.storage;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.adapter.JobInfoAdapter;
import net.advancedplugins.jobs.impl.utils.uuid.FastUuid;
import net.advancedplugins.jobs.objects.users.JobStore;
import net.advancedplugins.jobs.objects.users.User;
import net.advancedplugins.jobs.objects.users.UserJobInfo;
import net.advancedplugins.simplespigot.storage.storage.Storage;
import net.advancedplugins.simplespigot.storage.storage.load.Deserializer;
import net.advancedplugins.simplespigot.storage.storage.load.Serializer;
import org.bukkit.Bukkit;

public class UserStorage extends Storage<User> {
   public UserStorage(Core var1) {
      super(var1, var1x -> var1x.create(var1.getConfigStore().commons().get("storage-type"), var0x -> var0x.resolve("users-storage"), "", "users"));
      super.addAdapter(UserJobInfo.class, new JobInfoAdapter());
      super.saveChanges();
   }

   @Override
   public Serializer<User> serializer() {
      return (var0, var1, var2) -> {
         var1.addProperty("uuid", FastUuid.toString(var0.getUuid()));
         var1.addProperty("jobs-v2", var2.toJson(var0.getJobStore().asMap()));
         var1.addProperty("pass-id", var0.getPassId());
         var1.addProperty("points", var0.getPoints());
         return var1;
      };
   }

   @Override
   public Deserializer<User> deserializer() {
      return (var1, var2) -> {
         UUID var3 = FastUuid.parse(var1.get("uuid").getAsString());

         try {
            Object var4 = new HashMap();
            boolean var5 = false;
            if (var1.has("jobs")) {
               JsonObject var6 = (JsonObject)var2.fromJson(var1.get("jobs").getAsString(), JsonObject.class);
               if (var6.size() > 0) {
                  Entry var7 = (Entry)var6.entrySet().toArray()[0];
                  var4 = this.migrate(var1, var2, (JsonElement)var7.getValue());
               }

               var5 = true;
            } else if (var1.has("jobs-v2")) {
               var4 = (Map)var2.fromJson(
                  var1.get("jobs-v2").getAsString(), TypeToken.getParameterized(Map.class, new Type[]{String.class, UserJobInfo.class}).getType()
               );
            }

            JobStore var12 = new JobStore();
            var12.asMap().putAll((Map<? extends String, ? extends UserJobInfo>)var4);
            String var13 = var1.get("pass-id").getAsString();
            double var8 = var1.get("points").getAsDouble();
            User var10 = new User(var3, var12, var13.equalsIgnoreCase("premium"), var8);
            if (var5) {
               this.save(var3.toString(), var10);
            }

            return var10;
         } catch (Exception var11) {
            Bukkit.getLogger().log(Level.SEVERE, "Error whilst loading player data file: " + var3 + ".json", (Throwable)var11);
            return null;
         }
      };
   }

   private Map<String, UserJobInfo> migrate(JsonObject var1, Gson var2, JsonElement var3) {
      String var4 = var1.get("uuid").getAsString();
      Bukkit.getLogger().log(Level.INFO, "Migrating user " + var4 + " to new data format");
      ConcurrentHashMap var5 = new ConcurrentHashMap();
      var3.getAsJsonObject().entrySet().forEach(var1x -> {
         String var2x = (String)var1x.getKey();
         JsonObject var3x = ((JsonElement)var1x.getValue()).getAsJsonObject();
         int var4x = 0;
         int var5x = 0;
         BigDecimal var6 = BigDecimal.ZERO;
         boolean var7 = false;

         for (Entry var9 : var3x.entrySet()) {
            switch (var4x) {
               case 0:
               default:
                  break;
               case 1:
                  var5x = ((JsonElement)var9.getValue()).getAsInt();
                  break;
               case 2:
                  var6 = BigDecimal.valueOf(((JsonElement)var9.getValue()).getAsDouble());
                  break;
               case 3:
                  var7 = ((JsonElement)var9.getValue()).getAsBoolean();
            }

            var4x++;
         }

         var5.put(var2x, new UserJobInfo(var2x, var5x, var6, var7));
      });
      return var5;
   }
}
