import os

try:
    import pysvn
except:
    print "PySVN Not Found!"
    print "Get it at http://pysvn.tigris.org/"
    print "Exiting..."
    exit(1)

def ssl_server_trust_prompt(trust_dict):
    return True, 100, True

client = pysvn.Client()
client.callback_ssl_server_trust_prompt = ssl_server_trust_prompt

print "Updating to the latest version of JZBot..."
client.update(".")

print "Building JZBot"
os.system("build")

restartFile = open('./storage/restart', 'w')
restartFile.write('Restart!')
restartFile.close()

print "Updates have completed successfully. If your bot"
print "is currently running, it will be restarted momentarily."
