package net.advancedplugins.simplespigot.storage;

import com.google.gson.JsonObject;
import java.util.Set;

public interface Backend {
   JsonObject load(String var1);

   void save(String var1, JsonObject var2);

   Set<JsonObject> loadAll();

   void close();
}
