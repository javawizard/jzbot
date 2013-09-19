package jw.jzbot.eval.jexec;

import java.io.PushbackReader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

public class JExec
{
    /**
     * A class that allows function access to functions in java.lang.Math. This function
     * operates correctly on nullary functions, unary functions (both prefix and postfix),
     * and binary functions. Thus, if a MathFunction with the name of "cos" is installed
     * into a JExec instance, one could evaluate "cos0", "cos(0)", "cos 0", "0 cos",
     * "(0)cos", and so on, all of which would evaluate to "1" as the cosine of 0 is 1.
     * This demonstrates that invoking a function in prefix and postfix form does not have
     * any differences.
     * 
     * @author Alexander Boyd
     * 
     */
    public class MathFunction implements Function
    {
        private Method method;
        
        public MathFunction(String name)
        {
            try
            {
                method = Math.class.getMethod(name, Double.TYPE);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Exception while constructing a "
                    + "MathPrefixFunction with the name " + name, e);
            }
        }
        
        @Override
        public BigDecimal run(BigDecimal first, BigDecimal second)
        {
            try
            {
                Object result;
                if (first == null && second == null)
                    result = method.invoke(null);
                else if (first == null && second != null)
                    result = method.invoke(null, second.doubleValue());
                else if (first != null && second == null)
                    result = method.invoke(null, first.doubleValue());
                else
                    result = method.invoke(null, first.doubleValue(), second.doubleValue());
                return new BigDecimal((Double) result);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Exception while running math function "
                    + method.getName(), e);
            }
        }
        
    }
    
    public interface Function
    {
        /**
         * Runs this function and returns the result. If this function is being run as a
         * variable, <tt>first</tt> and <tt>second</tt> will be null. If this function is
         * being run as a unary prefix function, <tt>first</tt> will be null and
         * <tt>second</tt> will be the argument. If this function is being run as a binary
         * infix function, <tt>first</tt> will be the first argument and <tt>second</tt>
         * will be the second argument. If this function is being run as a unary postfix
         * function, <tt>first</tt> will be the argument and <tt>second</tt> will be the
         * null.
         * 
         * @param first
         *            the first argument
         * @param second
         *            the second argument
         * @return the result
         */
        public BigDecimal run(BigDecimal first, BigDecimal second);
    }
    
    public class VariableFunction implements Function
    {
        private BigDecimal v;
        
        public VariableFunction(BigDecimal v)
        {
            this.v = v;
        }
        
        @Override
        public BigDecimal run(BigDecimal first, BigDecimal second)
        {
            return v;
        }
    }
    
    private Map<String, Function> functions = new HashMap<String, Function>();
    /**
     * The math context to use when evaluating equations. The default context has a
     * precision of 600.
     */
    public MathContext context = new MathContext(600);
    
    public JExec()
    {
        installDefaultFunctions();
    }
    
    public void addVariable(String name, BigDecimal value)
    {
        addFunction(name, new VariableFunction(value));
    }
    
    private void addFunction(String name, Function function)
    {
        functions.put(name, function);
    }
    
    private void installDefaultFunctions()
    {
        addVariable("pi", new BigDecimal(pi()));
        addFunction("abs", new MathFunction("abs"));
        addFunction("", new MathFunction("acos"));
        addFunction("", new MathFunction("asin"));
        addFunction("", new MathFunction("atan"));
        addFunction("", new MathFunction("atanxy"));
        addFunction("", new MathFunction("cbrt"));
        addFunction("", new MathFunction("ceil"));
        addFunction("", new MathFunction("copysign"));
        addFunction("", new MathFunction("cos"));
        addFunction("", new MathFunction("cosh"));
        addFunction("", new MathFunction("exp"));
        addFunction("", new MathFunction("expm"));
        addFunction("", new MathFunction("floor"));
        addFunction("", new MathFunction("hypot"));
        addFunction("", new MathFunction("log"));
        addFunction("", new MathFunction("logten"));
        addFunction("", new MathFunction("logp"));
        // FIXME: finish here, we need grammar functions for >, <, etc
        addFunction("", new MathFunction(""));
        addFunction("", new MathFunction(""));
        addFunction("", new MathFunction(""));
        addFunction("", new MathFunction(""));
        addFunction("", new MathFunction(""));
        addFunction("", new MathFunction(""));
        addFunction("", new MathFunction(""));
        addFunction("", new MathFunction(""));
        addFunction("", new MathFunction(""));
        addFunction("", new MathFunction(""));
        addFunction("", new MathFunction(""));
        addFunction("", new MathFunction(""));
    }
    
    private String pi()
    {
        /*
         * Yes, this is a pointless amount of pi digits. No, I'm not shortening it any
         * more than it currently is.
         */
        return "3.14159265358979323846264338327950288419716"
            + "9399375105820974944592307816406286208998628"
            + "0348253421170679821480865132823066470938446"
            + "0955058223172535940812848111745028410270193"
            + "8521105559644622948954930381964428810975665"
            + "9334461284756482337867831652712019091456485"
            + "6692346034861045432664821339360726024914127"
            + "3724587006606315588174881520920962829254091"
            + "7153643678925903600113305305488204665213841"
            + "4695194151160943305727036575959195309218611"
            + "7381932611793105118548074462379962749567351"
            + "8857527248912279381830119491298336733624406"
            + "5664308602139494639522473719070217986094370"
            + "2770539217176293176752384674818467669405132"
            + "0005681271452635608277857713427577896091736"
            + "3717872146844090122495343014654958537105079"
            + "2279689258923542019956112129021960864034418"
            + "1598136297747713099605187072113499999983729"
            + "7804995105973173281609631859502445945534690"
            + "8302642522308253344685035261931188171010003"
            + "1378387528865875332083814206171776691473035"
            + "9825349042875546873115956286388235378759375"
            + "1957781857780532171226806613001927876611195";
    }
    
    /**
     * Runs the specified value (which could, for example, be "(5+3)*4"), as an arithmetic
     * equation and returns the result.
     * 
     * @param text
     *            the equation to run
     * @return the value of the equation
     * @throws RuntimeException
     *             if there is a syntax error in the equation, or if
     */
    public BigDecimal run(String text)
    {
        try
        {
            throw new RuntimeException("TODO: implement this");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception occurred while evaluating the equation "
                + text, e);
        }
    }
    
}
