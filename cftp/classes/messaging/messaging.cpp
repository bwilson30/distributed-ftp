/*
 * Messaging.cpp
 *
 *  Created on: Oct 16, 2012
 *      Author: bwilson30
 */

#include "Messaging.h"
#include <cstdlib>
#include <cstdio>
#include <iostream>
#define VERBOSE 1

using namespace std;

// Note: remote_path MUST have a ^ terminating the string
int Messaging::get(const char* ip_addr, const char* local_path,
		const char* remote_path) {
	// Default to these values. Could later add ability to specify
	int port = 5010;
	int dataport = -1;
	int rev = 1;
	bool bResult = false;

	if (VERBOSE) {
		cout << "Client: system " << ip_addr << ", port " << port
				<< ", datagram port " << dataport << ", reverse bytes " << rev
				<< endl;
		fflush(NULL);
	}

	Client mylink(port, dataport, ip_addr, rev, &bResult);

	if (!bResult) {
		cout << "Failed to create Client object!" << endl;
		return -1;
	}

	if (VERBOSE) {
		cout << "Client, made connection..." << endl;
		fflush(NULL);
	}

	mylink.SendString((char*) "get^");

	mylink.SendString((char*) remote_path);

	int fileSize;
	mylink.RecvInts(&fileSize, 1);
	if (fileSize <= 0) {
		cout << "Error: fileSize contains invalid value " << fileSize << endl;
		return -1;
	}
	if (VERBOSE) {
		cout << "fileSize is: " << fileSize << endl;
	}

	int bytes_recv = mylink.RecvFile(local_path, fileSize);
	if (bytes_recv <= 0) {
		cout << "Error: bytes_recv contains invalid value " << bytes_recv
				<< endl;
		return -1;
	}
	if (VERBOSE) {
		cout << "Received " << bytes_recv << " bytes" << endl;
		cout << "Client, closing connection..." << endl;
		fflush(NULL);
	}

	mylink.Close();

	if (VERBOSE) {
		cout << "Client, done..." << endl;
		fflush(NULL);
	}

	return 0;
}

int Messaging::put(const char* ip_addr, const char* local_path,
		const char* remote_path) {
	// Default to these values. Could later add ability to specify
	int port = 5010;
	int dataport = -1;
	int rev = 1;
	bool bResult = false;

	if (VERBOSE) {
		cout << "Client: system " << ip_addr << ", port " << port
				<< ", datagram port " << dataport << ", reverse bytes " << rev
				<< endl;
		fflush(NULL);
	}

	Client mylink(port, dataport, ip_addr, rev, &bResult);

	if (!bResult) {
		cout << "Failed to create Client object!" << endl;
		return -1;
	}

	if (VERBOSE) {
		cout << "Client, made connection..." << endl;
		fflush(NULL);
	}

	mylink.SendString((char*) "put^");

	mylink.SendString((char*) remote_path);

	mylink.SendFile(local_path);

	if (VERBOSE) {
		cout << "Client, closing connection..." << endl;
		fflush(NULL);
	}

	mylink.Close();

	if (VERBOSE) {
		cout << "Client, done" << endl;
		fflush(NULL);
	}

	return 0;
}

int Messaging::ls(const char* ip_addr, const char* local_path,
		const char* remote_path) {
	// Default to these values. Could later add ability to specify
	int port = 5010;
	int dataport = -1;
	int rev = 1;
	bool bResult = false;

	if (VERBOSE) {
		cout << "Client: system " << ip_addr << ", port " << port
				<< ", datagram port " << dataport << ", reverse bytes " << rev
				<< endl;
		fflush(NULL);
	}

	Client mylink(port, dataport, ip_addr, rev, &bResult);

	if (!bResult) {
		cout << "Failed to create Client object!" << endl;
		return -1;
	}

	if (VERBOSE) {
		cout << "Client, made connection..." << endl;
		fflush(NULL);
	}

	mylink.SendString((char*) "ls^");

	mylink.SendString((char*) remote_path);

	int fileSize;
	mylink.RecvInts(&fileSize, 1);
	if (fileSize <= 0) {
		cout << "Error: fileSize contains invalid value " << fileSize << endl;
		return -1;
	}
	if (VERBOSE) {
		cout << "fileSize is: " << fileSize << endl;
	}

	int bytes_recv = mylink.RecvFile(local_path, fileSize);
	if (bytes_recv <= 0) {
		cout << "Error: bytes_recv contains invalid value " << bytes_recv
				<< endl;
		return -1;
	}
	if (VERBOSE) {
		cout << "Received " << bytes_recv << " bytes" << endl;
		cout << "Client, closing connection..." << endl;
		fflush(NULL);
	}

	mylink.Close();

	if (VERBOSE) {
		cout << "Client, done..." << endl;
		fflush(NULL);
	}

	return 0;
}

int Messaging::mkdir(const char* ip_addr, const char* remote_path) {
	// Default to these values. Could later add ability to specify
	int port = 5010;
	int dataport = -1;
	int rev = 1;
	bool bResult = false;

	if (VERBOSE) {
		cout << "Client: system " << ip_addr << ", port " << port
				<< ", datagram port " << dataport << ", reverse bytes " << rev
				<< endl;
		fflush(NULL);
	}

	Client mylink(port, dataport, ip_addr, rev, &bResult);

	if (!bResult) {
		cout << "Failed to create Client object!" << endl;
		return -1;
	}

	if (VERBOSE) {
		cout << "Client, made connection..." << endl;
		fflush(NULL);
	}

	mylink.SendString((char*) "mkdir^");

	mylink.SendString((char*) remote_path);

	int response = 0;
	mylink.RecvInts(&response, 1);

	return response;
}

int Messaging::rmdir(const char* ip_addr, const char* remote_path) {
	// Default to these values. Could later add ability to specify
	int port = 5010;
	int dataport = -1;
	int rev = 1;
	bool bResult = false;

	if (VERBOSE) {
		cout << "Client: system " << ip_addr << ", port " << port
				<< ", datagram port " << dataport << ", reverse bytes " << rev
				<< endl;
		fflush(NULL);
	}

	Client mylink(port, dataport, ip_addr, rev, &bResult);

	if (!bResult) {
		cout << "Failed to create Client object!" << endl;
		return -1;
	}

	if (VERBOSE) {
		cout << "Client, made connection..." << endl;
		fflush(NULL);
	}

	mylink.SendString((char*) "rmdir^");

	mylink.SendString((char*) remote_path);

	int response = 0;
	mylink.RecvInts(&response, 1);

	return response;
}
