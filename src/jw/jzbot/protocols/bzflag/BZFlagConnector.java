package jw.jzbot.protocols.bzflag;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import jw.jzbot.protocols.bzflag.Packet.Layer;

/**
 * A class for connecting to a BZFlag server. Currently, only TCP packets are allowed. UDP
 * will be supported soon.
 * 
 * @author Alexander Boyd
 * 
 */
public class BZFlagConnector
{
    public static final int MsgAccept = 0x6163; // 'ac' //Implemented 2006-09-17
    public static final int MsgAdminInfo = 0x6169; // 'ai' //Implemented
    // 2007-08-10
    public static final int MsgAlive = 0x616c; // 'al'
    public static final int MsgAddPlayer = 0x6170; // 'ap' //Implemented
    public static final int MsgAutoPilot = 0x6175; // 'au'
    public static final int MsgCaptureFlag = 0x6366; // 'cf'
    public static final int MsgCustomSound = 0x6373; // 'cs'
    public static final int MsgCacheURL = 0x6375; // 'cu'
    public static final int MsgDropFlag = 0x6466; // 'df'
    public static final int MsgEnter = 0x656e; // 'en'
    public static final int MsgExit = 0x6578; // 'ex'
    public static final int MsgFlagUpdate = 0x6675; // 'fu'
    public static final int MsgFetchResources = 0x6672; // 'fr'
    public static final int MsgGrabFlag = 0x6766; // 'gf'
    public static final int MsgGMUpdate = 0x676d; // 'gm'
    public static final int MsgGetWorld = 0x6777; // 'gw'
    public static final int MsgGameSettings = 0x6773; // 'gs'
    public static final int MsgGameTime = 0x6774; // 'gt'
    public static final int MsgHandicap = 0x6863; // 'hc'
    public static final int MsgHit = 0x6869; // 'hi'
    public static final int MsgKilled = 0x6b6c; // 'kl'
    public static final int MsgKrbPrincipal = 0x6b70; // 'kp'
    public static final int MsgKrbTicket = 0x6b74; // 'kt'
    public static final int MsgLagState = 0x6c73; // 'ls'
    public static final int MsgMessage = 0x6d67; // 'mg' //Implemented
    // 2006-09-17
    public static final int MsgNewPlayer = 0x6e70; // 'np'
    public static final int MsgNewRabbit = 0x6e52; // 'nR'
    public static final int MsgNegotiateFlags = 0x6e66; // 'nf'
    public static final int MsgPause = 0x7061; // 'pa' //Implemented 2007-08-10
    public static final int MsgPlayerInfo = 0x7062; // 'pb' //Implemented
    // 2006-09-22
    public static final int MsgPlayerUpdate = 0x7075; // 'pu'
    public static final int MsgPlayerUpdateSmall = 0x7073; // 'ps'
    public static final int MsgQueryGame = 0x7167; // 'qg' //Implemented
    public static final int MsgQueryPlayers = 0x7170; // 'qp' //Implemented
    public static final int MsgReject = 0x726a; // 'rj'
    public static final int MsgRemovePlayer = 0x7270; // 'rp'
    public static final int MsgReplayReset = 0x7272; // 'rr'
    public static final int MsgShotBegin = 0x7362; // 'sb'
    public static final int MsgWShotBegin = 0x7762; // 'wb'
    public static final int MsgScore = 0x7363; // 'sc'
    public static final int MsgScoreOver = 0x736f; // 'so'
    public static final int MsgShotEnd = 0x7365; // 'se'
    public static final int MsgSuperKill = 0x736b; // 'sk'
    public static final int MsgSetTeam = 0x7374; // 'st'
    public static final int MsgSetVar = 0x7376; // 'sv' //Implemented
    public static final int MsgTimeUpdate = 0x746f; // 'to'
    public static final int MsgTeleport = 0x7470; // 'tp'
    public static final int MsgTransferFlag = 0x7466; // 'tf'
    public static final int MsgTeamUpdate = 0x7475; // 'tu' //Implemented
    public static final int MsgWantWHash = 0x7768; // 'wh' //Implemented
    // 2006-09-17
    public static final int MsgWantSettings = 0x7773; // 'ws'
    public static final int MsgPortalAdd = 0x5061; // 'Pa'
    public static final int MsgPortalRemove = 0x5072; // 'Pr'
    public static final int MsgPortalUpdate = 0x5075; // 'Pu'
    
    // world database codes
    public static final int WorldCodeHeader = 0x6865; // 'he'
    public static final int WorldCodeBase = 0x6261; // 'ba'
    public static final int WorldCodeBox = 0x6278; // 'bx'
    public static final int WorldCodeEnd = 0x6564; // 'ed'
    public static final int WorldCodeLink = 0x6c6e; // 'ln'
    public static final int WorldCodePyramid = 0x7079; // 'py'
    public static final int WorldCodeMesh = 0x6D65; // 'me'
    public static final int WorldCodeArc = 0x6172; // 'ar'
    public static final int WorldCodeCone = 0x636e; // 'cn'
    public static final int WorldCodeSphere = 0x7370; // 'sp'
    public static final int WorldCodeTetra = 0x7468; // 'th'
    public static final int WorldCodeTeleporter = 0x7465; // 'te'
    public static final int WorldCodeWall = 0x776c; // 'wl'
    public static final int WorldCodeWeapon = 0x7765; // 'we'
    public static final int WorldCodeZone = 0x7A6e; // 'zn'
    public static final int WorldCodeGroup = 0x6772; // 'gr'
    public static final int WorldCodeGroupDefStart = 0x6473; // 'ds'
    public static final int WorldCodeGroupDefEnd = 0x6465; // 'de'
    // ping packet sizes, codes and structure
    public static final int MsgPingCodeReply = 0x0303;
    public static final int MsgPingCodeRequest = 0x0404;
    // rejection codes
    public static final int RejectBadRequest = 0x0000;
    public static final int RejectBadTeam = 0x0001;
    public static final int RejectBadType = 0x0002;
    public static final int RejectBadEmail = 0x0003;
    public static final int RejectTeamFull = 0x0004;
    public static final int RejectServerFull = 0x0005;
    public static final int RejectBadCallsign = 0x0006;
    public static final int RejectRepeatCallsign = 0x0007;
    public static final int RejectRejoinWaitTime = 0x0008;
    public static final int RejectIPBanned = 0x0009;
    public static final int RejectHostBanned = 0x000A;
    public static final int RejectIDBanned = 0x000B;
    
