# BiDAl
<h5>Big Data Analyzer</h5>


How to compile:<br />
mvn compile

How to create JAR:<br />
mvn package

How to run:<br />
mvn exec:java -Dexec.mainClass="analyzer.Controller"

It has been tested on Linux and MacOS.<br />
It is necessary to have R, RSQLite package, hadoop and an environment variable set as<br />
export HADOOP_HOME=/path/to/hadoop/
