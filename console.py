
from java.net import Socket
from java.io import DataInputStream, DataOutputStream
import sys
import jline
from traceback import print_exc

if len(sys.argv) < 3:
    print "You need to specify the host and port, in that order, to"
    print "connect to."
    sys.exit()

print "Connecting..."
socket = Socket(sys.argv[1], int(sys.argv[2]))
print "Connected! One moment..."
in_stream = DataInputStream(socket.getInputStream())
out_stream = DataOutputStream(socket.getOutputStream())

try:
    while not socket.isClosed():
        mode = in_stream.readShort()
        if mode == 2: # normal text write
            sys.stdout.write(in_stream.readUTF())
            sys.stdout.flush()
        elif mode == 3: # raw_input with prompt
            result = raw_input(in_stream.readUTF())
            out_stream.writeUTF(result);
            out_stream.flush()
        elif mode == 4: # Exiting
            break
        else:
            print "Invalid mode received: " + str(mode)
            break
except:
    print_exc()

if not socket.isClosed():
    socket.close()

print "Connection closed."

sys.exit()

