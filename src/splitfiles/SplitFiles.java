//package readsmapping;

import java.io.*;
import java.util.*;

public class SplitFiles
{	
	public static void main(String[] args)
	{
		System.out.println("***************************");
		System.out.println("**The part 1 of PPM proj***");
		System.out.println("**Split fasta into subs****");
		System.out.println("***************************");

		if (args.length != 1)
		{
			String error_report = "\nUsage: "
				+ "java SplitFiles "
				+ "[fasta file name] \n"
				+ "e.g.: "
				+ "java SplitFiles 500SeqDB_1.fa";
			System.out.println(error_report);
			System.exit(-1);
		}

		String dataPath = args[0];
		System.out.println(dataPath);

		try
		{
			FileReader fileReader = new FileReader(new File(dataPath));
			BufferedReader br = new BufferedReader(fileReader);
			
			String genomeID = null, genomeSeq = null;

			//read each 2 lines of the file into a new file
			while ((genomeID = br.readLine()) != null)
			{
				if ((genomeSeq = br.readLine()) == null)
				{
					System.out.println("missing genomeSeq for genomeID: " + genomeID);
					System.exit(-1);
				}
				try
				{
					String fileName = genomeID.substring(1) + ".fa";
					FileWriter fw = new FileWriter(new File("files", fileName));
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(fileName + " "+ genomeSeq);
					bw.close();
				}catch(Exception e){
					System.out.println(e);
				}
			}
			br.close();
		}catch(Exception e){
			System.out.println(e);
		}

		System.out.println("SplitFiles are done");
	}//main
}
