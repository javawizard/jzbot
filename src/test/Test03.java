package test;

import org.opengroove.jzbot.fact.FactContext;
import org.opengroove.jzbot.fact.FactEntity;
import org.opengroove.jzbot.fact.FactParser;

public class Test03
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        String factoid = "{{eval||%1%>%2%}}";
        FactContext context = new FactContext();
        context.setChannel("#bztraining");
        context.setSender("jcp");
        context.getLocalVars().put("1", "3");
        context.getLocalVars().put("2", "5");
        FactEntity entity = FactParser.parse(factoid, "testing");
        System.out.println(entity.resolve(context));
    }
    
}
