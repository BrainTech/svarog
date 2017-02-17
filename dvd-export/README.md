1. Create directory dvd/apps and download the VLC installer from
     http://www.videolan.org/vlc/download-windows.html
   into that directory.

2. Create directory dvd/data and copy all data to be distributed
   into that directory.

3. Compile Svarog as usual (mvn package) and copy
   the generated svarog-standalone JAR file into directory dvd.

4. Run script "generate.sh" — this will generate ISO image file "dvd.iso".
   Now you can burn the DVD.
