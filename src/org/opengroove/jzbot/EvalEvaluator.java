package org.opengroove.jzbot;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.opengroove.jzbot.eval.Expression;
import org.opengroove.jzbot.eval.Operator;

public class EvalEvaluator extends Evaluator
{
    private static Map<String, BigDecimal> defaultVars = new HashMap<String, BigDecimal>();
    static
    {
        defaultVars.put("pi", new BigDecimal(
                "3.141592653589793238462643383279502884197169399375105820974"
                        + "94459230781640628620899862803482534211706798214"));
    }
    
    @Override
    public String evaluate(String value)
    {
        Expression exp = new Expression(value);
        BigDecimal result = exp.eval(defaultVars);
        result = result.round(Operator.defaultContext);
        if (result.signum() == 0)
            return "0";
        return result.toPlainString();
    }
    
    @Override
    public String getName()
    {
        return "eval";
    }
    
}
