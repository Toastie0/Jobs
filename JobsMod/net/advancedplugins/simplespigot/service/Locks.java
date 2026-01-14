package net.advancedplugins.simplespigot.service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;

public class Locks {
   public static ReentrantLock newReentrantLock() {
      return new ReentrantLock();
   }

   public static ReentrantLock newReentrantLock(boolean var0) {
      return new ReentrantLock(var0);
   }

   public static ReentrantReadWriteLock newReadWriteLock() {
      return new ReentrantReadWriteLock();
   }

   public static StampedLock newStampedLock() {
      return new StampedLock();
   }

   public static <T> T supplySafety(Lock var0, Supplier<T> var1) {
      var0.lock();

      Object var2;
      try {
         var2 = var1.get();
      } finally {
         var0.unlock();
      }

      return (T)var2;
   }

   public static void safety(Lock var0, Runnable var1) {
      var0.lock();

      try {
         var1.run();
      } finally {
         var0.unlock();
      }
   }
}
