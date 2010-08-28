import threading
import socket
from collections import deque
#from auth import tweener

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

class notificationServer (object):
    def __init__(self, msnconnection, addr, user):
        self.msnconnection = msnconnection
        self.addr = addr
        self.username = user
        self.socket = None
        
        self.transfer = False
        
        self.__recv = None
        self.__send = None
        self.__trid = 0
        
        self.connected = False
        self.is_connecting = True
        return
    
    def conn_close(self):
        if self.transfer:
            self.__recv.active = False
            self.__send.active = False
            
            self.connect()
        else:
            self.connected = False
            return False
    
    def isConnected(self):
        return self.connected
    
    def connect(self):
        self.socket = socket.socket()
        self.__recv = recvThread(self.socket, self.recv_callback,
            self.conn_close)
        self.__send = sendThread(self.socket)
        
        self.connected = False
        self._send(['VER', 'MSNP8'], True)
        
        self.socket.connect(self.addr)
        self.__recv.start()
        self.__send.start()
    
    def recv_callback(self):
        line = self._recv()
        if not line:
            return
        words = line.split(' ')
        print 'MSN Recv', line
        
        if words[0] == 'VER':
            versions = words[2:]
            assert '0' not in versions
            assert 'MSNP8' in versions
            self._send(['CVR', '0x0409', 'python', 'jzbot', 'java',
                'JZBOT', '42', 'MSMSGS', self.username], True)
            return
        if words[0] == 'CVR':
            self._send(['USR', 'TWN', 'I', self.username], True)
            return
        if words[0] == 'XFR':
            assert words[2] in ('NS')
            assert words[4] == '0'
            self.transfer = True
            host,port = words[3].split(':')
            port = int(port)
            
            self.addr = (host, port)
            return
        
        if words[0] == 'USR':
            assert words[2] == 'TWN'
            assert words[3] in ('S')
            tweenerstring = dict([item.split("=") for item in words[4].split(",")])

    def _send(self, args, trid=False, newline=True):
        "Send a protocol line to the remote server."
        if trid:
            self.__trid += 1
            raw_line = '%s %d %s' % (args[0], self.__trid, ' '.join(args[1:]))
        else:
            raw_line = ' '.join(args[1:])
        if newline:
            raw_line = raw_line + '\r\n'
        print 'MSN Send', raw_line
        self.__send.queue.append(raw_line)
        return
    
    def _recv(self):
        "Receive a protocol line from the remote server."
        return self.__recv.queue.popleft()
    
    def disconnect(self):
        "Disconects you from the remote server."
        return
