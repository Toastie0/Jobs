package net.advancedplugins.jobs.objects.reward;

import com.google.common.collect.Multiset;
import java.util.Map;
import net.advancedplugins.jobs.Core;
import net.advancedplugins.jobs.controller.BoostersController;
import net.advancedplugins.jobs.objects.IBoostable;
import org.bukkit.entity.Player;

public abstract class Reward<T> implements IBoostable {
   private final String id;
   private final String name;
   private final Map<String, String> variables;
   protected Multiset<T> set;

   public Reward(String var1, String var2, Map<String, String> var3, Multiset<T> var4) {
      this.id = var1;
      this.name = var2;
      this.variables = var3;
      this.set = var4;
   }

   public String getName(int var1, Player var2, String var3, double var4) {
      return var2 == null ? this.name : this.fillVariables(this.name, var1, var2, var3, var4);
   }

   public abstract void reward(Player var1, String var2, int var3, String var4, double var5);

   public String fillVariables(String var1, int var2, Player var3, String var4, double var5) {
      return this.fillVariables(var1, "", var2, var3, var4, var5);
   }

   public String fillVariables(String var1, String var2, int var3, Player var4, String var5, double var6) {
      return StringVariable.fillVariables(
         this.variables,
         var1,
         var2,
         var6x -> var6x.set("level", var3)
            .set("job", var5)
            .set(
               "booster",
               Core.getInstance()
                  .getBoostersController()
                  .calculateFinalBooster(
                     var4.getUniqueId(), BoostersController.BoosterType.REWARDS, new JobReward(Core.getInstance().getJobCache().getJob(var5), this)
                  )
            )
            .set("progress", var6)
            .tryAddPapi(var4)
      );
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Map<String, String> getVariables() {
      return this.variables;
   }

   public Multiset<T> getSet() {
      return this.set;
   }

   public void setSet(Multiset<T> var1) {
      this.set = var1;
   }
}
