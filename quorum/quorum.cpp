/*
 * quorum.c
 *
 *  Created on: Oct 22, 2012
 *      Author: Vinay Bharadwaj
 */
#include "quorum.h"
#include "filehash.h"
#include <malloc.h>
FILE *f1, *f2, *f3;

QFILE::QFILE(FILE *fptr, unsigned long int tstamp, int sID)
{
	fileptr = fptr;
	fileTStamp = new tStamp(tstamp);
	serverID = sID;
	similarityIndex = 0;
}

FILE * QFILE::getfile()
{
	return fileptr;
}

unsigned long tStamp::getTimestamp()
{
	return this->timestamp;
}

tStamp::tStamp(unsigned long int ts)
{
	timestamp=ts;
}


int QFILE::getServerID()
{
	return this->serverID;
}


quorum::quorum(int num_servers)
{
	this->num_servers = num_servers;
	quorum_start();
}

quorum::~quorum()
{
	quorum_stop();
}

int quorum::quorum_start()
{
	//Start the quorum of servers with the script
	return 1; //On success
}

int quorum::quorum_stop()
{
	//Stop the quorum of servers with the script
	return 1; // On success
}


unsigned long *tStamp::greatest_timeStamp(unsigned long int *tstamps, unsigned int server_count)
{
	static unsigned long *grtstamp = (unsigned long *)malloc(sizeof(unsigned long)*server_count);
	unsigned long maxtStamp = 0;
	int index;
	for(index=0; index<server_count; index++)
		if(tstamps[index] > maxtStamp) maxtStamp = tstamps[index]; //First find the max time stamp.

	for(index=0; index<server_count; index++)
		if(tstamps[index] == maxtStamp) grtstamp[index] = 1; //Next check if multiple files have SAME time stamp.

	return grtstamp;

}


int quorum::quorum_files(QFILE **files, unsigned int server_count, const char *destination_path)
{
	if(!(server_count > 0)) return -1;

	int maxSimilarity = 0;
	int i, count=0;
	uint64_t grTimestampsarry[server_count];
	this->timeStamps = (unsigned long *)malloc(sizeof(unsigned long)*server_count);

	for(i=0; i<server_count; i++){
		timeStamps[i] = files[i]->fileTStamp->getTimestamp();
		grTimestampsarry[i] = 0;
	}

	this->grTimestamps = tStamp::greatest_timeStamp(this->timeStamps, server_count);

	for(i=0; i<server_count; i++)
		if(grTimestamps[i] == 1) count++;

	if(count == 1){
		int num_bytes=0;
		for(i=0; i<server_count; i++){
			if(grTimestamps[i] == 1){/*Write the contents of file files[i]->fileptr into destination path*/
				
				FILE * ftemp;
				FILE * infile = files[i]->getfile();
				
				if((ftemp = fopen(destination_path, "w")) == NULL) return -1;
				fseek(infile, 1, SEEK_END);
				int size = ftell(infile);
				rewind(infile);
				char arr[size+1];
				fgets(arr, size+1, infile);
				num_bytes = fprintf(ftemp, "%s", arr);
				fclose(ftemp);
				break;
			}
		}
		if(num_bytes>0)
		return 1; //If writing into dest_path was successful.
	}

	//Else if there are multiple files with same time stamps, we need to compare the file hashes. If all the files hashes
	//are equal, then we just write a random file out of the lot into dest_path. Else, we need to have a MAJORITY of the
	//files whose hashes are equal. If we don't have a majority, we return an error code.
	for(i=0; i<server_count; i++)
		if(grTimestamps[i] == 1) grTimestampsarry[i] = compute_hash(files[i]->getfile());
		else grTimestampsarry[i] = -1;
	//Now we have an array that indicates which files have the largest (same) timestamps. All we need to do now is
	//to compare the files with the greatest timestamps and choose a random file out of the correct ones.

	//Compare hashes and update similarity index for each file.
	for(i=0; i<server_count; i++){
		if(grTimestamps[i] == 1)
		for(int j=0; j<server_count; j++)
		{
			if(grTimestamps[j] == 1 && i != j && grTimestampsarry[i] == grTimestampsarry[j]) files[i]->similarityIndex++;
		}
	}

	for(i=0; i<server_count; i++)
		if(grTimestamps[i] == 1 && files[i]->similarityIndex > maxSimilarity) maxSimilarity = files[i]->similarityIndex;

	int num_bytes=0;
	for(i=0; i<server_count; i++){
		if(files[i]->similarityIndex == maxSimilarity)/*Write files[i]->fileptr to dest path. */
		{
			FILE *ftemp, *infile;
				if((ftemp = fopen(destination_path, "w")) == NULL) return -1;
				printf("i %d", i);
				infile = files[i]->getfile();
				fseek(infile, 0, SEEK_END);
				int size = ftell(infile);
				rewind(infile);
				printf("SIZE %d", size);
				char arr[size+1];
				fgets(arr, size+1, infile);
				printf("ARRAY contents %s", arr);
				//num_bytes = fwrite(arr, sizeof(char), sizeof(arr), ftemp);
				num_bytes = fprintf(ftemp, "%s", arr);
				fclose(ftemp);
		}
	}
if(num_bytes>0)
return 1;
}

int main()
{	quorum *q1 = new quorum(3); //Actually doesn't do anything as of now. For later..
	f1 = fopen("1.txt.txt", "rw");
	f2 = fopen("2.txt.txt", "rw");
	f3 = fopen("3.txt.txt", "rw");
	QFILE *files[3];
	files[0] = new QFILE(f1, 1, 1);
	files[1] = new QFILE(f2, 1, 2);
	files[2] = new QFILE(f3, 1, 3);
	q1->quorum_files(files, 3, "output.txt");
			
return 0;
}
