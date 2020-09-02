from subprocess import check_output
output = check_output(['git', 'describe', '--tags']).decode()
try:
    version, distance = output.split('-')[0:2]
except ValueError:
    version = output.strip()
    distance = 0
nsis_ver = "{}.{}".format(version, distance)
print(nsis_ver)
 
