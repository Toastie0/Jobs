package net.advancedplugins.jobs.impl.actions;

import java.math.BigDecimal;
import java.math.BigInteger;
import net.advancedplugins.jobs.impl.actions.objects.variable.ExecutableActionResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionExecution {
   private final Player player;
   private final String questType;
   private final ExecutableActionResult questResult;
   private final boolean canBeAsync;
   private final BigDecimal progress;
   private final boolean overrideUpdate;
   private Object user;

   public ActionExecution(Player var1, String var2, BigInteger var3, boolean var4, ExecutableActionResult var5) {
      this(var1, var2, new BigDecimal(var3), var4, var5, false);
   }

   public ActionExecution(Player var1, String var2, BigDecimal var3, boolean var4, ExecutableActionResult var5) {
      this(var1, var2, var3, var4, var5, false);
   }

   public ActionExecution(Player var1, String var2, BigDecimal var3, boolean var4, ExecutableActionResult var5, boolean var6) {
      this.player = var1;
      this.questType = var2;
      this.progress = var3;
      this.overrideUpdate = var4;
      this.questResult = var5;
      this.canBeAsync = var6;
   }

   public BigDecimal getDecimalProgress() {
      return this.progress;
   }

   public BigInteger getProgress() {
      return this.progress.toBigInteger();
   }

   public boolean shouldOverrideUpdate() {
      return this.overrideUpdate;
   }

   public boolean canBeAsync() {
      return this.canBeAsync || !Bukkit.isPrimaryThread();
   }

   @Override
   public String toString() {
      return "player="
         + this.player.getName()
         + ", questType='"
         + this.questType
         + "', progress="
         + this.progress.toString()
         + ", overrideUpdate="
         + this.overrideUpdate
         + ", questResult="
         + this.questResult.toString();
   }

   public Player getPlayer() {
      return this.player;
   }

   public String getQuestType() {
      return this.questType;
   }

   public ExecutableActionResult getQuestResult() {
      return this.questResult;
   }

   public void setUser(Object var1) {
      this.user = var1;
   }

   public Object getUser() {
      return this.user;
   }
}
