package net.advancedplugins.jobs.impl.utils.evalex;

public interface LazyOperator {
   String getOper();

   int getPrecedence();

   boolean isLeftAssoc();

   boolean isBooleanOperator();

   Expression.LazyNumber eval(Expression.LazyNumber var1, Expression.LazyNumber var2);
}
