package test;

import java.io.StringReader;
import java.util.Properties;

public class Test08
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        Properties props = new Properties();
        props.load(new StringReader("=hello\\nworld"));
        System.out.println(props.get(""));
    }
    
}
