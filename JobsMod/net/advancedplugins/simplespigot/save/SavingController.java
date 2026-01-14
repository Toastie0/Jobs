package net.advancedplugins.simplespigot.save;

import com.google.common.collect.Sets;
import java.util.Set;
import org.bukkit.plugin.Plugin;

public class SavingController {
   private final Plugin plugin;
   private Set<SaveTask> saveTasks = Sets.newHashSet();

   public SavingController(Plugin var1) {
      this.plugin = var1;
   }

   public void addSavable(Savable var1, int var2) {
      this.saveTasks.add(new SaveTask(this.plugin, var1, var2));
   }

   public void clearController() {
      for (SaveTask var2 : this.saveTasks) {
         var2.stop();
      }

      this.saveTasks.clear();
   }
}
