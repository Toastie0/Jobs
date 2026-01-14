package net.advancedplugins.jobs.impl.utils.evalex;

import java.math.MathContext;

public class ExpressionSettings {
   private MathContext mathContext;
   private int powerOperatorPrecedence;

   private ExpressionSettings() {
   }

   public ExpressionSettings(MathContext var1, int var2) {
      this.mathContext = var1;
      this.powerOperatorPrecedence = var2;
   }

   public MathContext getMathContext() {
      return this.mathContext;
   }

   public int getPowerOperatorPrecedence() {
      return this.powerOperatorPrecedence;
   }

   public static ExpressionSettings.Builder builder() {
      return new ExpressionSettings.Builder();
   }

   public static class Builder {
      private MathContext mathContext = MathContext.DECIMAL32;
      private int powerOperatorPrecedence = 40;

      public ExpressionSettings.Builder mathContext(MathContext var1) {
         this.mathContext = var1;
         return this;
      }

      public ExpressionSettings.Builder powerOperatorPrecedenceHigher() {
         this.powerOperatorPrecedence = 80;
         return this;
      }

      public ExpressionSettings.Builder powerOperatorPrecedence(int var1) {
         this.powerOperatorPrecedence = var1;
         return this;
      }

      public ExpressionSettings build() {
         return new ExpressionSettings(this.mathContext, this.powerOperatorPrecedence);
      }
   }
}
