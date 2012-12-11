/*
 * Messaging.h
 *
 *  Created on: Oct 16, 2012
 *      Author: bwilson30
 */

#ifndef MESSAGING_H_
#define MESSAGING_H_

#define TERM_CHAR TERMINATING_CHARACTER 
#include "Client.h"

class Messaging {
public:
	~Messaging(){};
	Messaging(unsigned int pn){port = pn;}; //
	void setPort(unsigned int p);
	void setEncrypter();		// TODO: NEED to define
/////////////////// Communication commands ////////////////////////////
	// The get() command takes in a local_path which will be the
	// name of the temporary_file that it will write remote_path
	// to. It will return the file size if sucessful and a negative
	// number that represents the error code if false 
	int get(const char* local_path,  const char* remote_path);
		// Errors for get():
		// File not Found
		// Timeout
		// Non-authenticated user
	int ls( const char* local_path,  const char* remote_path);
	int put(  const char* local_path,  const char* remote_path);
		// Errors for put():
		// File can't be written
		// Timeout
		// Non-authenticated user
	int mkdir(const char* directory,   const char* remote_path);
	int rmdir(const char* remote_path);	// remote_path is the full path of the Directory being deleted
	int rm(const char*remote_path);		// remote_path is the full path of the file being deleted (Single files only)
///////////////////////////////////////////////////////////////////////
private:
	unsigned int port;
};

#endif /* MESSAGING_H_ */
