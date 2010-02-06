package jw.jzbot.eval.jexec;

import java.math.BigDecimal;

import jw.jzbot.Evaluator;

public class JExec extends Evaluator
{
    /**
     * Returns <tt>run(value).toPlainString()</tt>.
     */
    @Override
    public String evaluate(String value)
    {
        return "";
    }
    
    /**
     * Runs the specified value (which could, for example, be "(5+3)*4"), as an arithmetic
     * expression and returns the result.
     * 
     * @param value
     * @return
     */
    public static BigDecimal run(String value)
    {
        return BigDecimal.ZERO;
    }
    
    @Override
    public String getName()
    {
        return "jexec";
    }
    
}
