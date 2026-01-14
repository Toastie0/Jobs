package net.advancedplugins.jobs.impl.utils.commands.argument;

public class FutureArgument<T> {
   private final T object;
   private final Runnable finishRunnable;

   public void finish() {
      this.finishRunnable.run();
   }

   public FutureArgument(T var1, Runnable var2) {
      this.object = (T)var1;
      this.finishRunnable = var2;
   }

   public T getObject() {
      return this.object;
   }
}
