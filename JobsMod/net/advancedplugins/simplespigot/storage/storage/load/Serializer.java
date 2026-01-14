package net.advancedplugins.simplespigot.storage.storage.load;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@FunctionalInterface
public interface Serializer<T> {
   JsonObject apply(T var1, JsonObject var2, Gson var3);
}
