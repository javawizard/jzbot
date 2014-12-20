package net.sf.opengroove.common.utils;

/**
 * This class contains utility methods for parsing and generating user ids.
 * 
 * @author Alexander Boyd
 * 
 */
public class Userids
{
    
    /**
     * Converts the username and realm specified to a userid. Userids take the
     * format realm:username , so a user who's username is javawizard and who's
     * realm is opengroove.org would have the userid opengroove.org:javawizard .
     * 
     * @param realm
     *            The name of the realm
     * @param username
     *            The username
     * @return The corresponding userid
     */
    public static String toUserid(String realm,
        String username)
    {
        return realm + ":" + username;
    }
    
    /**
     * Extracts the name of the user's realm from the userid. If the userid does
     * not have a realm component, or if the userid is a username, the empty
     * string is returned.
     * 
     * @param userid
     *            The userid
     * @return The realm portion of the userid specified
     */
    public static String toRealm(String userid)
    {
        if (userid == null)
            return "";
        if (!userid.contains(":"))
            return "";
        return userid.split("\\:")[0];
    }
    
    /**
     * Extracts the user's username from the userid. If the userid specified
     * does not have a username component (e.g. "example.com:"), the empty
     * string is returned. If the userid specified is a username, then it is
     * returned without modification.
     * 
     * @param userid
     *            The userid
     * @return The username portion of the userid specified
     */
    public static String toUsername(String userid)
    {
        if (userid.indexOf(":") == -1)
            return userid;
        return userid.split("\\:")[1];
    }
    
    /**
     * Returns a new userid that is equal to the one specified, but with the
     * realm set to the realm specified.
     * 
     * @param userid
     * @param realm
     * @return
     */
    public static String setRealm(String userid,
        String realm)
    {
        return toUserid(realm, toUsername(userid));
    }
    
    /**
     * Returns a new userid that is equal to the one specified, but with the
     * username set to the username specified.
     */
    
    public static String setUsername(String userid,
        String username)
    {
        return toUserid(toRealm(userid), username);
    }
    
    /**
     * Resolves a username to a userid, relative to another username. More
     * precisely, if <code>toResolve</code> is already a userid, then it is
     * returned without modification. If it is a username, then it is made into
     * a userid by extracting the realm from <code>relativeTo</code> and
     * adding it to the username. For example:<br/><br/>
     * 
     * resolveTo("realm:username","differentrealm:differentusername")<br/><br/>
     * 
     * would evaluate to "realm:username", but:<br/><br/>
     * 
     * resolveTo("username","differentrealm:differentusername")<br/><br/>
     * 
     * would evaluate to "differentrealm:username".
     * 
     * @param toResolve
     *            The userid or username to resolve
     * @param relativeTo
     *            The userid that <code>toResolve</code> should be interpreted
     *            relative to if <code>toResolve</code> is a username
     * @return The relativized userid.
     */
    public static String resolveTo(String toResolve,
        String relativeTo)
    {
        checkUseridOrUsername(toResolve);
        checkUserid(relativeTo);
        if (isUserid(toResolve))
            return toResolve;
        assert isUsername(toResolve);
        return setUsername(relativeTo, toResolve);
    }
    
    /**
     * Converts a userid (or a username) to a username relative to the realm
     * specified, or leaves it the same if the userid specified is different
     * than the realm specified. This could be considered the opposite of
     * {@link #resolveTo(String, String)}, except that it takes a realm as it's
     * input instead of a userid.<br/><br/>
     * 
     * Another way to think of this method would be that it removes the realm
     * component of the userid (thereby reducing it to a username) if the realm
     * component matches the realm specified by <code>relativeTo</code>, or
     * leaves it alone if the input is a username.<br/><br/>
     * 
     * Here are some examples:<br/><br/>
     * 
     * <table border="0" cellspacing="0" cellpadding="2">
     * <tr>
     * <th>toRelativize</th>
     * <th>relativeTo</th>
     * <th>result</th>
     * </tr>
     * <tr>
     * <td>realm:username</td>
     * <td>realm</td>
     * <td>username</td>
     * </tr>
     * <tr>
     * <td>username</td>
     * <td>realm</td>
     * <td>username</td>
     * </tr>
     * <tr>
     * <td>realm:username</td>
     * <td>dfferentrealm</td>
     * <td>realm:username</td>
     * </tr>
     * <tr>
     * <td></td>
     * <td></td>
     * <td></td>
     * </tr>
     * <tr>
     * <td></td>
     * <td></td>
     * <td></td>
     * </tr>
     * </table>
     * 
     * @param toRelativize
     *            The userid to convert to relative
     * @param relativeTo
     *            The realm to relativize the userid to
     * @return The relativized userid
     */
    public static String relativeTo(String toRelativize,
        String relativeTo)
    {
        if (isUsername(toRelativize))
            return toRelativize;
        if (isUserid(toRelativize)
            && toRealm(toRelativize).equalsIgnoreCase(
                relativeTo))
            return toUsername(toRelativize);
        if (isUserid(toRelativize))
            return toRelativize;
        throw new IllegalArgumentException(
            "The argument provided as toRelativize ("
                + toRelativize + ") is not valid");
    }
    
    public static void checkUserid(String userid)
    {
        if (!isUserid(userid))
            throw new IllegalArgumentException("The value "
                + userid
                + " is not a userid, as was expected");
    }
    
    public static void checkUseridOrUsername(
        String useridOrUsername)
    {
        if (!(isUserid(useridOrUsername) || isUsername(useridOrUsername)))
            throw new IllegalArgumentException(
                "The value "
                    + useridOrUsername
                    + " is not a userid or a username, as was expected");
    }
    
    public static boolean isUserid(String userid)
    {
        return userid.contains(":")
            && userid.indexOf(":") == userid
                .lastIndexOf(":");
    }
    
    public static boolean isUsername(String username)
    {
        return !username.contains(":");
    }
    
}
