""" jzbot
This is a plugin to implement the MSN protocol. Please note, this is NOT complete, and will *NOT* give you a working MSN bot.
Do NOT use unless you happen to be developing this plugin.
"""

# Java.
from jw.jzbot import protocols

# Python.
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
    
    def connect(self):
        user = self.connectionContext.getNick()
        password = self.connectionContext.getNick()
        host = self.connectionContext.getServer()
        port = self.connectionContext.getPort()
        
        if (not host) or (host == 'default'):
            host = 'messenger.hotmail.com'
        if (not port) or (port == 'default') or (port == 0):
            port = 1863
        
        self.notificationServer = ns.notificationServer(msnconnection=self, addr=(host, port), user=user)
        self.notificationServer.connect()
        return
    
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
    
    def isConnected(self):
        return False
    
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
    #thread = asyncthread.AsyncThread()
    #thread.start()
