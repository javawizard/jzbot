package org.opengroove.jw.jmlogo.lang;

import java.util.Vector;

/**
 * A class to iterate over a list token. Methods are provided to get the next
 * element in the list, rollback to the previous element, and peek at an
 * element. This class will be used to parse logo commands.
 * 
 * @author Alexander Boyd
 * 
 */
public class TokenIterator
{
    private Vector tokens = new Vector();
    private int index;
    
    public TokenIterator(ListToken token)
    {
        for (int i = 0; i < token.getMembers().length; i++)
        {
            tokens.addElement(token.getMembers()[i]);
        }
    }
    
    public Token read()
    {
        if (index >= tokens.size())
            return null;
        return (Token) tokens.elementAt(index++);
    }
    
    public Token peek()
    {
        if (index >= tokens.size())
            return null;
        return (Token) tokens.elementAt(index);
    }
    
    public void rollback()
    {
        if (index >= 0)
            index--;
    }
    
    public boolean has()
    {
        return index < tokens.size();
    }
    
    public void insert(Token token)
    {
        tokens.insertElementAt(token, index);
    }
    
    public void insert(Token[] toAdd)
    {
        for (int i = toAdd.length - 1; i >= 0; i--)
        {
            tokens.insertElementAt(toAdd[i], index);
        }
    }
    
    public int getIndex()
    {
        return index;
    }
    
    /**
     * Removes the element just before the current index, and each element
     * before that, back to and including the element by the index specified.
     * The current index is then set to the index specified, and the tokens
     * specified are inserted at that index.
     * 
     * @param index
     * @param tokens
     */
    public void replaceBackWith(int ti, Token[] toAdd)
    {
        if (ti >= index)
            throw new IllegalArgumentException("rbw-ci:" + index + ",ti:" + ti + ",size:"
                + tokens.size());
        for (int i = ti; i < index; i++)
        {
            tokens.removeElementAt(ti);
        }
        index = ti;
        insert(toAdd);
    }
}
