StanfordPOS
===========

Classpath:
Location of the source code in your computer
 
export CLASSPATH=$CLASSPATH:/Users/Mahbub/Desktop/GitHub/UMBC_STS/src/


Command to compile:
	javac -O -encoding UTF8 -d .  *.java


Command to run the program:
	java -cp . edu.umbc.postagger.SfdTagger “path to input articles”

With JVM size flag 
	java -Xmx8G -cp . edu.umbc.postagger.SfdTagger “path to input articles”