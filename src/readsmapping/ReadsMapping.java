//package readsmapping;

import java.io.*;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;


import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ReadsMapping{

	public static class Map 
		extends Mapper<LongWritable, Text, Text, Text>{
			String filename;

			/*protected void setup(Context context) throws IOException, InterruptedException {
				FileSplit fsFileSplit = (FileSplit) context.getInputSplit();
				//filename = context.getConfiguration().get(fsFileSplit.getPath().getParent().getName());
				filename = fsFileSplit.getPath().getParent().getName();
				System.out.println("Name: " + filename);
			}*/

			private Text FILENAME = new Text();   // type of output key
			private Text ID = new Text();

			public void map(LongWritable key, Text value, Context context
					) throws IOException, InterruptedException {

				String QUERY = context.getConfiguration().get("QUERY", "");
				StringTokenizer itr = new StringTokenizer(value.toString());
				filename = itr.nextToken();
				String genome = itr.nextToken();

				int idx = genome.indexOf(QUERY);
				if (-1 != idx)
				{
					FILENAME.set(filename);//should get the input file name
					ID.set(String.valueOf(idx));
					context.write(FILENAME, ID);
				}
			}
		}

	public static class Reduce
	extends Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterable<Text> values, 
				Context context
				) throws IOException, InterruptedException {
			context.write(key, values.iterator().next()); // create a pair <keyword, number of occurences>
		}
	}

	// Driver program
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration(); 
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs(); // get all args
		if (otherArgs.length != 3) {
			System.err.println("Usage: ReadsMapping <hdfs input dir> <local query file> <hdfs output dir>");
			System.exit(2);
		}

		System.out.println("hdfs input dir: " + otherArgs[0]);
		System.out.println("local query file: " + otherArgs[1]);
		System.out.println("hdfs output dir: " + otherArgs[2]);

		try
		{
			FileReader fileReader = new FileReader(new File(otherArgs[1]));
			BufferedReader br = new BufferedReader(fileReader);
				
			String query = br.readLine();
			System.out.println("QUERY: " + query);
			conf.set("QUERY", query);
			br.close();

		}catch(IOException e){
			System.out.println(e);
			System.exit(-1);
		}

		// create a job with name "readsmapping"
		Job job = new Job(conf, "readsmapping");
		job.setJarByClass(ReadsMapping.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		// Add a combiner here, not required to successfully run the wordcount program  

		// set output key type   
		job.setOutputKeyClass(Text.class);
		// set output value type
		job.setOutputValueClass(Text.class);
		//set the HDFS path of the input data
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		// set the HDFS path for the output
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));

		int numReduceTasks = 4, numMapTasks = 4;
		//job.setNumMapTasks(numMapTasks);
		//job.setNumReduceTasks(numReduceTasks);

		//Wait till job completion
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
