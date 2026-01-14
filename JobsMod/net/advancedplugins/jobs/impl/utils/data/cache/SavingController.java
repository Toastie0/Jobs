package net.advancedplugins.jobs.impl.utils.data.cache;

import com.google.common.collect.Sets;
import java.util.Set;
import net.advancedplugins.jobs.impl.utils.FoliaScheduler;
import net.advancedplugins.jobs.impl.utils.data.cache.iface.IAsyncSavableCache;
import net.advancedplugins.jobs.impl.utils.data.cache.iface.ISavable;
import org.bukkit.plugin.Plugin;

public class SavingController {
   private final Plugin plugin;
   private final Set<SavingController.SaveTask> saveTasks = Sets.newHashSet();

   public SavingController(Plugin var1) {
      this.plugin = var1;
   }

   public void addSavable(ISavable<?, ?> var1, int var2) {
      this.saveTasks.add(new SavingController.SaveTask(this.plugin, var1, var2));
   }

   public void clearController() {
      for (SavingController.SaveTask var2 : this.saveTasks) {
         var2.stop();
      }

      this.saveTasks.clear();
   }

   public void saveAll() {
      this.saveTasks.forEach(var0 -> var0.savable.saveAll());
   }

   public static class SaveTask {
      private final ISavable<?, ?> savable;
      private final FoliaScheduler.Task bukkitTask;

      public SaveTask(Plugin var1, ISavable<?, ?> var2, int var3) {
         this.savable = var2;
         this.bukkitTask = FoliaScheduler.runTaskTimerAsynchronously(var1, () -> {
            if (this.savable instanceof IAsyncSavableCache var2x) {
               var2x.saveAsyncAll();
            } else {
               var2.saveAll();
            }
         }, (long)var3, (long)var3);
      }

      public void stop() {
         this.bukkitTask.cancel();
      }
   }
}
