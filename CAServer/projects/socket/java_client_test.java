// Simple test of the Java client class
//
// Keith Vertanen 4/99, updated 10/09

import java.io.*;
import java.net.*;

public class java_client_test 
{

   static int SIZE = 10;		// how many items in each packet
   static int NUM_PACKS = 3;		// how many repetitions to do

   public static void main( String args[] ) throws IOException
   {
	int port = 5010;
	int dataport = -1;
	int rev = 0;

	if (args.length < 1)
	{
		System.out.println("Not enough command line arguments!");
		System.out.println("     java_client_test <system-name> [port] [dataport] [reversed bytes]");
		System.out.println("");
	}
	else
	{
		if (args.length > 1)
		{
			port = (new Integer(args[1])).intValue();
			if (args.length > 2)
			{
				dataport = (new Integer(args[2])).intValue();
				if (args.length > 3)
					rev = (new Integer(args[3])).intValue();
			}
		}

		System.out.println("Client, port " + port + ", datagram port " + dataport + ", reverse bytes " + rev);

		Client myclient = new Client(port, dataport, args[0], rev);

		System.out.println("Client, made connection...");
	
		double C[];
		byte D[];

		C = new double[SIZE];
		D = new byte[SIZE];

		for (int i = 0; i < SIZE; i++)
		{
			C[i] = i*i + 0.5;
			D[i] = (byte) i;
		}


		for (int i = 0; i < NUM_PACKS; i++)
		{
			System.out.println("Client, receiving bytes, iteration " + i);
			myclient.RecvBytes(D, SIZE);

			System.out.println("Client, sending doubles, iteration " + i);
			myclient.SendDoubles(C, SIZE);
		}

		System.out.println("Client, closing connection...");
		myclient.Close();		

		System.out.println("Client, done...");
	}
   }
}
