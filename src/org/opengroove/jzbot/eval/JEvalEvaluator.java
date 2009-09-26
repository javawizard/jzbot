package org.opengroove.jzbot.eval;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.jibble.pircbot.Colors;
import org.opengroove.jzbot.Evaluator;
import org.opengroove.jzbot.eval.jeval.Expression;
import org.opengroove.jzbot.eval.jeval.Operator;

public class JEvalEvaluator extends Evaluator
{
    private static Map<String, BigDecimal> defaultVars = new HashMap<String, BigDecimal>();
    static
    {
        /**
         * I really don't know why I'm bothering to put 1000 digits of pi in,
         * but what the heck.
         */
        defaultVars.put("pi", new BigDecimal(
                "3.14159265358979323846264338327950288419716"
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
                        + "1957781857780532171226806613001927876611195"
                        + "90921642019"));
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
        return "jeval";
    }
    
}
