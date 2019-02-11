SVAROG DEVELOPERS' GUIDE
========================

This guide is targeted for users of Ubuntu Linux 16.04 and it is meant
to facilitate installation of developers' environment for Svarog,
allowing for preparation and testing of application's JAR files.


I. Preparation
----------------

1. Download Svarog's repository from:
  git@gitlab.com:braintech/svarog2.git

2. Run (as root) script “install-dev-env.sh” from scripts catalogue
(you should at least look at it before you run it). The script will
install NetBeans IDE, as well as all dependencies required to develop
Svarog, including Java JDK (e.g. OpenJDK 8 and OpenJFX).


II. Generating JAR files
--------------------------

ATTENTION: during the first compilation, maven will download a large
number of Svarog's dependencies (libraries). Depending of your network
connection, this process may take some time.

In project's main directory, run

  mvn clean package

whereas in case of minor changes "clean" may be omitted.

The command will generate Svarog's runnable JAR file into directory
svarog-standalone/target, as well as plugins' JAR files in separate
directories PLUGIN-NAME/target. To run generated JAR, execute

  java -jar svarog-standalone/target/svarog-standalone-*.jar

ATTENTION: This method runs Svarog without any plugins.
To run Svarog with all available plugins, you should

a) create symbolic links to JAR and XML files of all plugins you want
to run, linking from PLUGIN-NAME/target/PLUGIN-NAME.(xml|jar)
to directory $HOME/.svarog/plugins 

  OR

b) create a separate directory according to ZIP creation guide (below)
and run Svarog from that directory.


III. IDE configuration
----------------------

1. Build Svarog from command line according to the instructions in
section II. If you omit this point, NetBeans IDE will not be able to
find all automatically required files and will display an error.

2. Run already installed application NetBeans, got into Tools → Plugins
menu, open tab Settings and mark as checked all three available plugin
sources. Go to Available Plugins, and after having refreshed the plugin
list (Check for Newest) choose Maven from list and install it.
NetBeans will ask to be restarted after this step.

3. You can now choose File → Open Projects… and navigate to the path
of Svarog repository and/or subdirectory "svarog" inside it.
Svarog consists of several connected Maven packages:

* Svarog-top, which is the top-most project, which, when built, builds
the entire Svarog with all dependencies
* Svarog, which is the main part of Svarog
* other separate projects, one for each plugin

Most of the source code of Svarog's main part is in directory

  svarog/src/main/java

which is included in the project's CLASSPATH. It means that the hierarchy
of subdirectories inside this directory directly corresponds to
the structure of Java packages. Each source file contains directly
one class, with the same name as the file.

For example, svarog/src/main/java/org/signalml/app/SvarogApplication.java
file contains the SvarogApplication class. The first line of this file
defines the package, to which this class belongs:

  package org.signalml.app;

Analogically, every Svarog plugins has its own package hierarchy,
starting in:

  plugins/PLUGIN-NAME/src/main/java


IV. Working with translations
-----------------------------

The first step is to generate *.pot files from Svarog's code, as well as from
the source code of all the plugins. It is done by executing

  mvn gettext:gettext

in the "svarog" directory as well as in the directory of each plugin.

Next, the resulting *.pot files have to be merged into one *.pot file.
Utility "msgcat" can be used to accomplish this, and it is preferable
to call it with "-s" ("--sort-output") flag.

The resulting *.pot file can be used to initialize a new translation
by the use of "msginit" command. This will create an empty *.po
(translation) file suited to the given language. These files should be
edited either by hand or with the help of some GUI. Finished translation
files (e.g. "pl.po") should be added to the directory

  svarog/src/main/resources/org/signalml/app/resource/i18n/

and will be automatically included in the JAR file. Also, every available
language should be added to list LANGUAGES in file SvarogI18n.java.

Translating help files, however, is mostly manual. Help files are stored
in a versioned directory

  svarog/src/main/resources/org/signalml/help/

where the subdirectories (e.g. "en") mark the different language versions.
To create the help files for another language, simply create a directory for
a given language, and put the translations with the same names as original
versions.


V. Modifying Svarog version
---------------------------

Svarog's version number is automatically created by Maven based on
the most recent tags in git repository, as returned by

  git describe --tags

with

  mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$(git describe --tags)

command (Which CI runs by itself).

therefore, to create JAR with a new version number, it is necessary to:


1. Commit the changes and create a git tag with the new version number:

  git tag VERSION

2. You can now build the JAR file; it will have the new version number.


VI. Creating ZIP file for github Releases
-----------------------------------------

ZIP package consists of:
a) Svarog's JAR file
b) plugins' JAR and XML files
c) executable script run_svarog.sh
d) an example signal wakeEEG.bin with corresponding XML file
e) binary executables of MP5 and empi

The most recent ZIP file can be used to provide all the files except
a) and b). Afterwards, it is necessary to:

* replace Svarog's JAR file and plugins' files, if they have changed
* replace version number in run_svarog.sh
* make a ZIP file with the new name (check if the name of the package
  corresponds to the package's main directory)


VII. Known problems
-------------------

It is known that sometimes, especially when working in virtual machine
environment, any change of the display resolution or energy-saving
transition (e.g. computer sleep/wakeup) triggers the following error:

  message: sun.awt.image.BufImgSurfaceData cannot be cast to sun.java2d.xr.XRSurfaceData
  class java.lang.ClassCastException

This is a known bug in the current version of Java, and can be avoided
by starting Svarog from JAR file with the command-line option

  -Dsun.java2d.xrender=false
