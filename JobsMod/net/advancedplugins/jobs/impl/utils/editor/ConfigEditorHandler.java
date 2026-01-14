package net.advancedplugins.jobs.impl.utils.editor;

import org.bukkit.entity.Player;

public interface ConfigEditorHandler {
   boolean canCreateNewEntries();

   String getGlassColor();

   String getTextColor();

   ConfigEditorGui create(String var1, Player var2);

   ConfigEditorGui updateFiles(KeyInfo var1, Player var2, String var3, String var4);

   void openMainMenu(Player var1);

   ConfigEditorGui openEditor(KeyInfo var1, Player var2);

   ConfigEditorGui openEditor(KeyInfo var1, Player var2, String var3);
}
