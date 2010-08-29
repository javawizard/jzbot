import threading
import socket
from collections import deque

class recvThread (threading.Thread):
    def __init__(self, socket, callback, callback_err=None):
        threading.Thread.__init__(self)
        self.socket = socket
        self.callback = callback
        self.callback_err = callback_err or callback
        self.buffer = ""
        self.queue = deque()
        self.active = False

    def pop(self):
        if len(self.queue) > 0:
            return self.queue.popleft()
        else:
            return None
    
    def run(self):
        self.active = True
        while self.active:
            try:
                buffer = self.socket.recv(8192)
            except:
                self.active = False
            if buffer == '':
                if self.callback_err is not None:
                    self.callback_err()
                break
            self.buffer += buffer
            while "\r" in self.buffer:
                line, _, restbuffer = self.buffer.partition("\r")
                if restbuffer[0] == '\n':
                    restbuffer = restbuffer[1:]
                self.buffer = restbuffer
                self.queue.append(line)
                self.callback()

class sendThread(threading.Thread):
    def __init__(self, socket):
        threading.Thread.__init__(self)
        self.socket = socket
        self.queue = deque()
        self.active = False
    
    def append(self, line):
        self.queue.append(line)
    
    def run(self):
        self.active = True
        while self.active:
            if self.queue:
                line = str(self.queue.popleft())
                try:
                    sent=self.socket.send(line)
                except:
                    self.active = False
                    #raise
                if sent > 0:
                    self.queue.appendleft(line[sent:])

class baseConnection (object):
    def __init__(self, msnconnection, addr, config):
        self.msnconnection = msnconnection
        self.addr = addr
        self.config = config
        self.socket = None
        
        self.transfer = False
        
        self._recv = None
        self._send = None
        self._trid = 0
        
        self.connected = False
        self.is_connecting = True
        return
    
    def conn_close(self):
        if self.transfer:
            self._recv.active = False
            self._send.active = False
            
            self.connect()
        else:
            self.connected = False
            return False
    
    def isConnected(self):
        return self.connected
    
    def connect(self):
        print 'MSN Connect: %s:%d' % self.addr
        self.socket = socket.socket()
        self._recv = recvThread(self.socket, self.recv_callback,
            self.conn_close)
        self._send = sendThread(self.socket)
        
        self.connected = False
        self.send(['VER', 'MSNP8'], True)
        
        self.socket.connect(self.addr)
        self._recv.start()
        self._send.start()
    
    def recv_callback(self):
        return "MUST BE IMPLEMENTED IN SUBCLASS."

    def send(self, args, trid=False, newline=True):
        "Send a protocol line to the remote server."
        if trid:
            self._trid += 1
            raw_line = '%s %d %s' % (args[0], self._trid, ' '.join(args[1:]))
        else:
            raw_line = ' '.join(args[1:])
        if newline:
            raw_line = raw_line + '\r\n'
        print 'MSN Send', raw_line
        self._send.queue.append(raw_line)
        return
    
    def recv(self):
        "Receive a protocol line from the remote server."
        return self._recv.queue.popleft()
    
    def disconnect(self):
        "Disconects you from the remote server."
        print 'MSN Connect: %s:%d' % self.addr
        return
