package test;

import org.cheffo.jeplite.JEP;

public class Test02
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        JEP jep = new JEP();
        jep.addStandardConstants();
        jep.addStandardFunctions();
        jep.parseExpression("add(2,3,4)");
        double value = jep.getValue();
        System.out.println("" + value);
    }
}
