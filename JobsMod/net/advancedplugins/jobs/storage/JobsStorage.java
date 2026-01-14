package net.advancedplugins.jobs.storage;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.jobs.JobsResetHandler;
import net.advancedplugins.jobs.objects.job.Job;
import net.advancedplugins.jobs.validator.Validator;
import net.advancedplugins.simplespigot.storage.storage.Storage;
import net.advancedplugins.simplespigot.storage.storage.load.Deserializer;
import net.advancedplugins.simplespigot.storage.storage.load.Serializer;

public class JobsStorage extends Storage<JobsResetHandler> {
   private final Core plugin;

   public JobsStorage(Core var1) {
      super(var1, var1x -> var1x.create(var1.getConfigStore().commons().get("storage-type"), var0x -> var0x.resolve("jobs-storage"), "", "jobs"));
      this.plugin = var1;
   }

   @Override
   public Serializer<JobsResetHandler> serializer() {
      return (var0, var1, var2) -> {
         var1.addProperty("current-free-jobs", var2.toJson(var0.getCurrentFreeJobs().stream().map(Job::getId).collect(Collectors.toList())));
         var1.addProperty("current-premium-jobs", var2.toJson(var0.getCurrentPremiumJobs().stream().map(Job::getId).collect(Collectors.toList())));
         return var1;
      };
   }

   @Override
   public Deserializer<JobsResetHandler> deserializer() {
      return (var1, var2) -> {
         List var3 = (List)var2.fromJson(
            var1.get("current-free-jobs").getAsString(), TypeToken.getParameterized(List.class, new Type[]{String.class}).getType()
         );
         List var4 = (List)var2.fromJson(
            var1.get("current-premium-jobs").getAsString(), TypeToken.getParameterized(List.class, new Type[]{String.class}).getType()
         );
         List var5 = var3.stream().filter(Validator::validateJob).map(var1x -> this.plugin.getJobCache().getJob(var1x)).collect(Collectors.toList());
         List var6 = var4.stream().filter(Validator::validateJob).map(var1x -> this.plugin.getJobCache().getJob(var1x)).collect(Collectors.toList());
         return new JobsResetHandler(this.plugin, new LinkedList<>(var5), new LinkedList<>(var6));
      };
   }
}
