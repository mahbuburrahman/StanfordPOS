StanfordPOS
===========

The StanfordPOS project is used for preprocessing and pos-tagging your corpus. Necessary setting is given below. 

Setting Classpath:
	Location of the source code in your computer

	Example: 
		export CLASSPATH=$CLASSPATH:/Users/Mahbub/Desktop/GitHub/UMBC_STS/src/


Configuring the paths of model and corpus directory: 
	Edit the config.txt file in the main directory. 
	First line is for the location of model directory
	Second line is for the location of corpus directory

Command to compile:
	javac -O -encoding UTF8 -d .  *.java


Command to run the program:
	Without JVM size flag
		java -cp . edu.umbc.postagger.SfdTagger “path to input corpus”


	With JVM size flag 
		java -Xmx8G -cp . edu.umbc.postagger.SfdTagger “path to input corpus”


