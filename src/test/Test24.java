package test;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.python.core.PyDictionary;
import org.python.core.PyObject;
import org.python.util.InteractiveConsole;

public class Test24
{
    public static JFrame frame = new JFrame("Test24");
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        frame.setSize(200, 100);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
        frame.show();
        InteractiveConsole console =
                new InteractiveConsole(new PyDictionary(), "<swinginput>")
                {
                    
                    @Override
                    public String raw_input(PyObject prompt)
                    {
                        out("raw_input: " + prompt.__str__().toString());
                        return in();
                    }
                    
                    @Override
                    public void write(String data)
                    {
                        out("write: " + data);
                    }
                };
        console.setOut(new OutputStream()
        {
            
            @Override
            public void write(int b) throws IOException
            {
                System.out.println("out: " + b);
            }
        });
        console.interact();
        // out(console.getDefaultBanner());
        // out("initial prompt: >>>");
        // while (true)
        // {
        // out(console.push(in()) ? "prompt: ..." : "prompt: >>>");
        // }
    }
    
    public static String in()
    {
        return JOptionPane.showInputDialog(frame, "Type some input.");
    }
    
    public static void out(String message)
    {
        JOptionPane.showMessageDialog(frame, message);
    }
}
