package org.opengroove.jzbot.bzf;

import java.util.Map;

public class Server
{
    public String varSetPrefix = "";
    public String hostname;
    public String ip;
    private String version;
    public String description;
    public int gameStyle;
    public int maxShots;
    public int shakeWins;
    public int shakeTimeout;
    public int maxPlayerScore;
    public int maxTeamScore;
    public int maxTime;
    // the rest are 8s
    public int maxPlayers;
    public int rogueCount;
    public int rogueMax;
    public int redCount;
    public int redMax;
    public int greenCount;
    public int greenMax;
    public int blueCount;
    public int blueMax;
    public int purpleCount;
    public int purpleMax;
    public int observerCount;
    public int observerMax;
    /**
     * the number of players here, not including observers.
     */
    public int playerCount;
    /**
     * the number of people connected to this server, including observers.
     */
    public int joinedCount;
    private Map<String, String> targetVarMap;
    
    /**
     * parses a line from the list server and loads it into this server object.
     * 
     * @param line
     */
    public void parseLine(String line)
    {
        String[] splitLine = line.split(" ", 5);
        hostname = splitLine[0];
        version = splitLine[1];
        String gamedata = splitLine[2];
        ip = splitLine[3];
        if (splitLine.length > 4)
            description = splitLine[4];
        else
            description = "";
        gameStyle = gparse16(0, gamedata);
        maxShots = gparse16(4, gamedata);
        shakeWins = gparse16(8, gamedata);
        shakeTimeout = gparse16(12, gamedata);
        maxPlayerScore = gparse16(16, gamedata);
        maxTeamScore = gparse16(20, gamedata);
        maxTime = gparse16(24, gamedata);
        maxPlayers = gparse8(28, gamedata);
        rogueCount = gparse8(30, gamedata);
        rogueMax = gparse8(32, gamedata);
        redCount = gparse8(34, gamedata);
        redMax = gparse8(36, gamedata);
        greenCount = gparse8(38, gamedata);
        greenMax = gparse8(40, gamedata);
        blueCount = gparse8(42, gamedata);
        blueMax = gparse8(44, gamedata);
        purpleCount = gparse8(46, gamedata);
        purpleMax = gparse8(48, gamedata);
        observerCount = gparse8(50, gamedata);
        observerMax = gparse8(52, gamedata);
        playerCount = rogueCount + redCount + greenCount + blueCount + purpleCount;
        joinedCount = playerCount + observerCount;
    }
    
    private int gparse8(int index, String data)
    {
        return gparse(index, 2, data);
    }
    
    private int gparse16(int index, String data)
    {
        return gparse(index, 4, data);
    }
    
    private int gparse(int index, int length, String data)
    {
        String s = data.substring(index, index + length);
        return Integer.parseInt(s, 16);
    }
    
    public void loadIntoVars(Map<String, String> vars, String prefix)
    {
        varSetPrefix = prefix;
        targetVarMap = vars;
        doLoadVars();
    }
    
    private void doLoadVars()
    {
        var("bluecount", blueCount);
        var("bluemax", blueCount);
        var("description", blueCount);
        var("gamestyle", blueCount);
        var("greencount", blueCount);
        var("greenmax", greenMax);
        var("hostname", hostname);
        var("ip", ip);
        var("joinedcount", joinedCount);
        var("maxplayers", maxPlayers);
        var("maxplayerScore", maxPlayerScore);
        var("maxshots", maxShots);
        var("maxteamscore", maxTeamScore);
        var("maxtime", maxTime);
        var("observercount", observerCount);
        var("observermax", observerMax);
        var("playercount", playerCount);
        var("purplecount", purpleCount);
        var("purplemax", purpleMax);
        var("redcount", redCount);
        var("redmax", redMax);
        var("roguecount", rogueCount);
        var("roguemax", rogueMax);
        var("shaketimeout", shakeTimeout);
        var("shakewins", shakeWins);
    }
    
    private void var(String name, String value)
    {
        targetVarMap.put(varSetPrefix + "-" + name, value);
    }
    
    private void var(String name, int value)
    {
        targetVarMap.put(varSetPrefix + "-" + name, String.valueOf(value));
    }
    
}
