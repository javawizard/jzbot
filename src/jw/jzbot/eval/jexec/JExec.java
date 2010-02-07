package jw.jzbot.eval.jexec;

import java.io.PushbackReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import jw.jzbot.eval.jexec.lexer.Lexer;
import jw.jzbot.eval.jexec.node.AExpr;
import jw.jzbot.eval.jexec.node.AInAddp;
import jw.jzbot.eval.jexec.node.AInDivp;
import jw.jzbot.eval.jexec.node.AInMulp;
import jw.jzbot.eval.jexec.node.AInNmep;
import jw.jzbot.eval.jexec.node.AInSubp;
import jw.jzbot.eval.jexec.node.AInUnmp;
import jw.jzbot.eval.jexec.node.ANextAddp;
import jw.jzbot.eval.jexec.node.ANextDivp;
import jw.jzbot.eval.jexec.node.ANextMulp;
import jw.jzbot.eval.jexec.node.ANextNmep;
import jw.jzbot.eval.jexec.node.ANextSubp;
import jw.jzbot.eval.jexec.node.ANextUnmp;
import jw.jzbot.eval.jexec.node.ANumberTerm;
import jw.jzbot.eval.jexec.node.AParensTerm;
import jw.jzbot.eval.jexec.node.APostNmep;
import jw.jzbot.eval.jexec.node.APreNmep;
import jw.jzbot.eval.jexec.node.AVarNmep;
import jw.jzbot.eval.jexec.node.Node;
import jw.jzbot.eval.jexec.node.Start;
import jw.jzbot.eval.jexec.node.Token;
import jw.jzbot.eval.jexec.parser.Parser;

public class JExec
{
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
    }
    
    private BigDecimal runFunction(Token name, BigDecimal first, BigDecimal second)
    {
        try
        {
            Function function = functions.get(name.getText());
            if (function == null)
                throw new IllegalArgumentException("There is no function or "
                    + "variable named " + name.getText() + ".");
            return function.run(first, second);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("At line " + name.getLine() + ", character "
                + name.getPos() + ": An error occurred while processing the function "
                + name.getText(), e);
        }
    }
    
    /**
     * Runs the specified value (which could, for example, be "(5+3)*4"), as an arithmetic
     * equation and returns the result.
     * 
     * @param text
     *            the equation to run
     * @return
     */
    public BigDecimal run(String text)
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
    
    private BigDecimal run(Node node)
    {
        // This list is strictly in alphabetical order as it appears in the node package.
        // If you add items to this list, make sure that you place them in the list such
        // that it remains in alphabetical order.
        if (node instanceof AExpr)
            return run(((AExpr) node).getAddp());
        else if (node instanceof AInAddp)
            return run(((AInAddp) node).getFirst()).add(run(((AInAddp) node).getSecond()));
        else if (node instanceof AInDivp)
            return run(((AInDivp) node).getFirst()).divide(
                    run(((AInDivp) node).getSecond()), context);
        else if (node instanceof AInMulp)
            return run(((AInMulp) node).getFirst()).multiply(
                    run(((AInMulp) node).getSecond()), context);
        else if (node instanceof AInNmep)
            return runFunction(((AInNmep) node).getName(),
                    run(((AInNmep) node).getFirst()), run(((AInNmep) node).getSecond()));
        else if (node instanceof AInSubp)
            return run(((AInSubp) node).getFirst()).subtract(
                    run(((AInSubp) node).getSecond()));
        else if (node instanceof AInUnmp)
            return run(((AInUnmp) node).getSecond()).negate();
        else if (node instanceof ANextAddp)
            return run(((ANextAddp) node).getNext());
        else if (node instanceof ANextDivp)
            return run(((ANextDivp) node).getNext());
        else if (node instanceof ANextMulp)
            return run(((ANextMulp) node).getNext());
        else if (node instanceof ANextNmep)
            return run(((ANextNmep) node).getNext());
        else if (node instanceof ANextSubp)
            return run(((ANextSubp) node).getNext());
        else if (node instanceof ANextUnmp)
            return run(((ANextUnmp) node).getNext());
        else if (node instanceof ANumberTerm)
            return new BigDecimal(((ANumberTerm) node).getNumber().getText());
        else if (node instanceof AParensTerm)
            return run(((AParensTerm) node).getExpr());
        else if (node instanceof APostNmep)
            return runFunction(((APostNmep) node).getName(), run(((APostNmep) node)
                    .getFirst()), null);
        else if (node instanceof APreNmep)
            return runFunction(((APreNmep) node).getName(), null, run(((APreNmep) node)
                    .getSecond()));
        else if (node instanceof AVarNmep)
            return runFunction(((AVarNmep) node).getName(), null, null);
        else
            throw new IllegalArgumentException("Invalid node class \""
                + node.getClass().getName()
                + "\". This means that the JExec equation parser grammar "
                + "has been updated without updating JExec.java to "
                + "contain the logic necessary to compute the new grammar.");
    }
}
