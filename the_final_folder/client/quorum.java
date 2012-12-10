/* Quorum protocol classes and Quorum Servers classes
 * Vinay Bharadwaj (vind.1989@gatech.edu)
*/

import java.io.*;
import java.lang.*;
import java.math.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;


class tStamp{
	private	long timestamp;

	public tStamp(long tstamp){this.timestamp = tstamp;}
	public long getTimestamp(){return this.timestamp;}
	
	public static long []greatest_timeStamp(long []tstamps, int server_count){
		   long grtstamp[] = new long[server_count];
		   long maxtStamp = 0;
		   int index;
		
		   for(index=0; index<server_count; index++)
			if(tstamps[index] > maxtStamp) maxtStamp = tstamps[index]; //First find the max time stamp.

		for(index=0; index<server_count; index++)
			if(tstamps[index] == maxtStamp) grtstamp[index] = 1; //Next check if multiple files have SAME time stamp.

		return grtstamp;
		
	}
}

class QFILE{
	private File fileptr;
   	private int serverID;
    private tStamp fileTStamp;
    public int similarityIndex;

    public int getServerID(){
    	return this.serverID;
    }
    
    public String toString(){
    	FileInputStream stream = null;
    	MappedByteBuffer bb = null;
    try{
    	try {
    		stream = new FileInputStream(new File(fileptr.getPath()));
    	    FileChannel fc = stream.getChannel();
    	    bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
    	    /* Instead of using default, pass in a decoder. */
    	    
    	  }
    	  
    	  finally {
    	    stream.close();
    	  }
    }
    catch (Exception e){}
    return Charset.defaultCharset().decode(bb).toString();	  
    }
    
    public QFILE(File fileptr, long tstamp, int sID){
    	this.fileptr = fileptr;
    	fileTStamp = new tStamp(tstamp);
    	serverID = sID;
    	similarityIndex = 0;
    }
    
    public File getfile(){
    	return fileptr;
    }
    
    public tStamp gettStamp(){
    	return fileTStamp;
    }
    
}


public class quorum{
	
	private long []grTimestamps;
	private long []timeStamps;
	private boolean qflag = false;
		 
	//Use this static methods to directly call quorum::quorum_files_static() to quorum files.
	public static int quorum_files_static(QFILE [] qf, int num_servers, String dest_path){
		quorum q1 = new quorum();
		return q1.quorum_files(qf, num_servers, dest_path);
	}
	
	//Use this static methods to directly call quorum::quorum_opcodes_static() to quorum opcodes.
	public static long quorum_opcodes_static(long [] opcodes, int num_opcodes){
		int opcode_similarity [] = new int[num_opcodes];
		int i, j, max_sim_opcodes=0;
		for(i=0; i<num_opcodes; i++) opcode_similarity[i] = 0;
		for(i=0; i<num_opcodes; i++)
			for(j=0; j<num_opcodes; j++)
			{
				if( i!=j && opcodes[i] == opcodes[j]) opcode_similarity[i]++;
			}
		
		for(i=0; i<num_opcodes; i++){
			if(opcode_similarity[i] > max_sim_opcodes) max_sim_opcodes = opcode_similarity[i];
		}
		
		if(max_sim_opcodes +1 < (int)Math.floor(num_opcodes/2) + 1){System.out.println(max_sim_opcodes); return -1;}
		for(i=0; i<num_opcodes; i++){
			if(opcode_similarity[i] == max_sim_opcodes) return opcodes[i];
		}
			
		return -1;
	}
	
    public quorum(){}
    
	public quorum(QFILE [] qf, int num_servers, String dest_path) {
    	quorum_files(qf, num_servers, dest_path);
	}
	
