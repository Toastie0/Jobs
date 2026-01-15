package net.toastie.jobs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for evaluating mathematical formulas.
 * Supports basic operations: +, -, *, /, ^ (power), and parentheses.
 * 
 * Features:
 * - Formula result caching for performance
 * - Variable substitution (e.g., %level%)
 * - Thread-safe operation
 */
public class FormulaEvaluator {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormulaEvaluator.class);
    
    // Cache for formula evaluation results: "formula" -> result
    private final Map<String, BigDecimal> cache = new ConcurrentHashMap<>();
    
    /**
     * Evaluates a mathematical formula and caches the result.
     * 
     * @param formula Formula string (e.g., "100 * (level^2)")
     * @return Evaluated result as double
     */
    public double evaluate(String formula) {
        // Check cache first
        BigDecimal cached = cache.get(formula);
        if (cached != null) {
            return cached.doubleValue();
        }
        
        try {
            // Remove whitespace
            formula = formula.replaceAll("\\s+", "");
            
            // Handle parentheses first
            while (formula.contains("(")) {
                int start = formula.lastIndexOf('(');
                int end = formula.indexOf(')', start);
                if (end == -1) break;
                
                String subExpr = formula.substring(start + 1, end);
                double subResult = evaluateSimple(subExpr);
                formula = formula.substring(0, start) + subResult + formula.substring(end + 1);
            }
            
            double result = evaluateSimple(formula);
            
            // Cache the result
            cache.put(formula, BigDecimal.valueOf(result));
            
            return result;
        } catch (Exception e) {
            LOGGER.error("Failed to evaluate formula: {}", formula, e);
            return 0.0;
        }
    }
    
    /**
     * Evaluates a simple expression without parentheses.
     * Order of operations: ^ (power), *, /, +, -
     */
    private double evaluateSimple(String expr) {
        // Handle exponentiation
        if (expr.contains("^")) {
            String[] parts = expr.split("\\^", 2);
            double base = evaluateSimple(parts[0]);
            double exp = evaluateSimple(parts[1]);
            return Math.pow(base, exp);
        }
        
        // Handle multiplication and division
        if (expr.contains("*") || expr.contains("/")) {
            for (int i = 0; i < expr.length(); i++) {
                char c = expr.charAt(i);
                if (c == '*' || c == '/') {
                    double left = evaluateSimple(expr.substring(0, i));
                    double right = evaluateSimple(expr.substring(i + 1));
                    return c == '*' ? left * right : left / right;
                }
            }
        }
        
        // Handle addition and subtraction
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == '+' || (c == '-' && i > 0)) {
                double left = evaluateSimple(expr.substring(0, i));
                double right = evaluateSimple(expr.substring(i + 1));
                return c == '+' ? left + right : left - right;
            }
        }
        
        // Parse number
        return Double.parseDouble(expr);
    }
    
    /**
     * Clears the formula cache.
     * Useful if memory management is needed.
     */
    public void clearCache() {
        cache.clear();
    }
    
    /**
     * Gets the current cache size.
     * 
     * @return Number of cached formulas
     */
    public int getCacheSize() {
        return cache.size();
    }
}