    public static final int MsgLagPing = 0x7069; // implemented
    
    // /It may say "Message To ...", but you can also recieve messages from
    // teams!
    public static final int MsgToNoPlayer = 0xFF;// 255
    public static final int MsgToAllPlayers = 0xFE;// 254
    // you can only send commands to the server player, along with recieveing
    // messages from the server
    public static final int MsgToServerPlayer = 0xFD;// 253
    public static final int MsgToAdmins = 0xFC;// 252
    public static final int MsgToSpecial = 0xFB;// 251 Unused. Future
    // compatibilaty?
    public static final int MsgToRogueTeam = 0xFA;// 250
    public static final int MsgToRedTeam = 249;// 249
    public static final int MsgToGreenTeam = 248;// 248
    public static final int MsgToBlueTeam = 247;// 247
    public static final int MsgToPurpleTeam = 0xF6;// 246
    public static final int MsgToObserverTeam = 0xF5;// 245
    public static final int MsgToHuntedTeam = 0xF4;// 244
    public static final int LastRealPlayer = 0xF3;// 243: Don't ask, don't
    // know..
    public static final String[] teamChars = new String[]
    {
            "X", "R", "G", "B", "P", "O", "H"
    };
    
    public static final String[] teamNames = new String[]
    {
            "Rogue", "Red", "Green", "Blue", "Purple", "Observer", "Hunted"
    };
    
    private Socket socket;
    
    private int slot;
    
    public int getSlot()
    {
        return slot;
    }
    
    public void setSlot(int slot)
    {
        this.slot = slot;
    }
    
    public BZFlagConnector(String host, int port) throws IOException
    {
        this(new Socket(host, port), true);
    }
    
    /**
     * creates a new bzflag connector attached to the server specified. If an error
     * occures while connecting to that server, an exception is thrown.
     * 
     * @param hostname
     *            of the server to connect to
     * @param port
     *            the port to connect to
     */
    public BZFlagConnector(Socket socket, boolean doInitial) throws IOException
    {
        this.socket = socket;
        if (doInitial)
        {
            byte[] version = new byte[8];
            for (int i = 0; i < 8; i++)
            {
                version[i] = (byte) socket.getInputStream().read();
            }
            slot = socket.getInputStream().read();
            String versionString = new String(version, "ASCII");
            System.out.println("BZFlag Version: " + versionString);
        }
    }
    
    private Object sendLock = new Object();
    private Object receiveLock = new Object();
    
    /**
     * sends a packet.
     * 
     * @param packet
     *            the packet to send.
     */
    
    public void send(Packet packet) throws IOException
    {
        synchronized (sendLock)
        {
            if (packet.getLayer() != Packet.Layer.TCP)
                throw new UnsupportedOperationException(
                        "Only TCP packets are supported right now");
            socket.getOutputStream().write(
                    new byte[]
                    {
                            (byte) ((packet.getMessage().length & 0xFF00) >> 8),
                            (byte) (packet.getMessage().length & 0xFF),
                            (byte) ((packet.getType() & 0xFF00) >> 8),
                            (byte) (packet.getType() & 0xFF)
                    });
            // the first 2 bytes are the length of packet.getMessage(), the next
            // 2
            // bytes
            // are the packet's type
            // now we write the actual message
            socket.getOutputStream().write(packet.getMessage());
            socket.getOutputStream().flush();
        }
    }
    
    /**
     * Receives a packet. For TCP, this method blocks until a packet is ready. For UDP,
     * this method returns null if there isn't a packet ready. TODO: maybe have UDP use a
     * packet queue in this method so that it blocks?
     * 
     * @return
     * @throws IOException
     */
    public Packet receive(Layer layer) throws IOException
    {
        synchronized (receiveLock)
        {
            if (layer != Packet.Layer.TCP)
                throw new UnsupportedOperationException(
                        "Only TCP packets are supported right now");
            int b1 = socket.getInputStream().read();
            int b2 = socket.getInputStream().read();
            int b3 = socket.getInputStream().read();
            int b4 = socket.getInputStream().read();
            if (b1 == -1)
                throw new EOFException();
            Packet packet = new Packet();
            packet.setLayer(layer);
            packet.setType((b3 << 8) | b4);
            int length = (b1 << 8) | b2;
            byte[] bytes = new byte[length];
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());
            dataIn.readFully(bytes);
            packet.setMessage(bytes);
            return packet;
        }
    }
    
    public void close() throws IOException
    {
        socket.close();
    }
    
    public boolean isConnected()
    {
        return socket.isConnected();
    }
}
