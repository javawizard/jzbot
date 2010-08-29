
""" jzbot
This plugin starts a socket listening on a specific port. When a connection
on that port is received, the plugin starts a Python interactive console on
the socket.
"""

from java.util import Properties
from java.net import ServerSocket, InetAddress
from java.lang import Thread
from java.io import BufferedReader, InputStreamReader, ByteArrayOutputStream
from java.io import File, FileInputStream
from java.lang import String
from org.python.util import InteractiveConsole
from org.python.core import Py
import traceback 


class Console(InteractiveConsole):
    def __init__(self, reader, writer):
        self.reader = reader
        self.writer = writer
    
    def write(self, data):
        data = str(data)
        self.writer.write(data)
        self.writer.flush()
    
    def writeline(self, data):
        self.write(data)
        self.write("\n")
    
    def raw_input(self, prompt):
        self.write(prompt)
        result = self.reader.readLine()
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
        self.reader = BufferedReader(InputStreamReader(self.socket_in))
        self.writer = self.socket_out
        self.console = Console(self.reader, self.writer)
        self.console.getLocals()["write"] = self.console.write
        self.console.getLocals()["writeline"] = self.console.writeline
        self.console.getLocals()["exit"] = self.socket.close
    
    def run(self):
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
from jw.jzbot import JZBot
JZBot.restart()
and you can send messages to the channel #jzbot on the server freenode like so:
connection = JZBot.getConnection("freenode")
connection.sendMessage("#jzbot", "Hello everyone. How are you?")

Anyway, that's about it. Have fun!
""")
            self.console.interact()
        except:
            traceback.print_exc()
        try:
            self.socket.close()
        except:
            traceback.print_exc()


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



