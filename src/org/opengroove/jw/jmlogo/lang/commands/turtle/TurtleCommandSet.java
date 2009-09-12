package org.opengroove.jw.jmlogo.lang.commands.turtle;

import org.opengroove.jw.jmlogo.LogoScreen;
import org.opengroove.jw.jmlogo.Point;
import org.opengroove.jw.jmlogo.lang.InterpreterContext;
import org.opengroove.jw.jmlogo.lang.InterpreterException;
import org.opengroove.jw.jmlogo.lang.ListToken;
import org.opengroove.jw.jmlogo.lang.Token;
import org.opengroove.jw.jmlogo.lang.WordToken;

public class TurtleCommandSet extends ScreenCommand
{
    public static final int GET_POS = 0;
    public static final int HIDE_TURTLE = 1;
    public static final int SHOW_TURTLE = 2;
    public static final int SET_POS = 3;
    public static final int PEN_DOWN = 4;
    public static final int PEN_UP = 5;
    public static final int SET_PEN_COLOR = 6;
    public static final int SET_SCREEN_COLOR = 7;
    public static final int FORWARD = 8;
    public static final int RIGHT = 9;
    public static final int LEFT = 10;
    public static final int BACK = 11;
    public static final int HOME = 12;
    public static final int CLEAN = 13;
    public static final int CLEARSCREEN = 14;
    public static final int PEN_DOWN_P = 15;
    public static final int GET_PEN_COLOR = 16;
    public static final int GET_SCREEN_COLOR = 17;
    public static final int GET_HEADING = 18;
    public static final int SET_HEADING = 19;
    public static final int TOWARDS = 20;
    
    private int[] argcounts =
        new int[] { 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1 };
    private String[] commandNames =
        new String[] { "pos", "hideturtle", "showturtle", "setpos", "pendown", "penup",
            "setpencolor", "setscreencolor", "forward", "right", "left", "back",
            "home", "clean", "clearscreen", "pendownp", "pencolor", "screencolor",
            "heading", "setheading", "towards" };
    
    public static final int NUMBER_OF_COMMANDS = 21;
    
    private int command;
    
    private int thisCommandArgcount;
    
    private String thisCommandName;
    
    public TurtleCommandSet(LogoScreen screen, int command)
    {
        super(screen);
        this.command = command;
        thisCommandArgcount = argcounts[command];
        thisCommandName = commandNames[command];
    }
    
    public int getArgumentCount()
    {
        return thisCommandArgcount;
    }
    
    public String getName()
    {
        return thisCommandName;
    }
    
    public Token run(InterpreterContext context, Token[] arguments)
    {
        if (command == GET_POS)
        {
            Point p = screen.getPos();
            double x = p.x;
            double y = p.y;
            return new ListToken(new Token[] { new WordToken(x), new WordToken(y) });
        }
        if (command == HIDE_TURTLE)
        {
            screen.hideTurtle();
        }
        else if (command == SHOW_TURTLE)
        {
            screen.showTurtle();
        }
        else if (command == SET_POS)
        {
            validateList(arguments[0]);
            ListToken list = (ListToken) arguments[0];
            if (list.getMembers().length != 2)
                throw new InterpreterException("A list of 2 elements was expected");
            validateWord(list.getMembers()[0]);
            validateWord(list.getMembers()[1]);
            WordToken l1 = (WordToken) list.getMembers()[0];
            WordToken l2 = (WordToken) list.getMembers()[1];
            double x = l1.getNumeric();
            double y = l2.getNumeric();
            screen.setPos(x, y);
        }
        else if (command == PEN_UP)
        {
            screen.penUp();
        }
        else if (command == PEN_DOWN)
        {
            screen.penDown();
        }
        else if (command == SET_PEN_COLOR)
        {
            validateList(arguments[0]);
            ListToken list = (ListToken) arguments[0];
            if (list.getMembers().length != 3)
                throw new InterpreterException("A list of 3 elements was expected");
            validateWord(list.getMembers()[0]);
            validateWord(list.getMembers()[1]);
            validateWord(list.getMembers()[2]);
            WordToken l1 = (WordToken) list.getMembers()[0];
            WordToken l2 = (WordToken) list.getMembers()[1];
            WordToken l3 = (WordToken) list.getMembers()[2];
            double rd = l1.getNumeric();
            double gd = l2.getNumeric();
            double bd = l3.getNumeric();
            int r = (int) rd;
            int g = (int) gd;
            int b = (int) bd;
            r &= 0xFF;
            g &= 0xFF;
            b &= 0xFF;
            r <<= 16;
            g <<= 8;
            int rgb = r | g | b;
            screen.setPenColor(rgb);
        }
        else if (command == SET_SCREEN_COLOR)
        {
            validateList(arguments[0]);
            ListToken list = (ListToken) arguments[0];
            if (list.getMembers().length != 3)
                throw new InterpreterException("A list of 3 elements was expected");
            validateWord(list.getMembers()[0]);
            validateWord(list.getMembers()[1]);
            validateWord(list.getMembers()[2]);
            WordToken l1 = (WordToken) list.getMembers()[0];
            WordToken l2 = (WordToken) list.getMembers()[1];
            WordToken l3 = (WordToken) list.getMembers()[2];
            double rd = l1.getNumeric();
            double gd = l2.getNumeric();
            double bd = l3.getNumeric();
            int r = (int) rd;
            int g = (int) gd;
            int b = (int) bd;
            r &= 0xFF;
            g &= 0xFF;
            b &= 0xFF;
            r <<= 16;
            g <<= 8;
            int rgb = r | g | b;
            screen.setScreenColor(rgb);
        }
        else if (command == FORWARD)
        {
            double amount = getDoubleCommandArg(arguments);
            screen.forward(amount);
        }
        else if (command == RIGHT)
        {
            double amount = getDoubleCommandArg(arguments);
            screen.right(amount);
        }
        else if (command == LEFT)
        {
            double amount = getDoubleCommandArg(arguments);
            screen.left(amount);
        }
        else if (command == BACK)
        {
            double amount = getDoubleCommandArg(arguments);
            screen.back(amount);
        }
        else if (command == HOME)
        {
            screen.home();
        }
        else if (command == CLEAN)
        {
            screen.clean();
        }
        else if (command == CLEARSCREEN)
        {
            screen.clearscreen();
        }
        else if (command == GET_HEADING)
        {
            return new WordToken(screen.getHeading());
        }
        else if (command == SET_HEADING)
        {
            validateWord(arguments[0]);
            screen.setHeading(((WordToken) arguments[0]).getNumeric());
        }
        else if (command == TOWARDS)
        {
            validateList(arguments[0]);
            ListToken list = (ListToken) arguments[0];
            if (list.getMembers().length != 2)
                throw new InterpreterException("A list of 2 elements was expected");
            validateWord(list.getMembers()[0]);
            validateWord(list.getMembers()[1]);
            WordToken l1 = (WordToken) list.getMembers()[0];
            WordToken l2 = (WordToken) list.getMembers()[1];
            double x = l1.getNumeric();
            double y = l2.getNumeric();
            double angle = screen.towards(x, y);
            return new WordToken(angle);
        }
        else
        {
            throw new InterpreterException("Unsupported turtle command set command "
                + command);
        }
        return null;
    }
    
    private double getDoubleCommandArg(Token[] arguments)
    {
        validateWord(arguments[0]);
        return ((WordToken) arguments[0]).getNumeric();
    }
    
}
