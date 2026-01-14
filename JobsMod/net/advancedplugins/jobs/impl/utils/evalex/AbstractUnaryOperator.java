package net.advancedplugins.jobs.impl.utils.evalex;

import java.math.BigDecimal;

public abstract class AbstractUnaryOperator extends AbstractOperator {
   protected AbstractUnaryOperator(String var1, int var2, boolean var3) {
      super(var1, var2, var3);
   }

   @Override
   public Expression.LazyNumber eval(final Expression.LazyNumber var1, Expression.LazyNumber var2) {
      if (var2 != null) {
         throw new Expression.ExpressionException("Did not expect a second parameter for unary operator");
      } else {
         return new Expression.LazyNumber() {
            @Override
            public String getString() {
               return String.valueOf(AbstractUnaryOperator.this.evalUnary(var1.eval()));
            }

            @Override
            public BigDecimal eval() {
               return AbstractUnaryOperator.this.evalUnary(var1.eval());
            }
         };
      }
   }

   @Override
   public BigDecimal eval(BigDecimal var1, BigDecimal var2) {
      if (var2 != null) {
         throw new Expression.ExpressionException("Did not expect a second parameter for unary operator");
      } else {
         return this.evalUnary(var1);
      }
   }

   public abstract BigDecimal evalUnary(BigDecimal var1);
}
