package net.advancedplugins.jobs.impl.actions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javax.annotation.CheckReturnValue;
import net.advancedplugins.jobs.impl.actions.containers.ActionContainer;
import net.advancedplugins.jobs.impl.actions.containers.ExternalActionContainer;
import net.advancedplugins.jobs.impl.actions.objects.variable.ActionResult;
import net.advancedplugins.jobs.impl.actions.objects.variable.ExecutableActionResult;
import net.advancedplugins.jobs.impl.utils.ASManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ActionExecutionBuilder {
   private final String questType;
   private Player player;
   private BigDecimal progress;
   private boolean overrideUpdate;
   private boolean canBeAsync;
   private ExecutableActionResult questResult;
   private final ActionContainer container;

   public ActionExecutionBuilder(ActionContainer var1, String var2) {
      this.questType = var2;
      this.container = var1;
      this.canBeAsync = false;
   }

   public void buildAndExecute() {
      if (this.player != null) {
         ActionExecution var1 = this.build();
         ActionRegistry.getRegistry().getReader().onAction(var1);
      }
   }

   @CheckReturnValue
   public static ActionExecutionBuilder of(ActionContainer var0, String var1) {
      String var2;
      if (var0 instanceof ExternalActionContainer) {
         var2 = ((ExternalActionContainer)var0).getPrefix().concat(var1);
      } else {
         var2 = var1;
      }

      return new ActionExecutionBuilder(var0, var2);
   }

   public ActionExecution build() {
      String var1 = "QuestExecution Build -> %s must be set.";
      if (this.player == null) {
         throw new IllegalStateException(String.format(var1, "Player"));
      } else if (this.questType == null || this.questType.isEmpty()) {
         throw new IllegalStateException(String.format(var1, "Quest type"));
      } else if (this.progress == null) {
         throw new IllegalStateException(String.format(var1, "Progress"));
      } else {
         if (this.questResult == null) {
            this.questResult = new ExecutableActionResult().none();
         }

         return new ActionExecution(this.player, this.questType, this.progress, this.overrideUpdate, this.questResult, this.canBeAsync);
      }
   }

   public ActionExecutionBuilder player(Player var1) {
      this.player = var1;
      return this;
   }

   @Deprecated
   public ActionExecutionBuilder questResult(UnaryOperator<ActionResult> var1) {
      this.questResult = var1.apply(new ExecutableActionResult());
      return this;
   }

   public ActionExecutionBuilder rootFrom(Object var1) {
      ASManager.notNull(var1, "Quest execution root");
      if (this.questResult == null) {
         this.questResult = new ExecutableActionResult();
      }

      this.questResult.rootFrom(var1);
      return this;
   }

   public ActionExecutionBuilder root(String var1) {
      if (var1 == null) {
         var1 = "null";
      }

      return this.rootFrom(var1);
   }

   public ActionExecutionBuilder root(Block var1) {
      return this.rootFrom(var1);
   }

   public ActionExecutionBuilder root(ItemStack var1) {
      return this.rootFrom(var1);
   }

   public ActionExecutionBuilder root(Entity var1) {
      return this.rootFrom(var1);
   }

   public ActionExecutionBuilder root(Material var1) {
      return this.rootFrom(var1);
   }

   public ActionExecutionBuilder root(Predicate<Object> var1) {
      return this.rootFrom(var1);
   }

   public ActionExecutionBuilder subRootFrom(String var1, Object var2) {
      if (this.questResult == null) {
         this.questResult = new ExecutableActionResult();
      }

      this.questResult.subRootFrom(var1, var2);
      return this;
   }

   public ActionExecutionBuilder subRoot(String var1, String var2) {
      if (var2 == null) {
         var2 = "null";
      }

      return this.subRootFrom(var1, var2);
   }

   public ActionExecutionBuilder subRoot(String var1, boolean var2) {
      return this.subRootFrom(var1, String.valueOf(var2));
   }

   public ActionExecutionBuilder subRoot(String var1, Material var2) {
      return this.subRootFrom(var1, var2);
   }

   public ActionExecutionBuilder subRoot(String var1, ItemStack var2) {
      return this.subRootFrom(var1, var2);
   }

   public ActionExecutionBuilder subRoot(String var1, Block var2) {
      return this.subRootFrom(var1, var2);
   }

   public ActionExecutionBuilder subRoot(String var1, Entity var2) {
      return this.subRootFrom(var1, var2);
   }

   public ActionExecutionBuilder subRoot(String var1, Predicate<Object> var2) {
      return this.subRootFrom(var1, var2);
   }

   public ActionExecutionBuilder subRoot(ItemStack var1) {
      return this.subRootFrom("item", var1);
   }

   public ActionExecutionBuilder progressSingle() {
      return this.progress(BigInteger.ONE);
   }

   public ActionExecutionBuilder progress(BigDecimal var1) {
      ASManager.notNull(var1, "Quest execution progress");
      this.progress = var1;
      return this;
   }

   public ActionExecutionBuilder progress(BigInteger var1) {
      ASManager.notNull(var1, "Quest execution progress");
      this.progress = new BigDecimal(var1);
      return this;
   }

   public ActionExecutionBuilder progress(int var1) {
      ASManager.notNull(var1, "Quest execution progress");
      this.progress = BigDecimal.valueOf((long)var1);
      return this;
   }

   public ActionExecutionBuilder progress(double var1) {
      ASManager.notNull(var1, "Quest execution progress");
      this.progress = BigDecimal.valueOf(var1);
      return this;
   }

   public ActionExecutionBuilder overrideUpdate() {
      this.overrideUpdate = true;
      return this;
   }

   public ActionExecutionBuilder canBeAsync() {
      return this.canBeAsync(true);
   }

   public ActionExecutionBuilder canBeAsync(boolean var1) {
      this.canBeAsync = var1;
      return this;
   }
}
