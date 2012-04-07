package jw.jzbot.fact;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import jw.jzbot.fact.ast.FactEntity;
import jw.jzbot.fact.ast.FunctionReference;
import jw.jzbot.fact.ast.Literal;
import jw.jzbot.fact.ast.Sequence;
import jw.jzbot.fact.ast.VarReference;
import jw.jzbot.fact.exceptions.ParseException;
import jw.jzbot.fact.functions.*;
import jw.jzbot.fact.output.StringSink;

import org.jibble.pircbot.Colors;

import afn.parcon.Forward;
import afn.parcon.InfixExpr;
import afn.parcon.Parser;
import static afn.parcon.Functions.*;

/**
 * A class that can parse factoids. This is the main entry point to the Fact
 * interpreter. The parser is a hand-coded predictive recursive descent parser.<br/>
 * <br/>
 * 
 * To run a factoid and put the output into a string, you would do something
 * along these lines:<br/>
 * 
 * <pre>
 * String factoid = &quot;The numbers from 1 to 5 are {numberlist|1|5} and the var 1 is %1%&quot;;
 * FactContext context = new FactContext();
 * context.getLocalVars().put(&quot;1&quot;, &quot;Hello world!&quot;);
 * FactEntity entity = FactParser.parse(factoid, &quot;my_test_program&quot;);
 * StringSink sink = new StringSink();
 * entity.resolve(sink, context);
 * String result = sink.toString();
 * </pre>
 * 
 * <br/>
 * 
 * At that point, <tt>result</tt> would have the value
 * <tt>"The numbers from 1 to 5 are 1 2 3 4 5 and the var 1 is Hello world!"</tt>
 * . If you instead wanted the factoid's output to be sent to stdout, you could
 * replace the last 3 lines of the above example with this:<br/>
 * 
 * <pre>
 * entity.resolve(new StreamSink(System.out), context);
 * </pre>
 * 
 * @author Alexander Boyd
 * 
 */
public class FactParser {
    private static Map<String, Function> functionMap = new HashMap<String, Function>();
    private static Map<Function, String> reverseFunctionMap = new HashMap<Function, String>();
    private static Map<Class<? extends Function>, Function> functionsByClass = new HashMap<Class<? extends Function>, Function>();
    private static AtomicLong idSequence = new AtomicLong();
    
    public static final Parser createParser() {
        Forward sequence = new Forward();
        Parser literal = charNotIn("{|}%\\").onceOrMore()
                .translate(joinStrings).construct(Literal.class);
        Parser var = sequence(literal("%"),
                charNotIn("%").onceOrMore().translate(joinStrings),
                literal("%")).construct(VarReference.class);
        Parser call = sequence(
                literal("{"),
                new InfixExpr(sequence.translate(singletonList), InfixExpr.op(
                        "|", concatLists)), literal("}")).construct(
                FunctionReference.class);
        Parser backslashEscape = sequence(literal("\\"), charNotIn(""))
                .translate(method1(FactParser.class, "getEscapedChar"))
                .translate(toString).construct(Literal.class);
        sequence.parser = first(literal, call, var, backslashEscape)
                .zeroOrMore().construct(Sequence.class);
        return exact(sequence.parser);
    }
    
    public static final Parser parser = createParser();
    
    /**
     * Parses the specified factoid into a FactEntity. This fact entity can then
     * be {@link FactEntity#resolve(FactContext) resolved} at any point in the
     * future (and, in fact, resolved multiple times) to actually run this
     * factoid and get its output.<br/>
     * <br/>
     * 
     * Parsing a factoid does not cause any side effects, such as changes to
     * local or global variables, to occur. It's only when you actually resolve
     * a factoid that these side effects would occur.
     * 
     * @param factoid
     *            The factoid text to parse
     * @param name
     *            The name of this factoid. This doesn't technically need to be
     *            the actual name of the factoid. For that matter, it could even
     *            be the empty string. It's used when constructing the factoid
     *            stack trace if an exception gets thrown while running the
     *            factoid.
     * @return The parsed factoid
     */
    public static FactEntity parse(String factoid, String name) {
        return (FactEntity) parser.parseString(factoid);
    }
    
