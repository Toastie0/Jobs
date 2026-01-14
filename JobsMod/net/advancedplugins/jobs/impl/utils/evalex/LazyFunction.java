package net.advancedplugins.jobs.impl.utils.evalex;

import java.util.List;

public interface LazyFunction {
   String getName();

   int getNumParams();

   boolean numParamsVaries();

   boolean isBooleanFunction();

   Expression.LazyNumber lazyEval(List<Expression.LazyNumber> var1);
}
