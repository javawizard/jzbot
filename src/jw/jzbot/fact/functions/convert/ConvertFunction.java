package jw.jzbot.fact.functions.convert;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.FactParser;
import jw.jzbot.fact.Function;
import jw.jzbot.fact.Sink;
import jw.jzbot.fact.convert.Encoding;

@SuppressWarnings("unchecked")
public class ConvertFunction extends Function
{
    private static Map<String, Encoding> encodings = new HashMap<String, Encoding>();
    static
    {
        System.out.println("*********I'M HERE AS THE CONVERT FUNCTION");
        try
        {
            File factFolder =
                    new File(FactParser.class.getResource("FactParser.class").toURI())
                            .getParentFile();
            System.out.println("Folder is " + factFolder.getAbsolutePath());
            File encodingsFolder = new File(factFolder, "convert/encodings");
            System.out.println("Encodings in " + encodingsFolder);
            File[] files = encodingsFolder.listFiles();
            for (File file : files)
            {
                System.out.println("File " + file.getName());
                if (file.getName().endsWith("Encoding.class"))
                {
                    String className =
                            file.getName().substring(0,
                                    file.getName().length() - ".class".length());
                    String encodingName =
                            className
                                    .substring(0, className.length() - "Encoding".length())
                                    .toLowerCase();
                    System.out.println("Encoding name: " + encodingName);
                    Class<? extends Encoding> c =
                            (Class<? extends Encoding>) Class
                                    .forName("jw.jzbot.fact.convert.encodings." + className);
                    encodings.put(encodingName, c.newInstance());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Exception while initializing encodings", e);
        }
    }
    
    @Override
    public void evaluate(Sink sink, ArgumentList arguments, FactContext context)
    {
        String from = arguments.resolveString(0);
        String to = arguments.resolveString(1);
        String input = arguments.resolveString(2);
        Encoding fromEncoding = encodings.get(from);
        Encoding toEncoding = encodings.get(to);
        sink.write(toEncoding.encode(fromEncoding.decode(input)));
    }
    
    @Override
    public String getHelp(String topic)
    {
        if (topic == null)
            return "Syntax: {convert|<from>|<to>|<input>} -- Converts input between "
                + "several available encodings. Each encoding is a subpage of this "
                + "help page; request help on each of those subpages to see what "
                + "those encodings do. <from> and <to> are the names of encodings; "
                + "<input> is the input to convert in a form expected by the "
                + "encoding named by <from>. This function then evaluates to the "
                + "input in the encoding named by <to>. If the encodings are not "
                + "compatible, an exception will be thrown. The help page for "
                + "each encoding will say something like \"decodes to integral "
                + "and byte array, and encodes from integral\". If <from> decodes "
                + "to at least one of the types that <to> can encode from, then "
                + "the encodings are compatible.";
        Encoding encoding = encodings.get(topic);
        if (encoding != null)
            return encoding.getHelp();
        return "There isn't an encoding named " + topic + ".";
    }
    
    @Override
    public String[] getTopics()
    {
        String[] results = encodings.keySet().toArray(new String[0]);
        System.out.println("Topics for convert: " + Arrays.toString(results));
        return results;
    }
}
