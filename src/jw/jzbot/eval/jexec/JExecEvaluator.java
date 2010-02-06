package jw.jzbot.eval.jexec;

import java.math.BigDecimal;

import jw.jzbot.Evaluator;
import jw.jzbot.eval.jexec.node.Node;

public class JExecEvaluator extends Evaluator
{
    /**
     * Returns <tt>run(value).toPlainString()</tt>.
     */
    @Override
    public String evaluate(String value)
    {
        BigDecimal result = run(value);
        if (result.signum() == 0)
            return "0";
        return result.toPlainString();
    }
    
    @Override
    public String getName()
    {
        return "jexec";
    }
    
    public static void main(String[] args)
    {
        String text = "5+3";
        System.out.println(new JExec().evaluate(text));
    }
}