    // FIXME: With the change to Parcon parsing, position information was lost.
    // This needs to be restored.
    private static <T extends FactEntity> T init(T entity, String factName,
            int index) {
        entity.setFactName(factName);
        entity.setCharIndex(index);
        return entity;
    }
    
    /**
     * Gets the character that corresponds to the escaped character
     * <tt>char</tt>. This is called whenever there is a backslash followed by a
     * character within the factoid parser, to see what the actual character
     * that corresponds to the backslash-character pair should be. For example,
     * passing 'n' into this method causes it to return a newline character. Any
     * character that is not "special" according to this method will be returned
     * as-is. For example, calling this with '|' causes the method to return
     * '|'.<br/>
     * <br/>
     * 
     * If this method returns 0, then this indicates that no character is to be
     * included. This is the case when 'x' is passed in.
     * 
     * @param c
     *            The special character
     * @return The corresponding character
     */
    public static String getEscapedChar(char c) {
        switch (c) {
        case 'n':
            return "\n";
        case 'r':
            return "\r";
        case 'p':
            return "\u000f";
        case 'b':
            return "\u0002";
        case 'u':
            return "\u001f";
        case 'i':
            return "\u0016";
        case 'c':
            return "\u0003";
        case '0':
            return "\u0000";
        case 'x':
        case ' ':
            return null;
        }
        return "" + c;
    }
    
