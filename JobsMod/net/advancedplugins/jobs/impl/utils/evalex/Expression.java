package net.advancedplugins.jobs.impl.utils.evalex;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

public class Expression {
   public static final int OPERATOR_PRECEDENCE_UNARY = 60;
   public static final int OPERATOR_PRECEDENCE_EQUALITY = 7;
   public static final int OPERATOR_PRECEDENCE_COMPARISON = 10;
   public static final int OPERATOR_PRECEDENCE_OR = 2;
   public static final int OPERATOR_PRECEDENCE_AND = 4;
   public static final int OPERATOR_PRECEDENCE_POWER = 40;
   public static final int OPERATOR_PRECEDENCE_POWER_HIGHER = 80;
   public static final int OPERATOR_PRECEDENCE_MULTIPLICATIVE = 30;
   public static final int OPERATOR_PRECEDENCE_ADDITIVE = 20;
   public static final BigDecimal PI = new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679");
   public static final BigDecimal e = new BigDecimal("2.71828182845904523536028747135266249775724709369995957496696762772407663");
   public static final String MISSING_PARAMETERS_FOR_OPERATOR = "Missing parameter(s) for operator ";
   private MathContext mc = null;
   private int powerOperatorPrecedence = 40;
   private String firstVarChars = "_";
   private String varChars = "_";
   private final String originalExpression;
   private String expressionString = null;
   private List<Expression.Token> rpn = null;
   protected Map<String, LazyOperator> operators = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
   protected Map<String, net.advancedplugins.jobs.impl.utils.evalex.LazyFunction> functions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
   protected Map<String, Expression.LazyNumber> variables = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
   private static final char DECIMAL_SEPARATOR = '.';
   private static final char MINUS_SIGN = '-';
   private static final Expression.LazyNumber PARAMS_START = new Expression.LazyNumber() {
      @Override
      public BigDecimal eval() {
         return null;
      }

      @Override
      public String getString() {
         return null;
      }
   };

   protected Expression.LazyNumber createLazyNumber(final BigDecimal var1) {
      return new Expression.LazyNumber() {
         @Override
         public String getString() {
            return var1.toPlainString();
         }

         @Override
         public BigDecimal eval() {
            return var1;
         }
      };
   }

   public Expression(String var1) {
      this(var1, MathContext.DECIMAL32);
   }

   public Expression(String var1, MathContext var2) {
      this(var1, ExpressionSettings.builder().mathContext(var2).build());
   }

