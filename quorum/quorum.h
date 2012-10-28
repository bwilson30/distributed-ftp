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
	private: 
			 unsigned long *grTimestamps;
			 unsigned long *timeStamps;
			 

	public:
			quorum(QFILE **,unsigned int, const char*);
			~quorum();
			int quorum_files(QFILE **,unsigned int, const char*);


}quorum;


typedef class quorum_server
{
	private: int num_servers;
			 int quorum_server_start();
             int quorum_server_stop();
			 
	public: quorum_server(int num_servers);
			~quorum_server();
}quorum_server;


#endif
