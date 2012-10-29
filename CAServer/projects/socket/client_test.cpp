/* 
	Takes the C++ client on a little test spin 

   	Keith Vertanen 4/99, updated 10/09
*/

#include "Client.h"

#define SIZE 10		 	 /* how many items per packet */
#define NUM_PACKS 3	 /* number of times we'll do it */

int main(int argc, char *argv[])
{
	double C[SIZE];
	char D[SIZE];

	/* if no command line arguments passed, we'll default to 
		these two port number */

	int port = 5010;
	int dataport = -1;
	int rev = 0;
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

	/* put some dummy data in our arrays */

	for (int i = 0; i < SIZE; i++)
	{
		C[i] = (double) i * i + 0.5;
		D[i] = i;
	}

	for (int i = 0; i < NUM_PACKS; i++)
	{
		printf("Client, receiving bytes, iteration %d\n", i);
		fflush(NULL);
		mylink.RecvBytes(D, SIZE);

		printf("Client, sending doubles, iteration %d\n", i);
		fflush(NULL);
		mylink.SendDoubles(C, SIZE);
	}

	printf("Client, closing connection...\n");
	fflush(NULL);
	mylink.Close();

	printf("Client, done...\n");
	fflush(NULL);
	return 0;
}

