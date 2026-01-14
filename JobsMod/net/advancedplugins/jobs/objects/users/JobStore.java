package net.advancedplugins.jobs.objects.users;

import java.util.concurrent.ConcurrentHashMap;

public class JobStore {
   private final ConcurrentHashMap<String, UserJobInfo> jobs = new ConcurrentHashMap<>();

   public ConcurrentHashMap<String, UserJobInfo> asMap() {
      return this.jobs;
   }

   public void clear() {
      this.jobs.clear();
   }
}
