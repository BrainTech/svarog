import sys
import math
import os
template='''                <eegElectrode>
                    <label>{}</label>
                    <polarPosition>
                        <theta>{}</theta>
                        <phi>{}</phi>
                        <radius>{}</radius>
                    </polarPosition>
                </eegElectrode>
'''
labels_template = '''<string>{}</string>\n'''

PREFIXES = ["", "EEG "]

def cartesian_to_polar(x, y, z):
    r = (x*x + y*y + z*z)**0.5
    theta = math.degrees(math.atan2(y, x)) - 90
    phi = math.degrees(math.atan2(z, (x*x + y*y)**0.5))
    return r, phi, theta
                
def main(filename):
    output = open(os.path.join(os.path.dirname(os.path.abspath(filename)), 'output.txt'), 'w')
    output_labels = open(os.path.join(os.path.dirname(os.path.abspath(filename)), 'output_labels.txt'), 'w')
    with open(filename) as f:
        for line in f:
            label, x, y, z = line.split()
            for prefix in PREFIXES:
                try:
                    label = prefix + str(label)
                    x = float(x)
                    y = float(y)
                    z = float(z)
                except ValueError:
                    continue
                r, phi, theta = cartesian_to_polar(x, y, z)
                print(label, r, phi, theta)
                output.write(template.format(label, theta, phi, r))
                output_labels.write(labels_template.format(label))
    output.close()
    output_labels.close()
    
if __name__ == '__main__':
    filename = sys.argv[-1]
    main(filename)
        
