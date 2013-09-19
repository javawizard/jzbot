package jw.jzbot.eval;

import jw.jzbot.Evaluator;
import jw.jzbot.eval.caltech.CaltechEval;

public class CaltechEvaluator extends Evaluator
{
    
    @Override
    public String evaluate(String value)
    {
        return "" + (new CaltechEval(value).evaluate());
    }
    
    @Override
    public String getName()
    {
        return "caltech";
    }
    
}
