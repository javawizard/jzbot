package org.opengroove.jzbot.eval;

import org.opengroove.jzbot.Evaluator;
import org.opengroove.jzbot.eval.caltech.CaltechEval;

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
