# bidal
BiDAl big data analyzer


How to compile:__
mvn compile

How to create JAR:__
mvn package

How to run:__
mvn exec:java -Dexec.mainClass="analyzer.Controller"

It has been tested on Linux and MacOS.__
It is necessary to have R, RSQLite package, hadoop and an environment variable set as__
export HADOOP_HOME=/path/to/hadoop/
