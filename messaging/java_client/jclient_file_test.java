//Simple test of the Java client class for file transfer
//
//Keith Vertanen 4/99, updated 10/09
//Brett Wilson 9/12

import java.io.*;
import java.net.*;

public class jclient_file_test {

	public static void main(String args[]) throws IOException {
		int port = 5010;
		int dataport = -1;
		int rev = 1;

		if (args.length < 1) {
			System.out.println("Not enough command line arguments!");
			System.out
					.println("     java_client_test <system-name> [port] [dataport] [reversed bytes]");
			System.out.println("");
		} else {
			if (args.length > 1) {
				port = (new Integer(args[1])).intValue();
				if (args.length > 2) {
					dataport = (new Integer(args[2])).intValue();
					if (args.length > 3)
						rev = (new Integer(args[3])).intValue();
				}
			}

			System.out.println("Client, port " + port + ", datagram port "
					+ dataport + ", reverse bytes " + rev);

			Client myclient = new Client(port, dataport, args[0], rev);

			System.out.println("Client, made connection...");

			myclient.SendString("/home/bwilson30/Documents/6102/Project/Socket/cpp_server/paxos-simple.pdf^");
			
			int fileSize[] = new int[1];
			myclient.RecvInts(fileSize, 1);
			myclient.RecvFile("/home/bwilson30/Documents/6102/Project/Socket/java_client/paxos-simple.pdf", fileSize[0]);

			System.out.println("Client, closing connection...");
			myclient.Close();

			System.out.println("Client, done...");
		}
	}
}