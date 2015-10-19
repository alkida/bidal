# bidal
BiDAl big data analyzer


How to compile:
mvn compile

How to create JAR:
mvn package

How to run:
mvn exec:java -Dexec.mainClass="analyzer.Controller"

It has been tested on Linux and MacOS.
It is necessary to have R, RSQLite package, hadoop and an environment variable set as
export HADOOP_HOME=/path/to/hadoop/
