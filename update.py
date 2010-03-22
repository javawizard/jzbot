import os

print "Updating SVN Code"
os.system("includes/jsvn update")

print "Building JZBot"
os.system("build")

print "Restarting..."
restartFile = open('storage/restart', 'w')
restartFile.write('Restart!')
restartFile.close()

print "Updates have completed successfully. If your bot"
print "is currently running, it will be restarted momentarily."
