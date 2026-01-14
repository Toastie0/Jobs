package net.advancedplugins.jobs.impl.utils.data.cache;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletableFuture.AsynchronousCompletionTask;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class AsyncJob<V> implements Runnable, Comparable<AsyncJob>, AsynchronousCompletionTask {
   private final Integer priority;
   private final Long operationId;
   private final Supplier<V> supplier;
   private final CompletableFuture<V> future;

   @Override
   public void run() {
      try {
         if (!this.future.isDone()) {
            this.future.complete(this.supplier.get());
         }
      } catch (Throwable var2) {
         this.future.completeExceptionally(var2);
      }
   }

   public int compareTo(@NotNull AsyncJob var1) {
      int var2 = -this.priority.compareTo(var1.priority);
      return var2 != 0 ? var2 : this.operationId.compareTo(var1.operationId);
   }

   public AsyncJob(Integer var1, Long var2, Supplier<V> var3, CompletableFuture<V> var4) {
      this.priority = var1;
      this.operationId = var2;
      this.supplier = var3;
      this.future = var4;
   }

   public Integer getPriority() {
      return this.priority;
   }

   public Long getOperationId() {
      return this.operationId;
   }
}
