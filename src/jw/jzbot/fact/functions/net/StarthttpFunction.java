package jw.jzbot.fact.functions.net;

import jw.jzbot.JZBot;
import jw.jzbot.fact.ArgumentList;
import jw.jzbot.fact.FactContext;
import jw.jzbot.fact.Function;
import jw.jzbot.utils.Pastebin;
import jw.jzbot.utils.Pastebin.Duration;

public class StarthttpFunction extends Function
{
    
    @Override
    public String evaluate(ArgumentList arguments, FactContext context)
    {
        int port = Integer.parseInt(arguments.get(0));
        String factoid = arguments.get(1);
        JZBot.startHttpServer(port, factoid);
        return "";
    }
    
    @Override
    public String getHelp(String topic)
    {
        // String url = "http://pastebin.com/"
        // + Pastebin.createPost("jzbot-help", getExtendedHelp(),
        // Duration.DAY, null);
        String url = "(TODO, add into StarthttpFunction.java)";
        return "Syntax: {{starthttp||<port>||<factoid>}} -- Starts an HTTP server on the "
                + "specified port. See " + url + " for more info.";
    }
    
    private String getExtendedHelp()
    {
        return "JZBot supports starting an HTTP server from within the bot. This "
                + "allows the bot to serve up web pages that could contain, for "
                + "example, statistics on the kind of conversation that goes on at "
                + "channels which the bot joins.\n"
                + "The list of ports that servers can run on is specified by a file "
                + "called \"serverports.txt\" that resides in the storage folder. If "
                + "such a file is not present, HTTP servers are disabled. If the file "
                + "is present, it contains a single regular expression. Any port number "
                + "that matches this regular expression is considered a valid port on "
                + "which to listen.\n"
                + "Additionally, the file \"maxservers.txt\" can be present. If it is,"
                + " it should contain a decimal number, which is the maximum number of "
                + "servers that can be running concurrently. If there is no such file, "
                + "a default of 20 is used.\n"
                + "Servers can be started with the {{starthttp}} function (which could "
                + "potentially be called from the server's _onready factoid). This "
                + "function takes two arguments: <port> and <factoid>. <port> is the "
                + "port to listen on. <factoid> is a factoid that will be run whenever "
                + "an HTTP connection is received on the port specified.\n"
                + "When <factoid> is run, these local variables will be present:\n"
                + "http-url: The url of the path that the user is requesting.\n"
                + "http-method: The method used to make the request. Currently, "
                + "GET and POST are supported.\n"
                + "http-param-<name>: For each HTTP parameter, one of these "
                + "local variables is present. <name> is the name of the parameter.\n"
                + "http-header-<name>: For each HTTP header, one of these "
                + "local variables is present. <name> is the name of the header. "
                + "Header names are always lower-case.\n\n"
                + "<factoid> must be a global factoid. The factoid can also set a "
                + "number of local variables indicating how the response should be "
                + "handled: \n"
                + "http-status: The status code that the response should have, "
                + "currently one of ok, redirect, forbidden, notfound, badrequest, "
                + "internalerror, or notimplemented. The default is \"ok\" if you "
                + "don't set a value for this local variable.\n"
                + "http-resource: Indicates that, instead of sending the text that "
                + "the factoid evaluates to back as a response, the resource (see "
                + "the {{getresource}} function for information on resources) whose "
                + "name is the value of this local variable should be served instead. "
                + "This is useful when you want your server to serve images and the like. "
                + "Headers, the content type, and the status code are still specified "
                + "in the factoid. {{extensiontype}} can be used to look up the "
                + "content type for a number of known file extensions.\n"
                + "http-content-type: The type of content you're sending to the "
                + "client. If you don't set this, it defaults to \"text/html\". "
                + "This local variable is particularly relevant when http-resource "
                + "is used, as the content type will not be automatically detected "
                + "when using http-resource.\n"
                + "http-res-header-<name>: You can have as many of these as you want. "
                + "Each one sets the HTTP header named by <name> to be the value of the "
                + "local variable.\n\n"
                + "If http-resource is not specified, then the HTTP client gets sent "
                + "whatever the factoid outputs as the response.\n\n"
                + "For example, you could use a factoid with the text \""
                + "<html><body>Hello world! Param test is %http-param-test%."
                + "</body></html>\" to allow users of your server to see a simple "
                + "message and the value of the \"test\" query parameter. If a user "
                + "were to then visit your page with \"?test=something\" on the end of "
                + "their URL, they would see \"Hello world! Param test is something.\" "
                + "in their page.\n\n"
                + "When you're done with a server, you can use {{stophttp}} to shut "
                + "it down. You can also use {{listhttp}} to get a list of all servers "
                + "that you're running, in case you forgot their port numbers. "
                + "Reconnecting the bot (with ~reconnect) shuts down all servers.\n";
    }
    
}
