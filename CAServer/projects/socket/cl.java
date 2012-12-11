// test of the client class

// Keith Vertanen, 11/98

import java.io.*;
import java.net.*;
import Client;

public class cl {

   static int SIZE = 1024;   // how big the array of data is
   static int ITERS = 20;    // how many messages to bounce back and forth

   public static void main( String args[] ) throws IOException
   {
	// connect to the server, first number is the port, second
	// is the system name, third is whether you want reversed
	// bytes or not

	Client myclient = new Client(5007, "bee12.cs.orst.edu", 1);

	System.out.println(myclient.recv_string('\n'));

	myclient.send_string("We read you 5 by 5\n");

	double C[];

	C = new double[SIZE];

	for (int i=0; i<SIZE; i++)
		C[i] = 0.0;

	for (int i=0; i<ITERS; i++)
	{
		myclient.recv_doubles(C, SIZE);
		myclient.send_doubles(C, SIZE);

		System.out.print(i+" ");
	}

	myclient.closesocket();	
   }
}
