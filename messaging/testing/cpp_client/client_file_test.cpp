/*
	Takes the C++ client on a little test spin

   	Keith Vertanen 4/99, updated 10/09
*/

#include "Client.h"
#include <cstdlib>
#include <cstdio>
#include <iostream>

int main(int argc, char *argv[])
{
	/* if no command line arguments passed, we'll default to
		these two port number */

	int port = 5010;
	int dataport = -1;
	int rev = 1;
	bool bResult = false;

	if (argc < 2)
	{
		printf("Not enough command line arguments!\n\n");
		printf("    client_test <system-name> [port] [dataport] [reverse byte order]\n\n");
		return 0;
	}

	if (argc > 3)
	{
		port     = atoi(argv[2]);
		dataport = atoi(argv[3]);

		if (argc > 4)
			rev = atoi(argv[4]);
	}

	printf("Client, system '%s', port %d, datagram port %d, reverse bytes %d\n", argv[1], port, dataport, rev);
	fflush(NULL);
  	Client mylink(port, dataport, argv[1], rev, &bResult);

	if (!bResult)
	{
		printf("Failed to create Client object!\n");
		return 0;
	}

	printf("Client, made connection...\n");
	fflush(NULL);

	char requestedFile[100] = "/home/bwilson30/Documents/6102/Project/Socket/java_server/paxos-simple.pdf^";
	mylink.SendString(requestedFile);

	int fileSize;
	mylink.RecvInts(&fileSize, 1);
	printf("fileSize is: %d\n", fileSize);
	mylink.RecvFile("/home/bwilson30/Documents/6102/Project/Socket/cpp_client/paxos-simple.pdf", fileSize);

	printf("Client, closing connection...\n");
	fflush(NULL);
	mylink.Close();

	printf("Client, done...\n");
	fflush(NULL);
	return 0;
}
