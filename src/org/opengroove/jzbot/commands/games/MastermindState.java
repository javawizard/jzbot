package org.opengroove.jzbot.commands.games;

import java.util.ArrayList;

public class MastermindState
{
    public int guesses;
    public ArrayList<Integer> correct = new ArrayList<Integer>();
    public long changed;
}
