package org.opengroove.jw.jmlogo.lang.commands.turtle;

import org.opengroove.jw.jmlogo.LogoScreen;
import org.opengroove.jw.jmlogo.lang.Command;

public abstract class ScreenCommand extends Command
{
    protected LogoScreen screen;
    
    public ScreenCommand(LogoScreen screen)
    {
        this.screen = screen;
    }
}
