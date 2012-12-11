/*
 * server_file_test.cpp
 *
 *  Created on: Sep 27, 2012
 *      Author: bwilson30
 */

#include "Server.h"

int main(int argc, char *argv[])
{
	bool bResult = false;

	/* if no command line arguments passed, we'll default to
		these two port number */

	int port = 5010;
	int dataport = -1;

	if (argc > 1)
	{
		port = atoi(argv[1]);

		if (argc > 2)
			dataport = atoi(argv[2]);
	}

	printf("Server, listening on port %d, datagram port %d\n", port, dataport);
	fflush(NULL);

	Server mylink(port, dataport, &bResult);
	if (!bResult)
	{
		printf("Failed to create Server object!\n");
		return 0;
	}

	printf("Server, waiting for connection...\n");
	fflush(NULL);
	mylink.Connect();

	printf("Server, got a connection...\n");
	fflush(NULL);

	char requestedFile[100];
	mylink.RecvString(requestedFile, 100, '^');

	mylink.SendFile((const char*) requestedFile);

	printf("Server, closing connection...\n");
	fflush(NULL);
	mylink.Close();

	printf("Server, done...\n");
	fflush(NULL);
	return 0;
}




