package net.advancedplugins.jobs.impl.utils.actions.impl;

import net.advancedplugins.jobs.impl.utils.actions.Action;
import net.advancedplugins.jobs.impl.utils.text.Replacer;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class MessageAction extends Action {
   public MessageAction(String var1, String var2, double var3) {
      super(var1, var2, var3);
   }

   @Override
   public void accept(Player var1, @Nullable Replacer var2) {
      Text.sendMessage(var1, var2 == null ? this.value : var2.applyTo(this.value));
   }
}
