
from __future__ import with_statement

""" jzbot
This plugin starts a socket listening on a specific port. When a connection
on that port is received, the plugin starts a Python interactive console on
the socket.
"""

from java.util import Properties
from java.net import ServerSocket, InetAddress
from java.lang import Thread
from java.io import BufferedReader, InputStreamReader, ByteArrayOutputStream
from java.io import File, FileInputStream, DataInputStream, DataOutputStream
from java.lang import String
from org.python.util import InteractiveConsole
from org.python.core import Py
from threading import RLock
from jw.jzbot import JZBot
import traceback

sessions = []

class Console(InteractiveConsole):
    def __init__(self, reader, writer, read_lock, write_lock):
        self.reader = reader
        self.writer = writer
        self.read_lock = read_lock
        self.write_lock = write_lock
    
    def write(self, data):
        data = str(data)
        with self.write_lock:
            self.writer.writeShort(2)
            self.writer.writeUTF(data)
            self.writer.flush()
    
    def writeline(self, data):
        with self.write_lock:
            self.write(data)
            self.write("\n")
    
    def raw_input(self, prompt):
        prompt = str(prompt)
        with self.write_lock:
            self.writer.writeShort(3)
            self.writer.writeUTF(prompt)
            self.writer.flush()
        with self.read_lock:
            result = self.reader.readUTF()
        if result is None:
            raise Exception("End-of-stream detected")
        return result
    
    def showexception(self, exception):
        stream = ByteArrayOutputStream()
        result = traceback.format_exception(exception.type, exception.value,
                                            exception.traceback)
        result = "".join(result)
        self.write(result)


class HandlerThread(Thread):
    def __init__(self, socket):
        self.socket = socket
        self.socket_in = socket.getInputStream()
        self.socket_out = socket.getOutputStream()
        self.reader = DataInputStream(self.socket_in)
        self.writer = DataOutputStream(self.socket_out)
        self.read_lock = RLock()
        self.write_lock = RLock()
        self.console = Console(self.reader, self.writer,
                               self.read_lock, self.write_lock)
        self.locals = self.console.getLocals()
        self.locals["write"] = self.console.write
        self.locals["writeline"] = self.console.writeline
        self.locals["exit"] = self.client_disconnect
        self.locals["quit"] = self.client_disconnect
        self.locals["restart"] = JZBot.restart
        self.locals["shutdown"] = JZBot.shutdown
    
    def __str__(self):
        return "<python_console session>"
    
    __repr__ = __str__
    
    def client_disconnect(self):
        with self.write_lock:
            self.writer.writeShort(4)
            self.writer.flush()
        self.socket.close()
    
    def run(self):
        sessions.append(self)
        try:
            self.console.write(
"""JZBot Python console at your service.
======== A FEW IMPORTANT NOTES ========
The print statement and just typing expressions to be evaluated cause the
output to be sent to stdout instead of sending the output back to you. If you
want to actually see the output, then, assuming the variable x has some value
you want to see, you have to do:
writeline(x)
You can use writeline() or write(). They both accept any python object and
convert it to a string with str(). writeline sends a newline afterward.
History (a.k.a. the up-arrow) does not currently work.
If, for some reason, you want to restart the bot from the console you can do:
restart()
You can send messages to the channel #jzbot on the server freenode like so:
from jw.jzbot.scope import ScopeManager
jzbot_channel = ScopeManager.getMessenger("@freenode#jzbot")
jzbot_channel.sendMessage("Hello everyone. How are you?")
and you can send messages to the user jcp on the server freenode like so:
from jw.jzbot.scope import ScopeManager
jcp = ScopeManager.getMessenger("@freenode!jcp")
jcp.sendMessage("Hi jcp. How's it going?")
======== END OF IMPORTANT NOTES ========
(Scroll back up if some of it went off screen. It's important.)

Anyway, that's about it. Have fun!
""")
            self.console.interact()
        except:
            traceback.print_exc()
        try:
            self.socket.close()
        except:
            traceback.print_exc()
        sessions.remove(self)


def init(context):
    if not File("storage/python_console.props").exists():
        context.log("storage/python_console.props does not exist")
        return
    context.log("storage/python_console.props exists; proceeding with load")
    props = Properties()
    props.load(FileInputStream(File("storage/python_console.props"))) 
    server = ServerSocket(int(props["port"]), 20, 
                          InetAddress.getByName(props["host"])
                          if "host" in props else None)
    class ServerThread(Thread):
        def run(self):
            while not server.isClosed():
                socket = server.accept()
                HandlerThread(socket).start()
    ServerThread().start()



