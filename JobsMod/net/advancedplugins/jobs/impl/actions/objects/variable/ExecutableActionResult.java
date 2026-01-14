package net.advancedplugins.jobs.impl.actions.objects.variable;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Predicate;
import me.clip.placeholderapi.PlaceholderAPI;
import net.advancedplugins.jobs.impl.actions.ActionRegistry;
import net.advancedplugins.jobs.impl.actions.objects.root.ActionRoot;
import net.advancedplugins.jobs.impl.actions.objects.root.VariableRoot;
import net.advancedplugins.jobs.impl.utils.ASManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ExecutableActionResult implements ActionResult {
   private ActionRoot root;
   private Map<String, ActionRoot> subRoots = Maps.newHashMap();

   public ExecutableActionResult() {
      this.none();
   }

   public ExecutableActionResult rootFrom(Object var1) {
      if (var1 != null) {
         this.root = ActionRoot.from(var1);
      }

      return this;
   }

   public ExecutableActionResult root(String var1) {
      return this.rootFrom(var1);
   }

   @Override
   public ActionResult root(Material var1) {
      return this.rootFrom(var1);
   }

   @Override
   public ActionResult root(ItemStack var1) {
      return this.rootFrom(var1);
   }

   @Override
   public ActionResult root(Entity var1) {
      return this.rootFrom(var1);
   }

   public ExecutableActionResult root(Block var1) {
      return this.rootFrom(var1);
   }

   public ExecutableActionResult root(Predicate<Object> var1) {
      return this.rootFrom(var1);
   }

   public ExecutableActionResult none() {
      return this.rootFrom(ActionRegistry.NONE_ROOT);
   }

   public ExecutableActionResult subRootFrom(String var1, Object var2) {
      if (var2 != null) {
         this.subRoots.put(var1, ActionRoot.from(var2));
      }

      return this;
   }

   public ExecutableActionResult subRoot(String var1, String var2) {
      return this.subRootFrom(var1, var2);
   }

   @Override
   public ActionResult subRoot(String var1, ItemStack var2) {
      return this.subRootFrom(var1, var2);
   }

   @Override
   public ActionResult subRoot(String var1, Entity var2) {
      return this.subRootFrom(var1, var2);
   }

   @Override
   public ActionResult subRoot(String var1, Block var2) {
      return this.subRootFrom(var1, var2);
   }

   @Override
   public ActionResult subRoot(String var1, Material var2) {
      return this.subRootFrom(var1, var2);
   }

   @Override
   public ActionResult subRoot(String var1, Predicate<Object> var2) {
      return this.subRootFrom(var1, var2);
   }

   @Override
   public ActionResult subRoot(ItemStack var1) {
      return this.subRootFrom("item", var1);
   }

   @Override
   public double isEligible(Player var1, Variable var2) {
      double var3 = this.areSubRootsValid(var1, var2);
      return var3 == 0.0 ? 0.0 : var3 * this.areVariableRootsValid(var2.getRoots(), this.root, var2.getBlacklists().contains("root"));
   }

   public boolean areConditionsValid(Player var1, Map<Variable.ConditionType, List<String>> var2) {
      if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
         return true;
      } else {
         boolean var3 = ((List)var2.get(Variable.ConditionType.OR)).isEmpty();

         for (String var5 : (List)var2.get(Variable.ConditionType.OR)) {
            if (ASManager.parseCondition(PlaceholderAPI.setPlaceholders(var1, var5))) {
               var3 = true;
               break;
            }
         }

         boolean var7 = true;

         for (String var6 : (List)var2.get(Variable.ConditionType.AND)) {
            if (!ASManager.parseCondition(PlaceholderAPI.setPlaceholders(var1, var6))) {
               var7 = false;
               break;
            }
         }

         return var3 && var7;
      }
   }

   private double areSubRootsValid(Player var1, Variable var2) {
      if (!this.areConditionsValid(var1, var2.getConditions())) {
         return 0.0;
      } else {
         Map var3 = var2.getSubRoots();
         double var4 = 1.0;
         var4 *= this.isHeldItemSubRootValid(var1, var3, var2.getBlacklists());
         return var4 * this.areCustomSubRootsValid(var3, var2.getBlacklists());
      }
   }

   private double isHeldItemSubRootValid(Player var1, Map<String, List<VariableRoot>> var2, List<String> var3) {
      if (!var2.containsKey("holding.item")) {
         return 1.0;
      } else {
         ItemStack var4 = var1.getInventory().getItemInMainHand();
         ItemMeta var5 = var4.getItemMeta();
         double var6 = this.areVariableRootsValid((List<VariableRoot>)var2.get("holding.item"), ActionRoot.from(var4), var3.contains("holding.item"));
         double var8 = var2.containsKey("holding.name") && Objects.nonNull(var5)
            ? this.areVariableRootsValid((List<VariableRoot>)var2.get("holding.name"), ActionRoot.from(var5.getDisplayName()), var3.contains("holding.name"))
            : 1.0;
         double var10 = var2.containsKey("holding.model") && Objects.nonNull(var5)
            ? this.areVariableRootsValid(
               (List<VariableRoot>)var2.get("holding.model"),
               ActionRoot.from(var5.hasCustomModelData() ? var5.getCustomModelData() : 0),
               var3.contains("holding.model")
            )
            : 1.0;
         double var12 = !var2.containsKey("holding.amount")
            ? 1.0
            : this.areVariableRootsValid((List<VariableRoot>)var2.get("holding.amount"), ActionRoot.from(var4.getAmount()), var3.contains("holding.amount"));
         return var6 * var8 * var10 * var12;
      }
   }

   private double areCustomSubRootsValid(Map<String, List<VariableRoot>> var1, List<String> var2) {
      double var3 = 1.0;

      for (Entry var6 : this.subRoots.entrySet()) {
         String var7 = (String)var6.getKey();
         if (var1.containsKey(var7)) {
            double var8 = this.areVariableRootsValid((List<VariableRoot>)var1.get(var7), (ActionRoot)var6.getValue(), var2.contains(var7));
            if (var8 == 0.0) {
               return 0.0;
            }

            var3 *= var3;
         }
      }

      return var3;
   }

   private double areVariableRootsValid(List<VariableRoot> var1, ActionRoot var2, boolean var3) {
      double var4 = var3 ? 1.0 : 0.0;

      for (VariableRoot var7 : var1) {
         double var8 = var7.isValid(var2);
         if (var8 > 0.0) {
            if (var3) {
               return 0.0;
            }

            var4 = var8;
            break;
         }
      }

      return var4;
   }

   @Override
   public String toString() {
      return "QuestResult{root=" + this.root + ", subRoots=" + this.subRoots + "}";
   }

   @Override
   public String getEffectiveRoot() {
      return this.root.toString();
   }

   public ActionRoot getRoot() {
      return this.root;
   }

   public Map<String, ActionRoot> getSubRoots() {
      return this.subRoots;
   }
}
