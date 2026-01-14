package net.advancedplugins.jobs.impl.utils.universalDataStorage;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerDataProvider {
   <T> T getData(UUID var1, String var2, Class<T> var3);

   void saveData(UUID var1, String var2, Object var3);

   void removeData(UUID var1, String var2);

   void clearPlayerData(UUID var1);

   void saveAllPlayerDataAsync();

   void saveAllPlayerDataSync();

   CompletableFuture<Map<String, Object>> getPlayerDataAsync(UUID var1);

   void shutdown();

   boolean isPlayerDataLoaded(UUID var1);
}
