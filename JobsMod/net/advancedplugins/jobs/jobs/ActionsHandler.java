package net.advancedplugins.jobs.jobs;

import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.impl.actions.ActionExecution;
import net.advancedplugins.jobs.impl.actions.ActionsReader;

public class ActionsHandler implements ActionsReader {
   @Override
   public void onAction(ActionExecution var1) {
      Core.getInstance().getJobsPipeline().handle(var1);
   }
}
