package net.advancedplugins.jobs.impl.utils.evalex;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFunction extends AbstractLazyFunction implements Function {
   protected AbstractFunction(String var1, int var2) {
      super(var1, var2);
   }

   protected AbstractFunction(String var1, int var2, boolean var3) {
      super(var1, var2, var3);
   }

   @Override
   public Expression.LazyNumber lazyEval(final List<Expression.LazyNumber> var1) {
      return new Expression.LazyNumber() {
         private List<BigDecimal> params;

         @Override
         public BigDecimal eval() {
            return AbstractFunction.this.eval(this.getParams());
         }

         @Override
         public String getString() {
            return String.valueOf(AbstractFunction.this.eval(this.getParams()));
         }

         private List<BigDecimal> getParams() {
            if (this.params == null) {
               this.params = new ArrayList<>();

               for (Expression.LazyNumber var2 : var1) {
                  this.params.add(var2.eval());
               }
            }

            return this.params;
         }
      };
   }
}
