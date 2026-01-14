package net.advancedplugins.jobs.impl.utils.evalex;

public abstract class AbstractLazyOperator implements LazyOperator {
   protected String oper;
   protected int precedence;
   protected boolean leftAssoc;
   protected boolean booleanOperator = false;

   protected AbstractLazyOperator(String var1, int var2, boolean var3, boolean var4) {
      this.oper = var1;
      this.precedence = var2;
      this.leftAssoc = var3;
      this.booleanOperator = var4;
   }

   protected AbstractLazyOperator(String var1, int var2, boolean var3) {
      this.oper = var1;
      this.precedence = var2;
      this.leftAssoc = var3;
   }

   @Override
   public String getOper() {
      return this.oper;
   }

   @Override
   public int getPrecedence() {
      return this.precedence;
   }

   @Override
   public boolean isLeftAssoc() {
      return this.leftAssoc;
   }

   @Override
   public boolean isBooleanOperator() {
      return this.booleanOperator;
   }
}
