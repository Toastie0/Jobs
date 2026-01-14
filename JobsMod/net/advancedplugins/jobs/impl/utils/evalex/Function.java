package net.advancedplugins.jobs.impl.utils.evalex;

import java.math.BigDecimal;
import java.util.List;

public interface Function extends LazyFunction {
   BigDecimal eval(List<BigDecimal> var1);
}
