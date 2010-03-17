package jw.jzbot.eval.jexec;

import java.math.BigDecimal;

import jw.jzbot.Evaluator;

public class JExecEvaluator extends Evaluator
{
    private static final JExec je = new JExec();
    
    /**
     * Returns <tt>run(value).toPlainString()</tt>.
     */
    @Override
    public String evaluate(String value)
    {
        BigDecimal result = je.run(value);
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
        JExec e = new JExec();
        e.addVariable("pi", new BigDecimal("3.1415926"));
        String text = "pi3";
        System.out.println(e.run(text));
    }
}
