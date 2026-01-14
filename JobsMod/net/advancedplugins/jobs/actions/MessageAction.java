package net.advancedplugins.jobs.actions;

import net.advancedplugins.jobs.impl.utils.text.Replacer;
import net.advancedplugins.jobs.impl.utils.text.Text;
import org.bukkit.entity.Player;

public class MessageAction extends Action {
   public MessageAction(String var1, String var2) {
      super(var1, var2);
   }

   public synchronized void accept(Player var1, Replacer var2) {
      Text.sendMessage(var1, var2 == null ? this.value : var2.applyTo(this.value));
   }
}
