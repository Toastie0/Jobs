package net.advancedplugins.jobs.impl.utils.evalex;

import java.math.BigDecimal;
import java.util.List;

public class LazyIfNumber implements Expression.LazyNumber {
   private final List<Expression.LazyNumber> lazyParams;

   public LazyIfNumber(List<Expression.LazyNumber> var1) {
      this.lazyParams = var1;
   }

   @Override
   public BigDecimal eval() {
      BigDecimal var1 = this.lazyParams.get(0).eval();
      this.assertNotNull(var1);
      boolean var2 = var1.compareTo(BigDecimal.ZERO) != 0;
      return var2 ? this.lazyParams.get(1).eval() : this.lazyParams.get(2).eval();
   }

   @Override
   public String getString() {
      return this.lazyParams.get(0).getString();
   }

   private void assertNotNull(BigDecimal var1) {
      if (var1 == null) {
         throw new ArithmeticException("Operand may not be null");
      }
   }
}
