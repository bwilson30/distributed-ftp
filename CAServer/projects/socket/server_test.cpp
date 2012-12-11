/* 
   Takes the C++ server on a little test spin 

   Keith Vertanen 4/99, updated 10/09 
*/

#include "Server.h"

#define SIZE 10		  /* how many items per packet */
#define NUM_PACKS 3   /* number of times we'll do it */

int main(int argc, char *argv[])
{
	double C[SIZE];
	char D[SIZE];
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

	/* put some dummy data in our arrays */

	for (int i = 0; i < SIZE; i++)
	{
		C[i] = (double) i*i+0.5;
		D[i] = i;
	}
	printf("Server, waiting for connection...\n");
	fflush(NULL);
	mylink.Connect();

	printf("Server, got a connection...\n");
	fflush(NULL);

	for (int i = 0; i < NUM_PACKS; i++)
	{
		printf("Server, sending bytes, iteration %d...\n", i);
	   	fflush(NULL);
		mylink.SendBytes(D, SIZE);

		printf("Server, receiving doubles, iteration %d...\n", i);
		fflush(NULL);
		mylink.RecvDoubles(C, SIZE);
	}

	printf("Server, closing connection...\n");
	fflush(NULL);
	mylink.Close();

	printf("Server, done...\n");
	fflush(NULL);
	return 0;
}

