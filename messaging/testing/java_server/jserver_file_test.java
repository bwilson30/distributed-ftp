//Simple test of the Java server class

//Keith Vertanen 4/99, updated 10/09

import java.io.*;
import java.net.*;

public class jserver_file_test {

	public static void main(String args[]) throws IOException {
		int port = 5010;
		int dataport = -1;
		int rev = 1;

		if (args.length > 1) {
			port = (new Integer(args[0])).intValue();
			if (args.length > 2)
				dataport = (new Integer(args[1])).intValue();
		}

		System.out.println("Server, listening on port " + port
				+ ", datagram port " + dataport);
		Server mylink = new Server(port, dataport);

		System.out.println("Server, waiting for connection...");
		mylink.Connect();

		String filePath = mylink.RecvString('^');
				
		mylink.SendFile(filePath);

		System.out.println("Server, closing connection...");
		mylink.Close();

		System.out.println("Server, done...");

	}
}