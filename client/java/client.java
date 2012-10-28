import  java.io.*;
import java.util.*;
public class client{
   public static void main (String[] args) {
      	while(true){
		//  prompt the user to enter their name
		System.out.print("quarac> ");
		//  open up standard input
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String ccmd = null;
		//  read the username from the command-line; need to use try/catch with the
		//  readLine() method
		try {
		 ccmd = br.readLine();
		} catch (IOException ioe) {
		 System.out.println("IO error trying to read your name!");
		 System.exit(1);
		}
		// System.out.println(ccmd);
		StringTokenizer Tok = new StringTokenizer(ccmd);
		if(!Tok.hasMoreElements()) continue;
		String cmdstr = (String)Tok.nextElement();
		int cmdargc =Tok.countTokens();
		String[] cmdargs = new String[cmdargc];
		for(int i = 0; i < cmdargc; i++) cmdargs[i] = (String)Tok.nextElement(); 
		// System.out.println(cmdstr);
		if(cmdstr.equals("quit") || cmdstr.equals("exit")) return;
		else if(cmdstr.equals("get"));
			
	}
   }
	
}
