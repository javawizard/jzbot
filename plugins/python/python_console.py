
""" jzbot
This plugin starts a socket listening on a specific port. When a connection
on that port is received, the plugin starts a Python interactive console on
the socket.
"""

from java.util import Properties
from java.net import ServerSocket, InetAddress
from java.lang import Thread
from java.io import BufferedReader, InputStreamReader, ByteArrayOutputStream
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
When you print stuff with the print statement, or when you just type stuff as
an expression to evaluate, the output gets sent to the bot's standard out,
not across this socket to you. In other words, the second line of something
like this:
x = 5
x
would ordinarily cause "5" to appear on the console. In actuality, "5" will
end up appearing on the bot's standard out, and nothing will get sent back to
you. If you want to see any output, you need to use two functions provided to
you: write and writeline. Both of them accept any object as an argument and
convert it to a string, then send it back to you across the wire. writeline
appends a newline to the end of the string before sending it.
Other python plugins are available as modules named after the plugin. For
example, if you've currently loaded a python plugin named example_python,
you can get access to it as a module by importing example_python.
If, for some reason, you want to restart the bot from the console you can do:
from jw.jzbot import JZBot
JZBot.restart()
and you can send messages to the channel #jzbot on the server freenode like so:
from jw.jzbot import JZBot
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
#    server = ServerSocket(24680)
    class ServerThread(Thread):
        def run(self):
            while not server.isClosed():
                socket = server.accept()
                HandlerThread(socket).start()
    ServerThread().start()



