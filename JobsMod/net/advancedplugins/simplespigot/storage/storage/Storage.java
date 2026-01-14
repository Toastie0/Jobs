package net.advancedplugins.simplespigot.storage.storage;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import net.advancedplugins.simplespigot.plugin.SimplePlugin;
import net.advancedplugins.simplespigot.storage.Backend;
import net.advancedplugins.simplespigot.storage.BackendFactory;
import net.advancedplugins.simplespigot.storage.adapter.Adapter;
import net.advancedplugins.simplespigot.storage.storage.load.Deserializer;
import net.advancedplugins.simplespigot.storage.storage.load.Serializer;

public abstract class Storage<T> {
   private Backend backend;
   private GsonBuilder gsonBuilder;
   private Gson gson;

   public Storage(SimplePlugin var1, Function<BackendFactory, Backend> var2) {
      this.backend = (Backend)var2.apply(new BackendFactory(var1));
      this.gsonBuilder = new GsonBuilder();
      this.gson = new Gson();
   }

   public Storage(Backend var1) {
      this.backend = var1;
   }

   public abstract Serializer<T> serializer();

   public abstract Deserializer<T> deserializer();

   public void addAdapter(Class<?> var1, Adapter<?> var2) {
      this.gsonBuilder.registerTypeAdapter(var1, var2);
   }

   public void addAdapter(Class<?> var1, TypeAdapter<?> var2) {
      this.gsonBuilder.registerTypeAdapter(var1, var2);
   }

   public void saveChanges() {
      this.gson = this.gsonBuilder.create();
   }

   public T load(String var1) {
      JsonObject var2 = this.backend.load(var1);
      return var2 == null ? null : this.deserializer().apply(var2, this.gson);
   }

   public T save(String var1, T var2) {
      this.backend.save(var1, this.serializer().apply((T)var2, new JsonObject(), this.gson));
      return (T)var2;
   }

   public Set<T> loadAll() {
      HashSet var1 = Sets.newHashSet();

      for (JsonObject var3 : this.backend.loadAll()) {
         var1.add(this.deserializer().apply(var3, this.gson));
      }

      return var1;
   }

   public void closeBack() {
      this.backend.close();
   }
}
