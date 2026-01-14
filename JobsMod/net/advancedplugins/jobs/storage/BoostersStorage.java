package net.advancedplugins.jobs.storage;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.adapter.BoosterAdapter;
import net.advancedplugins.jobs.controller.BoostersController;
import net.advancedplugins.simplespigot.storage.storage.Storage;
import net.advancedplugins.simplespigot.storage.storage.load.Deserializer;
import net.advancedplugins.simplespigot.storage.storage.load.Serializer;

public class BoostersStorage extends Storage<BoostersController> {
   private final Core plugin;

   public BoostersStorage(Core var1) {
      super(var1, var1x -> var1x.create(var1.getConfigStore().commons().get("storage-type"), var0x -> var0x.resolve("boosters"), "", "boosters-data"));
      super.addAdapter(BoostersController.Booster.class, new BoosterAdapter());
      this.plugin = var1;
   }

   @Override
   public Serializer<BoostersController> serializer() {
      return (var0, var1, var2) -> {
         var1.addProperty("server-boosters", var2.toJson(var0.getServerBoosters()));
         var1.addProperty("player-boosters", var2.toJson(var0.getPlayerBoosters()));
         return var1;
      };
   }

   @Override
   public Deserializer<BoostersController> deserializer() {
      return (var1, var2) -> {
         Map var3 = (Map)var2.fromJson(
            var1.get("server-boosters").getAsString(),
            TypeToken.getParameterized(
                  Map.class,
                  new Type[]{
                     BoostersController.BoosterType.class, TypeToken.getParameterized(List.class, new Type[]{BoostersController.Booster.class}).getType()
                  }
               )
               .getType()
         );
         Map var4 = (Map)var2.fromJson(
            var1.get("player-boosters").getAsString(),
            TypeToken.getParameterized(
                  Map.class,
                  new Type[]{
                     UUID.class,
                     TypeToken.getParameterized(
                           Map.class,
                           new Type[]{
                              BoostersController.BoosterType.class,
                              TypeToken.getParameterized(List.class, new Type[]{BoostersController.Booster.class}).getType()
                           }
                        )
                        .getType()
                  }
               )
               .getType()
         );
         return new BoostersController(this.plugin, var3, var4);
      };
   }
}
