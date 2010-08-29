""" jzbot
This is a plugin to implement the MSN protocol. Please note, this is NOT complete, and will *NOT* give you a working MSN bot.
Do NOT use unless you happen to be developing this plugin.
"""

# Java.
from jw.jzbot import protocols
from jw.jzbot import configuration
import java.lang

# Python.
import time
import ns
import sb

class MSNConnection(protocols.Connection):
    def init(self, connectionContext):
        self.connectionContext = connectionContext
        self.notificationServer = None
        self.switchboards = []
        return
    
    def changeNick(self, newnick):
        return

    def isConnected(self):
        if self.notificationServer is not None:
            return self.notificationServer.connected
        else:
            return False
    
    def connect(self):
        user = self.connectionContext.getNick()
        password = self.connectionContext.getNick()
        host = self.connectionContext.getServer()
        port = self.connectionContext.getPort()
        
        nexus = configuration.Configuration.getText('server', 'msn/nexus')
        nexus_ssl = bool(configuration.Configuration.getBool('server', 'msn/nexus-ssl'))
        
        if (not host) or (host == 'default'):
            host = 'messenger.hotmail.com'
        if (not port) or (port == 'default') or (port == 0):
            port = 1863
        
        config = {'nexus': nexus, 'nexus-ssl': nexus_ssl, 'user': user, 'password': password}
        
        self.notificationServer = ns.notificationServer(msnconnection=self, addr=(host, port), user=user, config=config)
        self.notificationServer.connect()

        for x in xrange(30):
            if self.notificationServer.connected:
                return
            if self.notificationServer.connected is None:
                raise java.lang.IllegalStateException
            
            time.sleep(1)
        self.notificationServer.is_connecting = False
        raise java.lang.IllegalStateException("Took too long to connect.")
    
    def discard(self):
        return
    
    def disconnect(self, message):
        self.notificationServer.disconnect()
        return
    
    def getChannels(self):
        return []
    
    def getNick(self):
        return 'nick'
    
    def getOutgoingQueueSize(self):
        return 0
    
    def getProtocolDelimitedLength(self):
        return 0
    
    def getUsers(self, channel):
        return None
    
    def joinChannel(self, channel):
        return
    
    def kick(self, channel, user, reason):
        return
    
    def likesPastebin(self):
        return False
    
    def partChannel(self, channel, reason):
        return
    
    def processProtocolFunction(self, sink, arguments, context):
        return
    
    def sendAction(self, target, message):
        return
    
    def sendInvite(self, nick, channel):
        return
    
    def sendMessage(self, target, message):
        return
    
    def sendNotice(self, target, message):
        return
    
    def setEncoding(self, string):
        return
    
    def setLogin(self, nick):
        return
    
    def setMessageDelay(self, ms):
        return
    
    def setMode(self, channel, mode):
        return
    
    def setName(self, nick):
        return
    
    def setTopic(self, channel, topic):
        return
    
    def setVersion(self, string):
        return
    
    def supportsMessageDelay(self):
        return False


class MSNProtocol(protocols.Protocol):
    def getName(self):
        return "msn"
    
    def createConnection(self):
        return MSNConnection()

def init (pluginContext):
    protocols.ProtocolManager.installProtocol(MSNProtocol())
    configuration.Configuration.register('@msn', 'msn', 'MSN configuration settings', configuration.Configuration.VarType.folder, None)
    configuration.Configuration.register('@msn', 'msn/nexus', 'Nexus used to Authenticate to the MSN network.', configuration.Configuration.VarType.text, 'https://nexus.passport.com/rdr/pprdr.asp')
    configuration.Configuration.register('@msn', 'msn/nexus-ssl', 'If SSL is *required* to authenticate to the Nexus.', configuration.Configuration.VarType.bool, "1")
