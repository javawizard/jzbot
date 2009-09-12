package org.opengroove.jw.jmlogo;

/**
 * Represents a logo screen, a device that logo commands can draw onto. Right
 * now, methods are only provided for interacting with one turtle.<br/>
 * <br/>
 * 
 * Typically, there is one method for each logo command that interacts with the
 * turtle.<br/>
 * <br/>
 * 
 * If you're looking to implement your own LogoScreen, but you don't want to
 * have to add all of the mathematics behind all of the methods, you could take
 * a look at the source for {@link LogoCanvas}. LogoCanvas is an implementation
 * of LogoScreen for mobile phones, and you could probably get most of the
 * mathematics and turtle tracking functionality from that class.
 * 
 * @author Alexander Boyd
 * 
 */
public interface LogoScreen
{
    public Point getPos();
    
    public void hideTurtle();
    
    public void showTurtle();
    
    public void setPos(double x, double y);
    
    public void penDown();
    
    public void penUp();
    
    public void setPenColor(int rgb);
    
    public void setScreenColor(int rgb);
    
    public void forward(double length);
    
    public void right(double degrees);
    
    public void left(double degrees);
    
    public void back(double degrees);
    
    public void home();
    
    public void clean();
    
    public void clearscreen();
    
    public boolean penDownP();
    
    public int getPenColor();
    
    public int getScreenColor();
    
    public double getHeading();
    
    public void setHeading(double heading);
    
    public double towards(double x, double y);
}
