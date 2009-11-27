package test;

import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactEntity;
import jw.jzbot.fact.FactParser;
import jw.jzbot.fact.StreamSink;

public class Test03
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        String factoid = "Hello\\n{{wait||1000}}Bye\\n{{wait||1000}}cya";
        FactContext context = new FactContext();
        context.setChannel("#bztraining");
        context.setSender("jcp");
        context.getLocalVars().put("1", "3");
        context.getLocalVars().put("2", "5");
        FactEntity entity = FactParser.parse(factoid, "testing");
        entity.resolve(new StreamSink(System.out), context);
        System.out.flush();
    }
    
}
