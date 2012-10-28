/*
 * quorum.h
 *
 *  Created on: Oct 22, 2012
 *      Author: Vinay Bharadwaj
 */
#ifndef _QUORUM_H
#define _QUORUM_H
#include <iostream>
#include <stdlib.h>
#include <stdio.h>
using namespace std;

//#define timestamp unsigned long;
//#define quorumserver int;
#define greatest_tStamp 1;

typedef class tStamp
{
	private:
			unsigned long int timestamp;

	public:
			tStamp(unsigned long int tstamp);
			unsigned long getTimestamp();
			static unsigned long *greatest_timeStamp(unsigned long *tstamps, unsigned int server_count);
}tStamp;

typedef class QFILE
{
	private: FILE *fileptr;
		    int serverID;


	public:
			 int getServerID();
			 QFILE(FILE *, unsigned long int, int);
			~QFILE();
			 FILE *getfile();
			 tStamp *fileTStamp;
			 int similarityIndex;


}QFILE;


typedef class quorum
{
	private: int num_servers;
			 unsigned long *grTimestamps;
			 unsigned long *timeStamps;
			 int quorum_start();
			 int quorum_stop();

	public:
			quorum(int num_servers);
			~quorum();
			int quorum_files(QFILE **,unsigned int, const char*);


}quorum;



#endif
