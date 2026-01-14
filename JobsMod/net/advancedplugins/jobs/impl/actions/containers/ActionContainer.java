package net.advancedplugins.jobs.impl.actions.containers;

import net.advancedplugins.jobs.impl.actions.ActionExecutionBuilder;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ActionContainer implements Listener {
   public ActionContainer(JavaPlugin var1) {
   }

   public ActionExecutionBuilder executionBuilder(String var1) {
      return ActionExecutionBuilder.of(this, var1);
   }
}
