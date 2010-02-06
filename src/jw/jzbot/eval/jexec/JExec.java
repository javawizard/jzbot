package jw.jzbot.eval.jexec;

import java.io.PushbackReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import jw.jzbot.eval.jexec.lexer.Lexer;
import jw.jzbot.eval.jexec.node.Node;
import jw.jzbot.eval.jexec.node.Start;
import jw.jzbot.eval.jexec.parser.Parser;

public class JExec
{
    public interface Function
    {
        /**
         * Runs this function and returns the result. If this function is being run as a
         * variable, <tt>first</tt> and <tt>second</tt> will be null. If this function is
         * being run as a unary prefix function, <tt>first</tt> will be the argument and
         * <tt>second</tt> will be null. If this function is being run as a binary infix
         * function, <tt>first</tt> will be the first argument and <tt>second</tt> will be
         * the second argument. If this function is being run as a unary postfix function,
         * <tt>first</tt> will be null and <tt>second</tt> will be the argument.
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
    }
    
    private BigDecimal runFunction(String name, BigDecimal first, BigDecimal second)
    {
        Function function = functions.get(name);
        if (function == null)
            throw new IllegalArgumentException("There is no function or "
                + "variable named " + name + ".");
        return function.run(first, second);
    }
    
    /**
     * Runs the specified value (which could, for example, be "(5+3)*4"), as an arithmetic
     * equation and returns the result.
     * 
     * @param text
     *            the equation to run
     * @return
     */
    public static BigDecimal run(String text)
    {
        try
        {
            Lexer lexer = new Lexer(new PushbackReader(new StringReader(text)));
            Parser parser = new Parser(lexer);
            Start start = parser.parse();
            return run(start.getPExpr());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception occurred while evaluating the equation "
                + text, e);
        }
    }
    
    private static BigDecimal run(Node node)
    {
        if (false)
            return null;
        else
            throw new IllegalArgumentException("Invalid node class " + node
                + ". This means that the JExec equation parser grammar "
                + "has been updated without updating JExec.java to "
                + "contain the logic necessary to compute the new grammar.");
    }
    
}
