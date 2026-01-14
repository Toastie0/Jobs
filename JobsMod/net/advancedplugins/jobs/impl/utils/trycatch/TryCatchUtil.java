package net.advancedplugins.jobs.impl.utils.trycatch;

import java.util.function.Consumer;

public class TryCatchUtil {
   public static <T> T tryOrDefault(TryCatchUtil.ITryCatchWithReturn<T> var0, T var1, Consumer<Exception> var2) {
      try {
         return (T)var0.run();
      } catch (Exception var4) {
         if (var2 != null) {
            var2.accept(var4);
         } else {
            var4.printStackTrace();
         }

         return (T)var1;
      }
   }

   public static <T> T tryOrDefault(TryCatchUtil.ITryCatchWithReturn<T> var0, T var1) {
      return tryOrDefault(var0, (T)var1, null);
   }

   public static <T> T tryAndReturn(TryCatchUtil.ITryCatchWithReturn<T> var0) {
      return tryOrDefault(var0, null);
   }

   public static void tryRun(TryCatchUtil.ITryCatch var0, Consumer<Exception> var1) {
      tryOrDefault(() -> {
         var0.run();
         return null;
      }, null, var1);
   }

   public static void tryRun(TryCatchUtil.ITryCatch var0) {
      tryRun(var0, null);
   }

   @FunctionalInterface
   public interface ITryCatch {
      void run() throws Exception;
   }

   @FunctionalInterface
   public interface ITryCatchWithReturn<T> {
      T run() throws Exception;
   }
}
