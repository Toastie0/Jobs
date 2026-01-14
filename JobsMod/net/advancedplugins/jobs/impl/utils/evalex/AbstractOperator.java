package net.advancedplugins.jobs.impl.utils.evalex;

import java.math.BigDecimal;

public abstract class AbstractOperator extends AbstractLazyOperator implements Operator {
   protected AbstractOperator(String var1, int var2, boolean var3, boolean var4) {
      super(var1, var2, var3, var4);
   }

   protected AbstractOperator(String var1, int var2, boolean var3) {
      super(var1, var2, var3);
   }

   @Override
   public Expression.LazyNumber eval(final Expression.LazyNumber var1, final Expression.LazyNumber var2) {
      return new Expression.LazyNumber() {
         @Override
         public BigDecimal eval() {
            return AbstractOperator.this.eval(var1.eval(), var2.eval());
         }

         @Override
         public String getString() {
            return String.valueOf(AbstractOperator.this.eval(var1.eval(), var2.eval()));
         }
      };
   }
}
