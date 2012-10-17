/*
 * Messaging.h
 *
 *  Created on: Oct 16, 2012
 *      Author: bwilson30
 */

#ifndef MESSAGING_H_
#define MESSAGING_H_

#include "Client.h"

class Messaging {
public:
	int get(const char* ip_addr, const char* local_path, const char* remote_path);
	int put(const char* ip_addr, const char* local_path, const char* remote_path);
	int ls(const char* ip_addr, const char* local_path, const char* remote_path);
	int mkdir(const char* ip_addr, const char* remote_path);
	int rmdir(const char* ip_addr, const char* remote_path);
};

#endif /* MESSAGING_H_ */
