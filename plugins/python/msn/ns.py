#from auth import tweener
from common import baseConnection

class notificationServer (baseConnection):
    def __init__(self, msnconnection, addr, user):
        baseConnection.__init__(self, msnconnection, addr, self)
        return
    
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
