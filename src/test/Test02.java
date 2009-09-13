package test;

import java.math.BigDecimal;

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
        jep.parseExpression("200.0");
        double value = jep.getValue();
        System.out.println("" + value);
        BigDecimal d = new BigDecimal(value);
        d = d.movePointRight(9);
        d = new BigDecimal(d.toBigInteger());
        d = d.movePointLeft(9);
        d = d.stripTrailingZeros();
        System.out.println(d.toPlainString());
    }
}
