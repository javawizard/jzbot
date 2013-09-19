#from auth import tweener
from common import baseConnection

class notificationServer (baseConnection):
    def __init__(self, msnconnection, addr, config):
        baseConnection.__init__(self, msnconnection, addr, config)
        return
    
    def recv_callback(self):
        line = self.recv()
        if not line:
            return
        words = line.split(' ')
        print 'MSN Recv', `line`
        
        if words[0] == 'VER':
            versions = words[2:]
            assert '0' not in versions
            assert 'MSNP8' in versions
            self.send(['CVR', '0x0409', 'python', 'jzbot', 'java',
                'JZBOT', '42', 'MSMSGS', self.config['user']], True)
            return
        if words[0] == 'CVR':
            self.send(['USR', 'TWN', 'I', self.config['user']], True)
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