    public int quorum_files(QFILE [] files, int server_count, String dest_path){
    	if(!(server_count > 0)) return -1;
    	System.out.println("Attempting to quorum and output to : " + dest_path);
    	String [] file_data = new String[server_count];
    	int selected_file;
    	timeStamps = new long[server_count];
    	try{
    		//files[i].toString();
			for(int i=0; i<server_count; i++){ 
				file_data[i] =  filehash.computeHash(files[i].getfile());
				timeStamps[i] = files[i].gettStamp().getTimestamp();
			}
    		//else grTimestampsarry[i] = "-1";
    	}
    	catch(Exception e){System.err.println(e.toString()); return -1;}
    	Hashtable<String,Integer> entries = new Hashtable<String,Integer>();
    	for(int i = 0; i < server_count ;i++){
    		String ele = file_data[i];
  			if(entries.containsKey(ele)) entries.put(ele, (Integer) entries.get(ele) + 1);
			else 						 entries.put(ele,1);	
    		
    	}
    	selected_file = -1;
    	for(int i = 0; i < server_count ;i++){
    		String ele = file_data[i];
    		int occurances = entries.get(ele);
    		System.out.println("File "+ i + " generated hash " + file_data[i] +": Has [" + occurances +"] copies");
    		System.out.println("\tTIMESTAMP: " + timeStamps[i]);
    		if(occurances > 1)
    			if(selected_file < 0 || timeStamps[selected_file] < timeStamps[i])
    				selected_file = i;
    	}
    	if(selected_file < 0) {System.err.println("No consensus! Not writing output file."); return -1;}
    	else System.out.println("File#" +selected_file + " MAX SIMILARITY = " + entries.get(file_data[selected_file]));
    	
    	if(copy_to_output(files[selected_file],dest_path) < 0){ 
    		System.err.println("Unable to print selected_file to " + dest_path);
    		return -1;
    	}
		return 1;
	}
    public static int copy_to_output(QFILE file,String dest_path){
    	int num_bytes=0;
		File ftemp;
		FileOutputStream ostream = null; 
		File infile = file.getfile();
		FileInputStream instream = null;
		try{
			instream = new FileInputStream(infile);	
			ftemp = new File(dest_path); 
			ostream = new FileOutputStream(ftemp);
		}
		catch(Exception e){System.err.println(e.toString()); return -1;}
			long size = infile.length();
			byte [] arr = new byte[(int)size];
			try{
			instream.read(arr);
			ostream.write(arr);
			instream.close();
			ostream.close();
		}
		catch(Exception e){System.err.println(e.toString()); return -1;}
		return 0;
    	
    }
    public static String quorum_ls_files_static(QFILE [] files, int server_count, String dest_path,int qur_size){
    	int exists = 0;
    	String output = "";
    	for(int i = 0; i < qur_size;i++) if(files[i].getfile().exists()) exists++;
    	if(exists < qur_size - 1) return null;
    	Hashtable<String,Integer> entries = new Hashtable<String,Integer>();
    	for(int i = 0; i < qur_size; i++){
    		if(!files[i].getfile().exists()) continue;
    		String cls = files[i].toString();
    		StringTokenizer tok = new StringTokenizer(cls);
    		while(tok.hasMoreElements()){
    			String ele = (String) tok.nextElement();
    			System.out.println("Encountered an instance of file: " + ele);
    			if(entries.containsKey(ele)) entries.put(ele, (Integer) entries.get(ele) + 1);
    			else 						 entries.put(ele,1);
    		}
		}
		Enumeration<String> enumKey = entries.keys();
		while(enumKey.hasMoreElements()) {
		    String key = enumKey.nextElement();
		    Integer val = entries.get(key);
		    if(val >= (qur_size >>1)){
		    	output += "\n" + key;
		    	System.out.println("File named " + key + " probably exists.");
		    }
		    else System.out.println("File named " + key + " probably  doesn't exists.");
	    }
    	return output;
    }
    public static void main(String [] args){
    	File f1 = new File("C:/Users/Vinay Bharadwaj/workspace/quorum/src/1.txt.txt");
    	File f2 = new File("C:/Users/Vinay Bharadwaj/workspace/quorum/src/2.txt.txt");
    	File f3 = new File("C:/Users/Vinay Bharadwaj/workspace/quorum/src/3.txt.txt");
    	File f4 = new File("C:/Users/Vinay Bharadwaj/workspace/quorum/src/4.txt.txt");
    	File f5 = new File("C:/Users/Vinay Bharadwaj/workspace/quorum/src/5.txt.txt");
    	QFILE [] files = new QFILE[4];
    	files[0]= new QFILE(f1, 1 ,1);
    	files[1]= new QFILE(f2, 1, 2);
    	files[2]= new QFILE(f3, 1, 3);
    	files[3] = new QFILE(f4, 1, 4);
    	//files[4] = new QFILE(f5, 1, 5);
    	
    	//Example quoruming files using non-static function.
    	//quorum q1 = new quorum(files, 5, "C:/Users/Vinay Bharadwaj/workspace/quorum/src/output.txt");
    	
    	//Example quoruming files using static function.
    	quorum.quorum_files_static(files,4, "C:/Users/Vinay Bharadwaj/workspace/quorum/src/output.txt");
    	
    	//Eaxmple quoruming opcodes
    	long [] opcodes = {4,3,4,5,4,4};
    	long result = quorum.quorum_opcodes_static(opcodes, 6);
    	System.out.println("Opcode quorum result is:"+ result);
    	
    }
}

class quorum_server{
	private int num_servers;
	private int quorum_server_start(){return 1;}
    private	int quorum_server_stop(){return 1;}

    public quorum_server(int num_servers){ 
    	this.num_servers = num_servers;
    	quorum_server_start();
    }
    public void quorum_server_s(){quorum_server_stop();}
}