   public Expression(String var1, ExpressionSettings var2) {
      this.mc = var2.getMathContext();
      this.powerOperatorPrecedence = var2.getPowerOperatorPrecedence();
      this.expressionString = var1;
      this.originalExpression = var1;
      this.addOperator(new Expression.Operator("+", 20, true) {
         @Override
         public BigDecimal eval(BigDecimal var1, BigDecimal var2x) {
            Expression.this.assertNotNull(var1, var2x);
            return var1.add(var2x, Expression.this.mc);
         }
      });
      this.addOperator(new Expression.Operator("-", 20, true) {
         @Override
         public BigDecimal eval(BigDecimal var1, BigDecimal var2) {
            Expression.this.assertNotNull(var1, var2);
            return var1.subtract(var2, Expression.this.mc);
         }
      });
      this.addOperator(new Expression.Operator("*", 30, true) {
         @Override
         public BigDecimal eval(BigDecimal var1, BigDecimal var2) {
            Expression.this.assertNotNull(var1, var2);
            return var1.multiply(var2, Expression.this.mc);
         }
      });
      this.addOperator(new Expression.Operator("/", 30, true) {
         @Override
         public BigDecimal eval(BigDecimal var1, BigDecimal var2) {
            Expression.this.assertNotNull(var1, var2);
            return var1.divide(var2, Expression.this.mc);
         }
      });
      this.addOperator(new Expression.Operator("%", 30, true) {
         @Override
         public BigDecimal eval(BigDecimal var1, BigDecimal var2) {
            Expression.this.assertNotNull(var1, var2);
            return var1.remainder(var2, Expression.this.mc);
         }
      });
      this.addOperator(new Expression.Operator("^", this.powerOperatorPrecedence, false) {
         @Override
         public BigDecimal eval(BigDecimal var1, BigDecimal var2) {
            Expression.this.assertNotNull(var1, var2);
            int var3 = var2.signum();
            double var4 = var1.doubleValue();
            var2 = var2.multiply(new BigDecimal(var3));
            BigDecimal var6 = var2.remainder(BigDecimal.ONE);
            BigDecimal var7 = var2.subtract(var6);
            BigDecimal var8 = var1.pow(var7.intValueExact(), Expression.this.mc);
            BigDecimal var9 = BigDecimal.valueOf(Math.pow(var4, var6.doubleValue()));
            BigDecimal var10 = var8.multiply(var9, Expression.this.mc);
            if (var3 == -1) {
               var10 = BigDecimal.ONE.divide(var10, Expression.this.mc.getPrecision(), RoundingMode.HALF_UP);
            }

            return var10;
         }
      });
      this.addOperator(new Expression.Operator("&&", 4, false, true) {
         @Override
         public BigDecimal eval(BigDecimal var1, BigDecimal var2) {
            Expression.this.assertNotNull(var1, var2);
            boolean var3 = var1.compareTo(BigDecimal.ZERO) != 0;
            if (!var3) {
               return BigDecimal.ZERO;
            } else {
               boolean var4 = var2.compareTo(BigDecimal.ZERO) != 0;
               return var4 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
         }
      });
      this.addOperator(new Expression.Operator("||", 2, false, true) {
         @Override
         public BigDecimal eval(BigDecimal var1, BigDecimal var2) {
            Expression.this.assertNotNull(var1, var2);
            boolean var3 = var1.compareTo(BigDecimal.ZERO) != 0;
            if (var3) {
               return BigDecimal.ONE;
            } else {
               boolean var4 = var2.compareTo(BigDecimal.ZERO) != 0;
               return var4 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
         }
      });
      this.addOperator(new Expression.Operator(">", 10, false, true) {
         @Override
         public BigDecimal eval(BigDecimal var1, BigDecimal var2) {
            Expression.this.assertNotNull(var1, var2);
            return var1.compareTo(var2) > 0 ? BigDecimal.ONE : BigDecimal.ZERO;
         }
      });
      this.addOperator(new Expression.Operator(">=", 10, false, true) {
         @Override
         public BigDecimal eval(BigDecimal var1, BigDecimal var2) {
            Expression.this.assertNotNull(var1, var2);
            return var1.compareTo(var2) >= 0 ? BigDecimal.ONE : BigDecimal.ZERO;
         }
      });
      this.addOperator(new Expression.Operator("<", 10, false, true) {
         @Override
         public BigDecimal eval(BigDecimal var1, BigDecimal var2) {
            Expression.this.assertNotNull(var1, var2);
            return var1.compareTo(var2) < 0 ? BigDecimal.ONE : BigDecimal.ZERO;
         }
      });
      this.addOperator(new Expression.Operator("<=", 10, false, true) {
         @Override
         public BigDecimal eval(BigDecimal var1, BigDecimal var2) {
            Expression.this.assertNotNull(var1, var2);
            return var1.compareTo(var2) <= 0 ? BigDecimal.ONE : BigDecimal.ZERO;
         }
      });
      this.addOperator(new Expression.Operator("=", 7, false, true) {
         @Override
         public BigDecimal eval(BigDecimal var1, BigDecimal var2) {
            if (var1 == var2) {
               return BigDecimal.ONE;
            } else if (var1 != null && var2 != null) {
               return var1.compareTo(var2) == 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            } else {
               return BigDecimal.ZERO;
            }
         }
      });
      this.addOperator(new Expression.Operator("==", 7, false, true) {
         @Override
         public BigDecimal eval(BigDecimal var1, BigDecimal var2) {
            return ((Expression.Operator)Expression.this.operators.get("=")).eval(var1, var2);
         }
      });
      this.addOperator(new Expression.Operator("!=", 7, false, true) {
         @Override
         public BigDecimal eval(BigDecimal var1, BigDecimal var2) {
            if (var1 == var2) {
               return BigDecimal.ZERO;
            } else if (var1 != null && var2 != null) {
               return var1.compareTo(var2) != 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            } else {
               return BigDecimal.ONE;
            }
         }
      });
      this.addOperator(new Expression.Operator("<>", 7, false, true) {
         @Override
         public BigDecimal eval(BigDecimal var1, BigDecimal var2) {
            Expression.this.assertNotNull(var1, var2);
            return ((Expression.Operator)Expression.this.operators.get("!=")).eval(var1, var2);
         }
      });
      this.addOperator(new Expression.UnaryOperator("-", 60, false) {
         @Override
         public BigDecimal evalUnary(BigDecimal var1) {
            return var1.multiply(new BigDecimal(-1));
         }
      });
      this.addOperator(new Expression.UnaryOperator("+", 60, false) {
         @Override
         public BigDecimal evalUnary(BigDecimal var1) {
            return var1.multiply(BigDecimal.ONE);
         }
      });
      this.addFunction(new Expression.Function("FACT", 1, false) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            int var2x = ((BigDecimal)var1.get(0)).intValue();
            BigDecimal var3 = BigDecimal.ONE;

            for (int var4 = 1; var4 <= var2x; var4++) {
               var3 = var3.multiply(new BigDecimal(var4));
            }

            return var3;
         }
      });
      this.addFunction(new Expression.Function("NOT", 1, true) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            boolean var2 = ((BigDecimal)var1.get(0)).compareTo(BigDecimal.ZERO) == 0;
            return var2 ? BigDecimal.ONE : BigDecimal.ZERO;
         }
      });
      this.addLazyFunction(new Expression.LazyFunction("IF", 3) {
         @Override
         public Expression.LazyNumber lazyEval(List<Expression.LazyNumber> var1) {
            return new LazyIfNumber(var1);
         }
      });
      this.addFunction(new Expression.Function("RANDOM", 0) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            double var2 = Math.random();
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("SINR", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.sin(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("COSR", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.cos(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("TANR", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.tan(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("COTR", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = 1.0;
            double var4 = Math.tan(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2 / var4, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("SECR", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = 1.0;
            double var4 = Math.cos(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2 / var4, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("CSCR", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = 1.0;
            double var4 = Math.sin(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2 / var4, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("SIN", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.sin(Math.toRadians(((BigDecimal)var1.get(0)).doubleValue()));
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("COS", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.cos(Math.toRadians(((BigDecimal)var1.get(0)).doubleValue()));
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("TAN", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.tan(Math.toRadians(((BigDecimal)var1.get(0)).doubleValue()));
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("COT", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = 1.0;
            double var4 = Math.tan(Math.toRadians(((BigDecimal)var1.get(0)).doubleValue()));
            return new BigDecimal(var2 / var4, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("SEC", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = 1.0;
            double var4 = Math.cos(Math.toRadians(((BigDecimal)var1.get(0)).doubleValue()));
            return new BigDecimal(var2 / var4, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("CSC", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = 1.0;
            double var4 = Math.sin(Math.toRadians(((BigDecimal)var1.get(0)).doubleValue()));
            return new BigDecimal(var2 / var4, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("ASINR", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.asin(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("ACOSR", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.acos(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("ATANR", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.atan(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("ACOTR", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            if (((BigDecimal)var1.get(0)).doubleValue() == 0.0) {
               throw new Expression.ExpressionException("Number must not be 0");
            } else {
               double var2 = Math.atan(1.0 / ((BigDecimal)var1.get(0)).doubleValue());
               return new BigDecimal(var2, Expression.this.mc);
            }
         }
      });
      this.addFunction(new Expression.Function("ATAN2R", 2) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0), (BigDecimal)var1.get(1));
            double var2 = Math.atan2(((BigDecimal)var1.get(0)).doubleValue(), ((BigDecimal)var1.get(1)).doubleValue());
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("ASIN", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.toDegrees(Math.asin(((BigDecimal)var1.get(0)).doubleValue()));
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("ACOS", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.toDegrees(Math.acos(((BigDecimal)var1.get(0)).doubleValue()));
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("ATAN", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.toDegrees(Math.atan(((BigDecimal)var1.get(0)).doubleValue()));
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("ACOT", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            if (((BigDecimal)var1.get(0)).doubleValue() == 0.0) {
               throw new Expression.ExpressionException("Number must not be 0");
            } else {
               double var2 = Math.toDegrees(Math.atan(1.0 / ((BigDecimal)var1.get(0)).doubleValue()));
               return new BigDecimal(var2, Expression.this.mc);
            }
         }
      });
      this.addFunction(new Expression.Function("ATAN2", 2) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0), (BigDecimal)var1.get(1));
            double var2 = Math.toDegrees(Math.atan2(((BigDecimal)var1.get(0)).doubleValue(), ((BigDecimal)var1.get(1)).doubleValue()));
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("SINH", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.sinh(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("COSH", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.cosh(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("TANH", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.tanh(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("SECH", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = 1.0;
            double var4 = Math.cosh(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2 / var4, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("CSCH", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = 1.0;
            double var4 = Math.sinh(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2 / var4, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("COTH", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = 1.0;
            double var4 = Math.tanh(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2 / var4, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("ASINH", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.log(((BigDecimal)var1.get(0)).doubleValue() + Math.sqrt(Math.pow(((BigDecimal)var1.get(0)).doubleValue(), 2.0) + 1.0));
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("ACOSH", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            if (Double.compare(((BigDecimal)var1.get(0)).doubleValue(), 1.0) < 0) {
               throw new Expression.ExpressionException("Number must be x >= 1");
            } else {
               double var2 = Math.log(((BigDecimal)var1.get(0)).doubleValue() + Math.sqrt(Math.pow(((BigDecimal)var1.get(0)).doubleValue(), 2.0) - 1.0));
               return new BigDecimal(var2, Expression.this.mc);
            }
         }
      });
      this.addFunction(new Expression.Function("ATANH", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            if (!(Math.abs(((BigDecimal)var1.get(0)).doubleValue()) > 1.0) && Math.abs(((BigDecimal)var1.get(0)).doubleValue()) != 1.0) {
               double var2 = 0.5 * Math.log((1.0 + ((BigDecimal)var1.get(0)).doubleValue()) / (1.0 - ((BigDecimal)var1.get(0)).doubleValue()));
               return new BigDecimal(var2, Expression.this.mc);
            } else {
               throw new Expression.ExpressionException("Number must be |x| < 1");
            }
         }
      });
      this.addFunction(new Expression.Function("RAD", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.toRadians(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("DEG", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.toDegrees(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("MAX", -1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            if (var1.isEmpty()) {
               throw new Expression.ExpressionException("MAX requires at least one parameter");
            } else {
               BigDecimal var2 = null;

               for (BigDecimal var4 : var1) {
                  Expression.this.assertNotNull(var4);
                  if (var2 == null || var4.compareTo(var2) > 0) {
                     var2 = var4;
                  }
               }

               return var2;
            }
         }
      });
      this.addFunction(new Expression.Function("MIN", -1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            if (var1.isEmpty()) {
               throw new Expression.ExpressionException("MIN requires at least one parameter");
            } else {
               BigDecimal var2 = null;

               for (BigDecimal var4 : var1) {
                  Expression.this.assertNotNull(var4);
                  if (var2 == null || var4.compareTo(var2) < 0) {
                     var2 = var4;
                  }
               }

               return var2;
            }
         }
      });
      this.addFunction(new Expression.Function("ABS", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            return ((BigDecimal)var1.get(0)).abs(Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("LOG", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.log(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("LOG10", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            double var2 = Math.log10(((BigDecimal)var1.get(0)).doubleValue());
            return new BigDecimal(var2, Expression.this.mc);
         }
      });
      this.addFunction(new Expression.Function("ROUND", 2) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0), (BigDecimal)var1.get(1));
            BigDecimal var2 = (BigDecimal)var1.get(0);
            int var3 = ((BigDecimal)var1.get(1)).intValue();
            return var2.setScale(var3, Expression.this.mc.getRoundingMode());
         }
      });
      this.addFunction(new Expression.Function("FLOOR", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            BigDecimal var2 = (BigDecimal)var1.get(0);
            return var2.setScale(0, RoundingMode.FLOOR);
         }
      });
      this.addFunction(new Expression.Function("CEILING", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            BigDecimal var2 = (BigDecimal)var1.get(0);
            return var2.setScale(0, RoundingMode.CEILING);
         }
      });
      this.addFunction(new Expression.Function("SQRT", 1) {
         @Override
         public BigDecimal eval(List<BigDecimal> var1) {
            Expression.this.assertNotNull((BigDecimal)var1.get(0));
            BigDecimal var2 = (BigDecimal)var1.get(0);
            if (var2.compareTo(BigDecimal.ZERO) == 0) {
               return new BigDecimal(0);
            } else if (var2.signum() < 0) {
               throw new Expression.ExpressionException("Argument to SQRT() function must not be negative");
            } else {
               BigInteger var3 = var2.movePointRight(Expression.this.mc.getPrecision() << 1).toBigInteger();
               int var4 = var3.bitLength() + 1 >> 1;
               BigInteger var5 = var3.shiftRight(var4);

               BigInteger var7;
               do {
                  BigInteger var6 = var5;
                  var5 = var5.add(var3.divide(var5)).shiftRight(1);
                  Thread.yield();
                  var7 = var5.subtract(var6).abs();
               } while (var7.compareTo(BigInteger.ZERO) != 0 && var7.compareTo(BigInteger.ONE) != 0);

               return new BigDecimal(var5, Expression.this.mc.getPrecision());
            }
         }
      });
      this.variables.put("e", this.createLazyNumber(e));
      this.variables.put("PI", this.createLazyNumber(PI));
      this.variables.put("NULL", null);
      this.variables.put("TRUE", this.createLazyNumber(BigDecimal.ONE));
      this.variables.put("FALSE", this.createLazyNumber(BigDecimal.ZERO));
   }

   protected void assertNotNull(BigDecimal var1) {
      if (var1 == null) {
         throw new ArithmeticException("Operand may not be null");
      }
   }

   protected void assertNotNull(BigDecimal var1, BigDecimal var2) {
      if (var1 == null) {
         throw new ArithmeticException("First operand may not be null");
      } else if (var2 == null) {
         throw new ArithmeticException("Second operand may not be null");
      }
   }

   protected boolean isNumber(String var1) {
      if (var1.charAt(0) == '-' && var1.length() == 1) {
         return false;
      } else if (var1.charAt(0) == '+' && var1.length() == 1) {
         return false;
      } else if (var1.charAt(0) != '.' || var1.length() != 1 && Character.isDigit(var1.charAt(1))) {
         if (var1.charAt(0) != 'e' && var1.charAt(0) != 'E') {
            for (char var5 : var1.toCharArray()) {
               if (!Character.isDigit(var5) && var5 != '-' && var5 != '.' && var5 != 'e' && var5 != 'E' && var5 != '+') {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private List<Expression.Token> shuntingYard(String var1) {
      ArrayList var2 = new ArrayList();
      Stack var3 = new Stack();
      Expression.Tokenizer var4 = new Expression.Tokenizer(var1);
      Expression.Token var5 = null;
      Expression.Token var6 = null;

      while (var4.hasNext()) {
         Expression.Token var7 = var4.next();
         switch (var7.type) {
            case VARIABLE:
               var2.add(var7);
               break;
            case FUNCTION:
               var3.push(var7);
               var5 = var7;
               break;
            case LITERAL:
            case HEX_LITERAL:
               if (var6 != null && (var6.type == Expression.TokenType.LITERAL || var6.type == Expression.TokenType.HEX_LITERAL)) {
                  throw new Expression.ExpressionException("Missing operator", var7.pos);
               }

               var2.add(var7);
               break;
            case OPERATOR:
               if (var6 != null && (var6.type == Expression.TokenType.COMMA || var6.type == Expression.TokenType.OPEN_PAREN)) {
                  throw new Expression.ExpressionException("Missing parameter(s) for operator " + var7, var7.pos);
               }

               LazyOperator var11 = this.operators.get(var7.surface);
               if (var11 == null) {
                  throw new Expression.ExpressionException("Unknown operator " + var7, var7.pos + 1);
               }

               this.shuntOperators(var2, var3, var11);
               var3.push(var7);
               break;
            case UNARY_OPERATOR:
               if (var6 != null
                  && var6.type != Expression.TokenType.OPERATOR
                  && var6.type != Expression.TokenType.COMMA
                  && var6.type != Expression.TokenType.OPEN_PAREN
                  && var6.type != Expression.TokenType.UNARY_OPERATOR) {
                  throw new Expression.ExpressionException("Invalid position for unary operator " + var7, var7.pos);
               }

               LazyOperator var10 = this.operators.get(var7.surface);
               if (var10 == null) {
                  throw new Expression.ExpressionException("Unknown unary operator " + var7.surface.substring(0, var7.surface.length() - 1), var7.pos + 1);
               }

               this.shuntOperators(var2, var3, var10);
               var3.push(var7);
               break;
            case OPEN_PAREN:
               if (var6 != null) {
                  if (var6.type == Expression.TokenType.LITERAL
                     || var6.type == Expression.TokenType.CLOSE_PAREN
                     || var6.type == Expression.TokenType.VARIABLE
                     || var6.type == Expression.TokenType.HEX_LITERAL) {
                     Expression.Token var8 = new Expression.Token();
                     var8.append("*");
                     var8.type = Expression.TokenType.OPERATOR;
                     var3.push(var8);
                  }

                  if (var6.type == Expression.TokenType.FUNCTION) {
                     var2.add(var7);
                  }
               }

               var3.push(var7);
               break;
            case COMMA:
               if (var6 != null && var6.type == Expression.TokenType.OPERATOR) {
                  throw new Expression.ExpressionException("Missing parameter(s) for operator " + var6, var6.pos);
               }

               while (!var3.isEmpty() && ((Expression.Token)var3.peek()).type != Expression.TokenType.OPEN_PAREN) {
                  var2.add((Expression.Token)var3.pop());
               }

               if (var3.isEmpty()) {
                  if (var5 == null) {
                     throw new Expression.ExpressionException("Unexpected comma", var7.pos);
                  }

                  throw new Expression.ExpressionException("Parse error for function " + var5, var7.pos);
               }
               break;
            case CLOSE_PAREN:
               if (var6 != null && var6.type == Expression.TokenType.OPERATOR) {
                  throw new Expression.ExpressionException("Missing parameter(s) for operator " + var6, var6.pos);
               }

               while (!var3.isEmpty() && ((Expression.Token)var3.peek()).type != Expression.TokenType.OPEN_PAREN) {
                  var2.add((Expression.Token)var3.pop());
               }

               if (var3.isEmpty()) {
                  throw new Expression.ExpressionException("Mismatched parentheses");
               }

               var3.pop();
               if (!var3.isEmpty() && ((Expression.Token)var3.peek()).type == Expression.TokenType.FUNCTION) {
                  var2.add((Expression.Token)var3.pop());
               }
               break;
            case STRINGPARAM:
               var3.push(var7);
         }

         var6 = var7;
      }

      while (!var3.isEmpty()) {
         Expression.Token var9 = (Expression.Token)var3.pop();
         if (var9.type == Expression.TokenType.OPEN_PAREN || var9.type == Expression.TokenType.CLOSE_PAREN) {
            throw new Expression.ExpressionException("Mismatched parentheses");
         }

         var2.add(var9);
      }

      return var2;
   }

   private void shuntOperators(List<Expression.Token> var1, Stack<Expression.Token> var2, LazyOperator var3) {
      for (Expression.Token var4 = var2.isEmpty() ? null : (Expression.Token)var2.peek();
         var4 != null
            && (var4.type == Expression.TokenType.OPERATOR || var4.type == Expression.TokenType.UNARY_OPERATOR)
            && (
               var3.isLeftAssoc() && var3.getPrecedence() <= this.operators.get(var4.surface).getPrecedence()
                  || var3.getPrecedence() < this.operators.get(var4.surface).getPrecedence()
            );
         var4 = var2.isEmpty() ? null : (Expression.Token)var2.peek()
      ) {
         var1.add((Expression.Token)var2.pop());
      }
   }

   public BigDecimal eval() {
      return this.eval(true);
   }

   public BigDecimal eval(boolean var1) {
      ArrayDeque var2 = new ArrayDeque();

      for (final Expression.Token var4 : this.getRPN()) {
         switch (var4.type) {
            case VARIABLE:
               if (!this.variables.containsKey(var4.surface)) {
                  throw new Expression.ExpressionException("Unknown operator or function: " + var4);
               }

               var2.push(new Expression.LazyNumber() {
                  @Override
                  public BigDecimal eval() {
                     Expression.LazyNumber var1 = Expression.this.variables.get(var4.surface);
                     BigDecimal var2 = var1 == null ? null : var1.eval();
                     return var2 == null ? null : var2.round(Expression.this.mc);
                  }

                  @Override
                  public String getString() {
                     return var4.surface;
                  }
               });
               break;
            case FUNCTION:
               net.advancedplugins.jobs.impl.utils.evalex.LazyFunction var8 = this.functions.get(var4.surface.toUpperCase(Locale.ROOT));
               ArrayList var9 = new ArrayList(!var8.numParamsVaries() ? var8.getNumParams() : 0);

               while (!var2.isEmpty() && var2.peek() != PARAMS_START) {
                  var9.add(0, (Expression.LazyNumber)var2.pop());
               }

               if (var2.peek() == PARAMS_START) {
                  var2.pop();
               }

               Expression.LazyNumber var10 = var8.lazyEval(var9);
               var2.push(var10);
               break;
            case LITERAL:
               var2.push(new Expression.LazyNumber() {
                  @Override
                  public BigDecimal eval() {
                     return var4.surface.equalsIgnoreCase("NULL") ? null : new BigDecimal(var4.surface, Expression.this.mc);
                  }

                  @Override
                  public String getString() {
                     return String.valueOf(new BigDecimal(var4.surface, Expression.this.mc));
                  }
               });
               break;
            case OPERATOR:
               final Expression.LazyNumber var12 = (Expression.LazyNumber)var2.pop();
               final Expression.LazyNumber var13 = (Expression.LazyNumber)var2.pop();
               Expression.LazyNumber var7 = new Expression.LazyNumber() {
                  @Override
                  public BigDecimal eval() {
                     return Expression.this.operators.get(var4.surface).eval(var13, var12).eval();
                  }

                  @Override
                  public String getString() {
                     return String.valueOf(Expression.this.operators.get(var4.surface).eval(var13, var12).eval());
                  }
               };
               var2.push(var7);
               break;
            case UNARY_OPERATOR:
               final Expression.LazyNumber var5 = (Expression.LazyNumber)var2.pop();
               Expression.LazyNumber var6 = new Expression.LazyNumber() {
                  @Override
                  public BigDecimal eval() {
                     return Expression.this.operators.get(var4.surface).eval(var5, null).eval();
                  }

                  @Override
                  public String getString() {
                     return String.valueOf(Expression.this.operators.get(var4.surface).eval(var5, null).eval());
                  }
               };
               var2.push(var6);
               break;
            case OPEN_PAREN:
               var2.push(PARAMS_START);
               break;
            case COMMA:
            case CLOSE_PAREN:
            default:
               throw new Expression.ExpressionException("Unexpected token " + var4.surface, var4.pos);
            case HEX_LITERAL:
               var2.push(new Expression.LazyNumber() {
                  @Override
                  public BigDecimal eval() {
                     return new BigDecimal(new BigInteger(var4.surface.substring(2), 16), Expression.this.mc);
                  }

                  @Override
                  public String getString() {
                     return new BigInteger(var4.surface.substring(2), 16).toString();
                  }
               });
               break;
            case STRINGPARAM:
               var2.push(new Expression.LazyNumber() {
                  @Override
                  public BigDecimal eval() {
                     return null;
                  }

                  @Override
                  public String getString() {
                     return var4.surface;
                  }
               });
         }
      }

      BigDecimal var11 = ((Expression.LazyNumber)var2.pop()).eval();
      if (var11 == null) {
         return null;
      } else {
         if (var1) {
            var11 = var11.stripTrailingZeros();
         }

         return var11;
      }
   }

   public Expression setPrecision(int var1) {
      this.mc = new MathContext(var1);
      return this;
   }

   public Expression setRoundingMode(RoundingMode var1) {
      this.mc = new MathContext(this.mc.getPrecision(), var1);
      return this;
   }

   public Expression setFirstVariableCharacters(String var1) {
      this.firstVarChars = var1;
      return this;
   }

   public Expression setVariableCharacters(String var1) {
      this.varChars = var1;
      return this;
   }

   public <OPERATOR extends LazyOperator> OPERATOR addOperator(OPERATOR var1) {
      String var2 = var1.getOper();
      if (var1 instanceof AbstractUnaryOperator) {
         var2 = var2 + "u";
      }

      return (OPERATOR)this.operators.put(var2, var1);
   }

   public net.advancedplugins.jobs.impl.utils.evalex.Function addFunction(net.advancedplugins.jobs.impl.utils.evalex.Function var1) {
      return (net.advancedplugins.jobs.impl.utils.evalex.Function)this.functions.put(var1.getName(), var1);
   }

   public net.advancedplugins.jobs.impl.utils.evalex.LazyFunction addLazyFunction(net.advancedplugins.jobs.impl.utils.evalex.LazyFunction var1) {
      return this.functions.put(var1.getName(), var1);
   }

   public Expression setVariable(String var1, BigDecimal var2) {
      return this.setVariable(var1, this.createLazyNumber(var2));
   }

   public Expression setVariable(String var1, Expression.LazyNumber var2) {
      this.variables.put(var1, var2);
      return this;
   }

   public Expression setVariable(String var1, final String var2) {
      if (this.isNumber(var2)) {
         this.variables.put(var1, this.createLazyNumber(new BigDecimal(var2, this.mc)));
      } else if (var2.equalsIgnoreCase("null")) {
         this.variables.put(var1, null);
      } else {
         this.variables.put(var1, new Expression.LazyNumber() {
            private final Map<String, Expression.LazyNumber> outerVariables = Expression.this.variables;
            private final Map<String, net.advancedplugins.jobs.impl.utils.evalex.LazyFunction> outerFunctions = Expression.this.functions;
            private final Map<String, LazyOperator> outerOperators = Expression.this.operators;
            private final String innerExpressionString = var2;
            private final MathContext inneMc = Expression.this.mc;

            @Override
            public String getString() {
               return this.innerExpressionString;
            }

            @Override
            public BigDecimal eval() {
               Expression var1x = new Expression(this.innerExpressionString, this.inneMc);
               var1x.variables = this.outerVariables;
               var1x.functions = this.outerFunctions;
               var1x.operators = this.outerOperators;
               return var1x.eval();
            }
         });
         this.rpn = null;
      }

      return this;
   }

   private Expression createEmbeddedExpression(String var1) {
      Map var2 = this.variables;
      Map var3 = this.functions;
      Map var4 = this.operators;
      MathContext var5 = this.mc;
      Expression var6 = new Expression(var1, var5);
      var6.variables = var2;
      var6.functions = var3;
      var6.operators = var4;
      return var6;
   }

   public Expression with(String var1, BigDecimal var2) {
      return this.setVariable(var1, var2);
   }

   public Expression with(String var1, Expression.LazyNumber var2) {
      return this.setVariable(var1, var2);
   }

   public Expression and(String var1, String var2) {
      return this.setVariable(var1, var2);
   }

   public Expression and(String var1, BigDecimal var2) {
      return this.setVariable(var1, var2);
   }

   public Expression and(String var1, Expression.LazyNumber var2) {
      return this.setVariable(var1, var2);
   }

   public Expression with(String var1, String var2) {
      return this.setVariable(var1, var2);
   }

   public Iterator<Expression.Token> getExpressionTokenizer() {
      String var1 = this.expressionString;
      return new Expression.Tokenizer(var1);
   }

   private List<Expression.Token> getRPN() {
      if (this.rpn == null) {
         this.rpn = this.shuntingYard(this.expressionString);
         this.validate(this.rpn);
      }

      return this.rpn;
   }

   private void validate(List<Expression.Token> var1) {
      Stack var2 = new Stack();
      var2.push(0);

      for (Expression.Token var4 : var1) {
         switch (var4.type) {
            case FUNCTION:
               net.advancedplugins.jobs.impl.utils.evalex.LazyFunction var5 = this.functions.get(var4.surface.toUpperCase(Locale.ROOT));
               if (var5 == null) {
                  throw new Expression.ExpressionException("Unknown function " + var4, var4.pos + 1);
               }

               int var6 = (Integer)var2.pop();
               if (!var5.numParamsVaries() && var6 != var5.getNumParams()) {
                  throw new Expression.ExpressionException("Function " + var4 + " expected " + var5.getNumParams() + " parameters, got " + var6);
               }

               if (var2.isEmpty()) {
                  throw new Expression.ExpressionException("Too many function calls, maximum scope exceeded");
               }

               var2.set(var2.size() - 1, (Integer)var2.peek() + 1);
               break;
            case LITERAL:
            default:
               var2.set(var2.size() - 1, (Integer)var2.peek() + 1);
               break;
            case OPERATOR:
               if ((Integer)var2.peek() < 2) {
                  throw new Expression.ExpressionException("Missing parameter(s) for operator " + var4);
               }

               var2.set(var2.size() - 1, (Integer)var2.peek() - 2 + 1);
               break;
            case UNARY_OPERATOR:
               if ((Integer)var2.peek() < 1) {
                  throw new Expression.ExpressionException("Missing parameter(s) for operator " + var4);
               }
               break;
            case OPEN_PAREN:
               var2.push(0);
         }
      }

      if (var2.size() > 1) {
         throw new Expression.ExpressionException("Too many unhandled function parameter lists");
      } else if ((Integer)var2.peek() > 1) {
         throw new Expression.ExpressionException("Too many numbers or variables");
      } else if ((Integer)var2.peek() < 1) {
         throw new Expression.ExpressionException("Empty expression");
      }
   }

   public String toRPN() {
      StringBuilder var1 = new StringBuilder();

      for (Expression.Token var3 : this.getRPN()) {
         if (var1.length() != 0) {
            var1.append(" ");
         }

         if (var3.type == Expression.TokenType.VARIABLE && this.variables.containsKey(var3.surface)) {
            Expression.LazyNumber var4 = this.variables.get(var3.surface);
            String var5 = var4.getString();
            if (this.isNumber(var5)) {
               var1.append(var3);
            } else {
               Expression var6 = this.createEmbeddedExpression(var5);
               String var7 = var6.toRPN();
               var1.append(var7);
            }
         } else {
            var1.append(var3);
         }
      }

      return var1.toString();
   }

   public Set<String> getDeclaredVariables() {
      return Collections.unmodifiableSet(this.variables.keySet());
   }

   public Set<String> getDeclaredOperators() {
      return Collections.unmodifiableSet(this.operators.keySet());
   }

   public Set<String> getDeclaredFunctions() {
      return Collections.unmodifiableSet(this.functions.keySet());
   }

   public String getExpression() {
      return this.expressionString;
   }

   public List<String> getUsedVariables() {
      ArrayList var1 = new ArrayList();
      Expression.Tokenizer var2 = new Expression.Tokenizer(this.expressionString);

      while (var2.hasNext()) {
         Expression.Token var3 = var2.next();
         String var4 = var3.toString();
         if (var3.type == Expression.TokenType.VARIABLE && !var4.equals("PI") && !var4.equals("e") && !var4.equals("TRUE") && !var4.equals("FALSE")) {
            var1.add(var4);
         }
      }

      return var1;
   }

   public String getOriginalExpression() {
      return this.originalExpression;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Expression var2 = (Expression)var1;
         return this.expressionString == null ? var2.expressionString == null : this.expressionString.equals(var2.expressionString);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.expressionString == null ? 0 : this.expressionString.hashCode();
   }

   @Override
   public String toString() {
      return this.expressionString;
   }

   public boolean isBoolean() {
      List var1 = this.getRPN();
      if (!var1.isEmpty()) {
         for (int var2 = var1.size() - 1; var2 >= 0; var2--) {
            Expression.Token var3 = (Expression.Token)var1.get(var2);
            if (!var3.surface.equals("IF")) {
               if (var3.type == Expression.TokenType.FUNCTION) {
                  return this.functions.get(var3.surface).isBooleanFunction();
               }

               if (var3.type == Expression.TokenType.OPERATOR) {
                  return this.operators.get(var3.surface).isBooleanOperator();
               }
            }
         }
      }

      return false;
   }

   public List<String> infixNotation() {
      ArrayList var1 = new ArrayList();
      Expression.Tokenizer var2 = new Expression.Tokenizer(this.expressionString);

      while (var2.hasNext()) {
         Expression.Token var3 = var2.next();
         String var4 = "{" + var3.type + ":" + var3.surface + "}";
         var1.add(var4);
      }

      return var1;
   }

   public static class ExpressionException extends RuntimeException {
      private static final long serialVersionUID = 1118142866870779047L;

      public ExpressionException(String var1) {
         super(var1);
      }

      public ExpressionException(String var1, int var2) {
         super(var1 + " at character position " + var2);
      }
   }

   public abstract class Function extends AbstractFunction {
      public Function(String nullx, int nullxx) {
         super(nullx, nullxx);
      }

      public Function(String nullx, int nullxx, boolean nullxxx) {
         super(nullx, nullxx, nullxxx);
      }
   }

   public abstract class LazyFunction extends AbstractLazyFunction {
      public LazyFunction(String nullx, int nullxx, boolean nullxxx) {
         super(nullx, nullxx, nullxxx);
      }

      public LazyFunction(String nullx, int nullxx) {
         super(nullx, nullxx);
      }
   }

   public interface LazyNumber {
      BigDecimal eval();

      String getString();
   }

   public abstract class Operator extends AbstractOperator {
      public Operator(String nullx, int nullxx, boolean nullxxx, boolean nullxxxx) {
         super(nullx, nullxx, nullxxx, nullxxxx);
      }

      public Operator(String nullx, int nullxx, boolean nullxxx) {
         super(nullx, nullxx, nullxxx);
      }
   }

   public class Token {
      public String surface = "";
      public Expression.TokenType type;
      public int pos;

      public void append(char var1) {
         this.surface = this.surface + var1;
      }

      public void append(String var1) {
         this.surface = this.surface + var1;
      }

      public char charAt(int var1) {
         return this.surface.charAt(var1);
      }

      public int length() {
         return this.surface.length();
      }

      @Override
      public String toString() {
         return this.surface;
      }
   }

   static enum TokenType {
      VARIABLE,
      FUNCTION,
      LITERAL,
      OPERATOR,
      UNARY_OPERATOR,
      OPEN_PAREN,
      COMMA,
      CLOSE_PAREN,
      HEX_LITERAL,
      STRINGPARAM;
   }

   private class Tokenizer implements Iterator<Expression.Token> {
      private int pos = 0;
      private final String input;
      private Expression.Token previousToken;

      public Tokenizer(String nullx) {
         this.input = nullx.trim();
      }

      @Override
      public boolean hasNext() {
         return this.pos < this.input.length();
      }

      private char peekNextChar() {
         return this.pos < this.input.length() - 1 ? this.input.charAt(this.pos + 1) : '\u0000';
      }

      private boolean isHexDigit(char var1) {
         return var1 == 'x' || var1 == 'X' || var1 >= '0' && var1 <= '9' || var1 >= 'a' && var1 <= 'f' || var1 >= 'A' && var1 <= 'F';
      }

      public Expression.Token next() {
         Expression.Token var1 = Expression.this.new Token();
         if (this.pos >= this.input.length()) {
            this.previousToken = null;
            return null;
         } else {
            char var2 = this.input.charAt(this.pos);

            while (Character.isWhitespace(var2) && this.pos < this.input.length()) {
               var2 = this.input.charAt(++this.pos);
            }

            var1.pos = this.pos;
            boolean var3 = false;
            if (!Character.isDigit(var2) && (var2 != '.' || !Character.isDigit(this.peekNextChar()))) {
               if (var2 == '"') {
                  this.pos++;
                  if (this.previousToken.type == Expression.TokenType.STRINGPARAM) {
                     return this.next();
                  }

                  for (char var7 = this.input.charAt(this.pos); var7 != '"'; var7 = this.pos == this.input.length() ? 0 : this.input.charAt(this.pos)) {
                     var1.append(this.input.charAt(this.pos++));
                  }

                  var1.type = Expression.TokenType.STRINGPARAM;
               } else if (Character.isLetter(var2) || Expression.this.firstVarChars.indexOf(var2) >= 0) {
                  while (
                     (
                           Character.isLetter(var2)
                              || Character.isDigit(var2)
                              || Expression.this.varChars.indexOf(var2) >= 0
                              || var1.length() == 0 && Expression.this.firstVarChars.indexOf(var2) >= 0
                        )
                        && this.pos < this.input.length()
                  ) {
                     var1.append(this.input.charAt(this.pos++));
                     var2 = this.pos == this.input.length() ? 0 : this.input.charAt(this.pos);
                  }

                  if (Character.isWhitespace(var2)) {
                     while (Character.isWhitespace(var2) && this.pos < this.input.length()) {
                        var2 = this.input.charAt(this.pos++);
                     }

                     this.pos--;
                  }

                  if (Expression.this.operators.containsKey(var1.surface)) {
                     var1.type = Expression.TokenType.OPERATOR;
                  } else if (var2 == '(') {
                     var1.type = Expression.TokenType.FUNCTION;
                  } else {
                     var1.type = Expression.TokenType.VARIABLE;
                  }
               } else if (var2 != '(' && var2 != ')' && var2 != ',') {
                  StringBuilder var4 = new StringBuilder();
                  int var5 = this.pos;
                  var2 = this.input.charAt(this.pos);

                  int var6;
                  for (var6 = -1;
                     !Character.isLetter(var2)
                        && !Character.isDigit(var2)
                        && Expression.this.firstVarChars.indexOf(var2) < 0
                        && !Character.isWhitespace(var2)
                        && var2 != '('
                        && var2 != ')'
                        && var2 != ','
                        && this.pos < this.input.length();
                     var2 = this.pos == this.input.length() ? 0 : this.input.charAt(this.pos)
                  ) {
                     var4.append(var2);
                     this.pos++;
                     if (Expression.this.operators.containsKey(var4.toString())) {
                        var6 = this.pos;
                     }
                  }

                  if (var6 != -1) {
                     var1.append(this.input.substring(var5, var6));
                     this.pos = var6;
                  } else {
                     var1.append(var4.toString());
                  }

                  if (this.previousToken != null
                     && this.previousToken.type != Expression.TokenType.OPERATOR
                     && this.previousToken.type != Expression.TokenType.OPEN_PAREN
                     && this.previousToken.type != Expression.TokenType.COMMA
                     && this.previousToken.type != Expression.TokenType.UNARY_OPERATOR) {
                     var1.type = Expression.TokenType.OPERATOR;
                  } else {
                     var1.surface = var1.surface + "u";
                     var1.type = Expression.TokenType.UNARY_OPERATOR;
                  }
               } else {
                  if (var2 == '(') {
                     var1.type = Expression.TokenType.OPEN_PAREN;
                  } else if (var2 == ')') {
                     var1.type = Expression.TokenType.CLOSE_PAREN;
                  } else {
                     var1.type = Expression.TokenType.COMMA;
                  }

                  var1.append(var2);
                  this.pos++;
               }
            } else {
               if (var2 == '0' && (this.peekNextChar() == 'x' || this.peekNextChar() == 'X')) {
                  var3 = true;
               }

               while (
                  var3 && this.isHexDigit(var2)
                     || (
                           Character.isDigit(var2)
                              || var2 == '.'
                              || var2 == 'e'
                              || var2 == 'E'
                              || var2 == '-' && var1.length() > 0 && ('e' == var1.charAt(var1.length() - 1) || 'E' == var1.charAt(var1.length() - 1))
                              || var2 == '+' && var1.length() > 0 && ('e' == var1.charAt(var1.length() - 1) || 'E' == var1.charAt(var1.length() - 1))
                        )
                        && this.pos < this.input.length()
               ) {
                  var1.append(this.input.charAt(this.pos++));
                  var2 = this.pos == this.input.length() ? 0 : this.input.charAt(this.pos);
               }

               var1.type = var3 ? Expression.TokenType.HEX_LITERAL : Expression.TokenType.LITERAL;
            }

            this.previousToken = var1;
            return var1;
         }
      }

      @Override
      public void remove() {
         throw new Expression.ExpressionException("remove() not supported");
      }
   }

   public abstract class UnaryOperator extends AbstractUnaryOperator {
      public UnaryOperator(String nullx, int nullxx, boolean nullxxx) {
         super(nullx, nullxx, nullxxx);
      }
   }
}
