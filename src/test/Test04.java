package test;

public class Test04
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.out
                .println(("\"joined\", \"left\", \"nick\", \"message\", or \"action\" right now. <time> is the "
                        + "number of milliseconds since the epoch. <source> is the nick that "
                        + "caused the action to occur. <details> varies depending on the action. "
                        + "For mode, details is the IRC-format mode string that happened, such as "
                        + "\"+o jcp\". For kick, details is the name of the person that was kicked, "
                        + "a space, and the reason that the person was kicked.\n").length());
    }
}
