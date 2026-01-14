package net.advancedplugins.jobs.impl.utils.evalex;

import java.math.BigDecimal;

public interface Operator extends LazyOperator {
   BigDecimal eval(BigDecimal var1, BigDecimal var2);
}
