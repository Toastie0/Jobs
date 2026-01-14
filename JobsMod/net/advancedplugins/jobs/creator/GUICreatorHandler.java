package net.advancedplugins.jobs.creator;

import java.util.HashMap;
import java.util.UUID;
import net.advancedplugins.jobs.creator.gui.CreatorEditJob;
import net.advancedplugins.jobs.creator.gui.CreatorJobList;
import net.advancedplugins.jobs.creator.listeners.ChatListener;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GUICreatorHandler {
   private HashMap<UUID, GUICreator> editors = new HashMap<>();
   private static GUICreatorHandler handler = null;
   private CreatorJobList creatorJobList;
   private CreatorEditJob creatorEditJob;

   public GUICreatorHandler(JavaPlugin var1) {
      handler = this;
      this.loadInventories(var1);
   }

   public GUICreator getEditor(UUID var1) {
      return this.editors.get(var1);
   }

   public boolean isEditing(UUID var1) {
      return this.editors.containsKey(var1);
   }

   public void stopAllEditors() {
      this.editors.values().forEach(GUICreator::stop);
   }

   public static GUICreatorHandler getHandler() {
      return handler == null ? null : handler;
   }

   public void stopEditing(UUID var1) {
      GUICreator var2 = this.editors.remove(var1);
      if (var2 != null) {
         var2.stop();
      }
   }

   public static String color(String var0) {
      return Text.modify(var0);
   }

   public void startNewEditor(Player var1) {
      this.stopEditing(var1.getUniqueId());
      GUICreator var2 = new GUICreator(var1);
      this.editors.put(var1.getUniqueId(), var2);
   }

   public void openEditor(Player var1) {
      if (this.isEditing(var1.getUniqueId())) {
         GUICreator var2 = this.getEditor(var1.getUniqueId());
         if (var2.getJob() != null) {
            CreatorEditJob.open(var1);
         } else {
            CreatorJobList.open(var1);
         }
      } else {
         this.startNewEditor(var1);
         CreatorJobList.open(var1);
      }
   }

   public CreatorEditJob getCreatorEditQuest() {
      return this.creatorEditJob;
   }

   public CreatorJobList getCreatorQuestList() {
      return this.creatorJobList;
   }

   public void loadInventories(JavaPlugin var1) {
      this.creatorJobList = new CreatorJobList(var1);
      this.creatorEditJob = new CreatorEditJob(var1);
      Bukkit.getPluginManager().registerEvents(new ChatListener(), var1);
   }
}
