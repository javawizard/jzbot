package jw.jzbot.psystem.server;

import java.net.Socket;

import jw.jzbot.psystem.client.*;

import jw.jzbot.rpc.RPCLink;

public class LoadedPlugin
{
    public String name;
    public RPCLink<PluginClientInterface> link;
    public Socket socket;
}
