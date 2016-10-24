#!/bin/bash

if [ ! -d classes ]; then
        mkdir classes;
fi

if [ -d files ]; then
	rm -rf files
fi
mkdir files

# Compile WordCount
javac -d ./classes SplitFiles.java

# Create the Jar
jar -cvf splitfiles.jar -C ./classes/ .
 
# Execute the program and copy generated split files to ../data/files
java -cp splitfiles.jar SplitFiles ../data/500SeqDB_1.fa

if [ -f splitfiles.jar ]; then
	echo Source code compiled!
else
	echo There maybe errors in your source codes, please check it.
	exit 255
fi

if [ -d ../data/files ]; then
	rm -rf ../data/files
fi
mv files ../data/files
echo mv files/  to ../data/files
