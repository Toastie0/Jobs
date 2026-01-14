package net.advancedplugins.jobs.impl.utils.evalex;

import java.util.Locale;

public abstract class AbstractLazyFunction implements LazyFunction {
   protected String name;
   protected int numParams;
   protected boolean booleanFunction;

   protected AbstractLazyFunction(String var1, int var2, boolean var3) {
      this.name = var1.toUpperCase(Locale.ROOT);
      this.numParams = var2;
      this.booleanFunction = var3;
   }

   protected AbstractLazyFunction(String var1, int var2) {
      this(var1, var2, false);
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public int getNumParams() {
      return this.numParams;
   }

   @Override
   public boolean numParamsVaries() {
      return this.numParams < 0;
   }

   @Override
   public boolean isBooleanFunction() {
      return this.booleanFunction;
   }
}
