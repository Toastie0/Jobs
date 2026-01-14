package net.advancedplugins.jobs.actions;

public class DynamicAction extends Action {
   public DynamicAction(String var1, String var2) {
      super(var1, var2);
   }

   public synchronized void accept(String var1, Runnable var2) {
      if (this.value.equalsIgnoreCase(var1)) {
         var2.run();
      }
   }
}