    public static void installFunction(String name, Function function) {
        functionMap.put(name.toLowerCase(), function);
        reverseFunctionMap.put(function, name.toLowerCase());
        functionsByClass.put(function.getClass(), function);
        try {
            String helpString = function.getHelp(null);
            if (helpString == null || helpString.equals(""))
                System.out.println("Warning: function " + name
                        + " does not have help text");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Function getFunction(String name) {
        return functionMap.get(name.toLowerCase());
    }
    
    public static String getFunctionName(Function function) {
        return reverseFunctionMap.get(function);
    }
    
    /**
     * Gets the function with the specified class. If more than one function
     * with the specified class has been installed, the last one to be installed
     * will be returned.
     * 
     * @param c
     * @return
     */
    public static Function getFunctionByClass(Class<? extends Function> c) {
        return functionsByClass.get(c);
    }
    
    static {
        installDefaultSet();
        installSpecialSet();
    }
    
    private static void installDefaultSet() {
        try {
            File factFolder = new File(FactParser.class.getResource(
                    "FactParser.class").toURI()).getParentFile();
            File functionsFolder = new File(factFolder, "functions");
            ArrayList<File> fileList = new ArrayList<File>();
            listFunctionFolders(functionsFolder, fileList);
            File[] files = fileList.toArray(new File[0]);
            for (File file : files) {
                try {
                    if (file.getName().endsWith("Function.class")) {
                        String className = file.getName().substring(0,
                                file.getName().length() - ".class".length());
                        String functionName = className.substring(0,
                                className.length() - "Function".length());
                        if (functionName.startsWith("_")) {
                            functionName = functionName.substring(1)
                                    .replace("Plus", "+").replace("Minus", "-")
                                    .replace("Star", "*").replace("Slash", "/")
                                    .replace("Equal", "=")
                                    .replace("Greater", ">")
                                    .replace("Less", "<")
                                    .replace("Percent", "%")
                                    .replace("Dollar", "$")
                                    .replace("Bang", "!");
                        }
                        functionName = functionName.toLowerCase().replace("_",
                                ".");
                        String folderName = generateFolderTo(file,
                                functionsFolder);
                        String classNameInFolder = folderName + className;
                        String functionNameInFolder = folderName + functionName;
                        System.out.println("Loading function "
                                + functionNameInFolder + " with class "
                                + classNameInFolder);
                        Class<? extends Function> c = (Class<? extends Function>) Class
                                .forName("jw.jzbot.fact.functions."
                                        + classNameInFolder.replaceAll(
                                                "(/|\\\\)", "."));
                        installFunction(functionName, c.newInstance());
                    } else {
                        System.out.println("Skipping non-function class "
                                + file);
                    }
                } catch (Throwable t) {
                    throw new RuntimeException(
                            "Exception while loading function for class file "
                                    + file, t);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            if (!new File("classes/jw/jzbot").exists()) {
                System.err
                        .println("Couldn't load functions because the function "
                                + "class folder doesn't exist. This could mean "
                                + "you've compiled JZBot with gcj, which means that "
                                + "JZBot won't have any support for functions.");
            } else {
                throw new RuntimeException(
                        "Error while loading default function set", t);
            }
        }
    }
    
    private static String generateFolderTo(File lower, File upper) {
        /*
         * f1 -> f2 -> f3 -> f4 -> f5 -> f6 -> f7 -> f8
         * 
         * lower=f8 upper=f3
         * 
         * Output should be f4/f5/f6/f7/
         */
        File current = lower;
        String path = "";
        while (true) {
            File parent = current.getParentFile();
            if (parent.equals(upper))
                return path;
            path = parent.getName() + "/" + path;
            current = parent;
        }
    }
    
    private static void listFunctionFolders(File folder,
            ArrayList<File> fileList) {
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.getName().startsWith(".") || file.isHidden())
                return;
            if (file.isDirectory()) {
                listFunctionFolders(file, fileList);
            } else {
                fileList.add(file);
            }
        }
    }
    
    /**
     * @deprecated All of these functions have been replaced by escapes as noted
     *             in each function's help text.
     */
    private static void installSpecialSet() {
        installFunction(
                "c",
                new CharCodeSpecial(
                        "c",
                        "\u0003",
                        "Inserts the IRC color change character. Immediately following "
                                + "this should be two digits, which represent the color of text "
                                + "that should show up.\n"
                                + "Create a factoid with the text \"{split| |{numberlist|1|15}|"
                                + "c|{c}{lset|c|{pad|2|0|%c%}}%c%%c%| }\" (without "
                                + "quotes), then run it; the result will be a list of numbers and "
                                + "the color they represent.\n"
                                + "This function is deprecated, and \"\\c\" should be used instead."));
        installFunction(
                "n",
                new CharCodeSpecial(
                        "n",
                        Colors.NORMAL,
                        "Resets any coloring that has been applied in the factoid, so that "
                                + "all succeeding text has no special formatting.\n"
                                + "This function is deprecated, and \"\\p\" should be used instead."));
        installFunction(
                "b",
                new CharCodeSpecial(
                        "b",
                        Colors.BOLD,
                        "Inserts the IRC bold character, which causes all following text "
                                + "to be shown as bold.\n"
                                + "This function is deprecated, and \"\\b\" should be used instead."));
        installFunction(
                "i",
                new CharCodeSpecial(
                        "i",
                        Colors.REVERSE,
                        "Inserts the IRC reverse character, which, depending on the client, "
                                + "either reverses the foreground and background colors or shows text"
                                + " as italic.\n"
                                + "This function is deprecated, and \"\\i\" should be used instead."));
        installFunction(
                "u",
                new CharCodeSpecial(
                        "u",
                        Colors.UNDERLINE,
                        "Inserts the IRC underline character, which causes all "
                                + "succeeding text to be underlined.\n"
                                + "This function is deprecated, and \"\\u\" should be used instead."));
    }
    
    public static String[] getFunctionNames() {
        String[] names = functionMap.keySet().toArray(new String[0]);
        Arrays.sort(names);
        return names;
    }
    
    /**
     * Parses the specified text and then explains it, omitting the default
     * {identity} function.
     * 
     * @param factoid
     *            The text of the factoid to explain
     * @param name
     *            The name of the factoid that we're in. See the <tt>name</tt>
     *            parameter of the <tt>parse</tt> method for more info on what
     *            this is.
     * @return The explanation
     */
    public static String explain(String factoid, String name) {
        FactEntity entity = parse(factoid, name);
        StringSink sink = new StringSink();
        entity.explain(sink, 0, 4);
        return sink.toString();
    }
    
    public static long nextId() {
        return idSequence.incrementAndGet();
    }
}
