echo "################################################"
echo "#### Make sure you finish splitfiles/ first ####"
echo "####        Else Please Ctrl + C            ####"
echo "################################################"
sleep 5


echo
echo "################################################"
echo "####    Compling files under readsmapping/  ####"
echo "################################################"

# Compile 
if [ ! -d classes ]; then
        mkdir classes;
fi

# Compile ReadsMapping
export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar
#javac -classpath $HADOOP_HOME/hadoop-core-1.1.2.jar:$HADOOP_HOME/lib/commons-cli-1.2.jar -d ./classes ReadsMapping.java
$HADOOP_HOME/bin/hadoop com.sun.tools.javac.Main -d ./classes ReadsMapping.java

# Create the Jar
jar -cvf readsmapping.jar -C ./classes/ .
 
# Copy the jar file to the Hadoop distributions
cp readsmapping.jar $HADOOP_HOME/bin/ 
cp query.fa $HADOOP_HOME/bin/ 
cp -r ../data/files $HADOOP_HOME/bin


#  Ready to execute the hadoop
echo
echo "########################################"
echo "###### Stopping Hadoop #################"
echo "########################################"

cd $HADOOP_HOME/bin/
pwd

#./hadoop namenode -format
./stop-all.sh

# Delete the data directory
./hadoop namenode -format
rm -rf /tmp/hadoop-ubuntu/dfs/data/

echo
echo "########################################"
echo "###### Starting Hadoop #################"
echo "########################################"
./start-all.sh

#./hadoop dfs -mkdir input
echo Sleep 5 secs for setting up
./hdfs dfs -ls /

./hdfs dfs -mkdir /files
./hdfs dfs -ls /

jps

sleep 5

echo
echo "########################################"
echo "###### Uploading files to HDFS #########"
echo "########################################"

#./hadoop dfs -put files/ input
./hdfs dfs -put files/ /files

#echo Sleep 20 secs for uploading, or it will race condition
#sleep 20

echo
echo "########################################"
echo "###### Reads Mapping Starting  #########"
echo "######   Up to several mins    #########"
echo "########################################"
./hadoop jar readsmapping.jar ReadsMapping /files/files query.fa output

rm -rf output
./hdfs dfs -get /user/ubuntu/output .

rm -rf $PPM/output
mv output/ $PPM 

echo
echo "########################################"
echo "###### Reads Mapping Finished  #########"
echo "###### Results in $PPM/output/ #########"
echo "########################################"

