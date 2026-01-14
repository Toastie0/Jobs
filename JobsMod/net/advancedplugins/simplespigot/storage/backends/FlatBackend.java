package net.advancedplugins.simplespigot.storage.backends;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.advancedplugins.simplespigot.storage.Backend;

public class FlatBackend implements Backend {
   private final Path path;
   private final Path subPath;

   public FlatBackend(Path var1) {
      this.subPath = var1;
      this.path = this.createPath(var1);
   }

   @Override
   public JsonObject load(String var1) {
      try {
         Path var2 = this.path.resolve(var1 + ".json");
         if (!Files.exists(var2)) {
            return null;
         } else {
            FileReader var3 = new FileReader(var2.toFile());
            JsonParser var4 = new JsonParser();
            JsonElement var5 = var4.parse(var3);
            var3.close();
            return var5.isJsonNull() ? null : var5.getAsJsonObject();
         }
      } catch (Throwable var6) {
         throw var6;
      }
   }

   @Override
   public void save(String var1, JsonObject var2) {
      try {
         if (Files.exists(this.path)) {
            Path var3 = this.path.resolve(var1 + ".json");
            Gson var4 = new GsonBuilder().create();
            BufferedWriter var5 = Files.newBufferedWriter(var3);
            var4.toJson(var2, var5);
            var5.close();
         }
      } catch (Throwable var6) {
         throw var6;
      }
   }

   @Override
   public Set<JsonObject> loadAll() {
      try {
         return Files.walk(this.path)
            .map(Path::toString)
            .filter(var0 -> var0.endsWith(".json"))
            .map(var0 -> var0.replace(".json", ""))
            .map(this::load)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
      } catch (Throwable var2) {
         throw var2;
      }
   }

   @Override
   public void close() {
   }

   private Path createPath(Path var1) {
      try {
         if (!Files.exists(var1) || !Files.isDirectory(var1) && !Files.isSymbolicLink(var1)) {
            var1.toFile().mkdirs();
            return var1;
         } else {
            return var1;
         }
      } catch (Throwable var3) {
         throw var3;
      }
   }
}
