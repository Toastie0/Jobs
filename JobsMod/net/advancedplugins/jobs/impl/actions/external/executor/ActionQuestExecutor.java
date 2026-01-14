package net.advancedplugins.jobs.impl.actions.external.executor;

import java.math.BigInteger;
import java.util.function.UnaryOperator;
import net.advancedplugins.jobs.impl.actions.ActionExecutionBuilder;
import net.advancedplugins.jobs.impl.actions.containers.ExternalActionContainer;
import net.advancedplugins.jobs.impl.actions.objects.variable.ActionResult;
import net.advancedplugins.jobs.impl.utils.text.Replace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ActionQuestExecutor extends ExternalActionContainer {
   @Deprecated
   public ActionQuestExecutor(JavaPlugin var1, String var2) {
      super(var1, var2);
   }

   @Deprecated
   protected void execute(String var1, Player var2, int var3, UnaryOperator<ActionResult> var4, Replace var5, boolean var6) {
      this.execute(var1, var2, BigInteger.valueOf(var3), var4, var5, var6);
   }

   @Deprecated
   protected void execute(String var1, Player var2, int var3, UnaryOperator<ActionResult> var4, Replace var5) {
      this.execute(var1, var2, var3, var4, var5, false);
   }

   @Deprecated
   protected void execute(String var1, Player var2, UnaryOperator<ActionResult> var3, Replace var4) {
      this.execute(var1, var2, BigInteger.ONE, var3, var4, false);
   }

   @Deprecated
   protected void execute(String var1, Player var2, int var3, UnaryOperator<ActionResult> var4) {
      this.execute(var1, var2, var3, var4, var0 -> var0, false);
   }

   @Deprecated
   protected void execute(String var1, Player var2, UnaryOperator<ActionResult> var3) {
      this.execute(var1, var2, BigInteger.ONE, var3);
   }

   @Deprecated
   protected void execute(String var1, Player var2, BigInteger var3, UnaryOperator<ActionResult> var4, Replace var5, boolean var6) {
      ActionExecutionBuilder var7 = ActionExecutionBuilder.of(this, var1).player(var2).progress(var3).questResult(var4);
      if (var6) {
         var7.overrideUpdate();
      }

      var7.buildAndExecute();
   }

   @Deprecated
   protected void execute(String var1, Player var2, BigInteger var3, UnaryOperator<ActionResult> var4, Replace var5) {
      this.execute(var1, var2, var3, var4, var5, false);
   }

   @Deprecated
   protected void execute(String var1, Player var2, BigInteger var3, UnaryOperator<ActionResult> var4) {
      this.execute(var1, var2, var3, var4, var0 -> var0);
   }
}
