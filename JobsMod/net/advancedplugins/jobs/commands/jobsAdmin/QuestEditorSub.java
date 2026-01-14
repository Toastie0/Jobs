package net.advancedplugins.jobs.commands.jobsAdmin;

import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.commands.JobSubCommand;
import org.bukkit.entity.Player;

public class QuestEditorSub extends JobSubCommand<Player> {
   public QuestEditorSub(Core var1) {
      super(var1);
      this.inheritPermission();
      this.addFlat("editor");
   }

   public void onExecute(Player var1, String[] var2) {
      Core.getInstance().getCreatorHandler().openEditor(var1);
   }
}
