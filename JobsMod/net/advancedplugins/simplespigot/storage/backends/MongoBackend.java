package net.advancedplugins.simplespigot.storage.backends;

import com.google.gson.JsonObject;
import java.util.Set;
import net.advancedplugins.simplespigot.storage.Backend;

public class MongoBackend implements Backend {
   @Override
   public JsonObject load(String var1) {
      return null;
   }

   @Override
   public void save(String var1, JsonObject var2) {
   }

   @Override
   public Set<JsonObject> loadAll() {
      return null;
   }

   @Override
   public void close() {
   }
}
