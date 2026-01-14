package net.advancedplugins.jobs.impl.utils.collections;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class UniquePriorityQueue<E> extends PriorityQueue<E> {
   private final Set<E> set = new HashSet<>();

   public UniquePriorityQueue(Comparator<E> var1) {
      super(var1);
   }

   @Override
   public boolean offer(E var1) {
      return this.set.add((E)var1) ? super.offer((E)var1) : false;
   }

   @Override
   public boolean add(E var1) {
      return this.offer((E)var1);
   }

   @Override
   public E poll() {
      Object var1 = super.poll();
      if (var1 != null) {
         this.set.remove(var1);
      }

      return (E)var1;
   }

   @Override
   public boolean remove(Object var1) {
      boolean var2 = super.remove(var1);
      if (var2) {
         this.set.remove(var1);
      }

      return var2;
   }
}
