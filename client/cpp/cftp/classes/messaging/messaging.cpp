/*
 * Messaging.cpp
 *
 *  Created on: Oct 16, 2012
 *      Author: bwilson30
 */

#include "messaging.h"
#include <cstdlib>
#include <cstdio>
#include <iostream>
#include <string>
#define VERBOSE 1

using namespace std;

void Messaging::Messaging(unsigned int port_num, Encryption encrpyt) :
		m_port(port_num), m_encrypt(encrypt) {
}

void Messaging::setPort(unsigned int port_num) {
	m_port = port_num;
}

void Messaging::setEncrypter(Encryption encrypt) {
	m_encrypt = encrypt;
}

int Messaging::get(const char* local_path, const char* remote_path) {
	// Default to these values. Could later add ability to specify
	int dataport = -1;
	int rev = 1;
	bool bResult = false;

	if (VERBOSE) {
		cout << "Client: system " << m_encrypt.getIPAddress() << ", port "
				<< m_port << ", datagram port " << dataport
				<< ", reverse bytes " << rev << endl << flush;
	}

	Client mylink(m_encrypt, m_port, dataport, m_encrypt.getIPAddress(), rev,
			&bResult);

	if (!bResult) {
		if (VERBOSE) {
			cout << "Failed to create Client object!" << endl << flush;
		}
		return -1;
	}

	if (VERBOSE) {
		cout << "Client, made connection..." << endl << flush;
	}

	mylink.SendString((char*) "get^");

	string r_path = remote_path;
	size_t found = r_path.find('\0');
	if (found == string::npos) { // No NULL char found
		r_path.push_back('^');
	} else {
		r_path.insert(found, '^');
	}
	mylink.SendString((char*) r_path.c_str());

	int fileSize;
	mylink.RecvInts(&fileSize, 1);
	if (fileSize <= 0) {
		if (VERBOSE) {
			cout << "Error: fileSize contains invalid value " << fileSize
					<< endl << flush;
		}
		return flieSize;
	}
	if (VERBOSE) {
		cout << "fileSize is: " << fileSize << endl << flush;
	}

	int bytes_recv = mylink.RecvFile(local_path, fileSize);
	if (bytes_recv <= 0) {
		cout << "Error: bytes_recv contains invalid value " << bytes_recv
				<< endl << flush;
		return -1;
	}
	if (VERBOSE) {
		cout << "Received " << bytes_recv << " bytes" << endl;
		cout << "Client, closing connection..." << endl << flush;
	}

	mylink.Close();

	if (VERBOSE) {
		cout << "Client, done..." << endl << flush;
	}

	return fileSize;
}

int Messaging::put(const char* local_path, const char* remote_path) {
	// Default to these values. Could later add ability to specify
	int dataport = -1;
	int rev = 1;
	bool bResult = false;

	if (VERBOSE) {
		cout << "Client: system " << m_encrypt.getIPAddress() << ", port "
				<< m_port << ", datagram port " << dataport
				<< ", reverse bytes " << rev << endl << flush;
	}

	Client mylink(m_encrypt, m_port, dataport, m_encrypt.getIPAddress(), rev,
			&bResult);

	if (!bResult) {
		if (VERBOSE) {
			cout << "Failed to create Client object!" << endl << flush;
		}
		return -1;
	}

	if (VERBOSE) {
		cout << "Client, made connection..." << endl << flush;
	}

	mylink.SendString((char*) "put^");

	string r_path = remote_path;
	size_t found = r_path.find('\0');
	if (found == string::npos) { // No NULL char found
		r_path.push_back('^');
	} else {
		r_path.insert(found, '^');
	}
	mylink.SendString((char*) r_path.c_str());

	int put_response = 0;
	mylink.RecvInts(&put_response, 1);

	if (put_response < 0) {
		if (VERBOSE) {
			cout << "Error code returned for put request" << endl << flush;
		}
		return put_response;
	} else {
		mylink.SendFile(local_path);

		if (VERBOSE) {
			cout << "Client, closing connection..." << endl << flush;
		}

		mylink.Close();

		if (VERBOSE) {
			cout << "Client, done" << endl << flush;
		}
	}

	return 0;
}

int Messaging::ls(const char* local_path, const char* remote_path) {
	// Default to these values. Could later add ability to specify
	int dataport = -1;
	int rev = 1;
	bool bResult = false;

	if (VERBOSE) {
		cout << "Client: system " << m_encrypt.getIPAddress() << ", port "
				<< m_port << ", datagram port " << dataport
				<< ", reverse bytes " << rev << endl;
		fflush (NULL);
	}

	Client mylink(m_encrypt, m_port, dataport, m_encrypt.getIPAddress(), rev,
			&bResult);

	if (!bResult) {
		cout << "Failed to create Client object!" << endl;
		return -1;
	}

	if (VERBOSE) {
		cout << "Client, made connection..." << endl;
		fflush (NULL);
	}

	mylink.SendString((char*) "ls^");

	string r_path = remote_path;
	size_t found = r_path.find('\0');
	if (found == string::npos) { // No NULL char found
		r_path.push_back('^');
	} else {
		r_path.insert(found, '^');
	}
	mylink.SendString((char*) r_path.c_str());

	int fileSize;
	mylink.RecvInts(&fileSize, 1);
	if (fileSize <= 0) {
		if (VERBOSE) {
			cout << "Error: fileSize contains invalid value " << fileSize
					<< endl << flush;
		}
		return fileSize;
	}
	if (VERBOSE) {
		cout << "fileSize is: " << fileSize << endl << flush;
	}

	int bytes_recv = mylink.RecvFile(local_path, fileSize);
	if (bytes_recv <= 0) {
		cout << "Error: bytes_recv contains invalid value " << bytes_recv
				<< endl << flush;
		return -1;
	}
	if (VERBOSE) {
		cout << "Received " << bytes_recv << " bytes" << endl;
		cout << "Client, closing connection..." << endl << flush;
	}

	mylink.Close();

	if (VERBOSE) {
		cout << "Client, done..." << endl << flush;
	}

	return 0;
}

int Messaging::mkdir(const char* remote_path) {
	// Default to these values. Could later add ability to specify
	int dataport = -1;
	int rev = 1;
	bool bResult = false;

	if (VERBOSE) {
		cout << "Client: system " << m_encrypt.getIPAddress() << ", port "
				<< m_port << ", datagram port " << dataport
				<< ", reverse bytes " << rev << endl << flush;
	}

	Client mylink(m_encrypt, m_port, dataport, m_encrypt.getIPAddress(), rev,
			&bResult);

	if (!bResult) {
		if (VERBOSE) {
			cout << "Failed to create Client object!" << endl << flush;
		}
		return -1;
	}

	if (VERBOSE) {
		cout << "Client, made connection..." << endl << flush;
	}

	mylink.SendString((char*) "mkdir^");

	string r_path = remote_path;
	size_t found = r_path.find('\0');
	if (found == string::npos) { // No NULL char found
		r_path.push_back('^');
	} else {
		r_path.insert(found, '^');
	}
	mylink.SendString((char*) r_path.c_str());

	int response = 0;
	mylink.RecvInts(&response, 1);

	mylink.Close();

	if (VERBOSE) {
		cout << "Client, done..." << endl << flush;
	}

	return response;
}

int Messaging::rmdir(const char* remote_path) {
	// Default to these values. Could later add ability to specify
	int dataport = -1;
	int rev = 1;
	bool bResult = false;

	if (VERBOSE) {
		cout << "Client: system " << m_encrypt.getIPAddress() << ", port "
				<< m_port << ", datagram port " << dataport
				<< ", reverse bytes " << rev << endl << flush;
	}

	Client mylink(m_encrypt, m_port, dataport, m_encrypt.getIPAddress(), rev,
			&bResult);

	if (!bResult) {
		if (VERBOSE) {
			cout << "Failed to create Client object!" << endl << flush;
		}
		return -1;
	}

	if (VERBOSE) {
		cout << "Client, made connection..." << endl << flush;
	}

	mylink.SendString((char*) "rmdir^");

	string r_path = remote_path;
	size_t found = r_path.find('\0');
	if (found == string::npos) { // No NULL char found
		r_path.push_back('^');
	} else {
		r_path.insert(found, '^');
	}
	mylink.SendString((char*) r_path.c_str());

	int response = 0;
	mylink.RecvInts(&response, 1);

	mylink.Close();

	if (VERBOSE) {
		cout << "Client, done..." << endl << flush;
	}

	return response;
}

int Messaging::rm(const char* remote_path) {
	// Default to these values. Could later add ability to specify
	int dataport = -1;
	int rev = 1;
	bool bResult = false;

	if (VERBOSE) {
		cout << "Client: system " << m_encrypt.getIPAddress() << ", port "
				<< m_port << ", datagram port " << dataport
				<< ", reverse bytes " << rev << endl << flush;
	}

	Client mylink(m_encrypt, m_port, dataport, m_encrypt.getIPAddress(), rev,
			&bResult);

	if (!bResult) {
		if (VERBOSE) {
			cout << "Failed to create Client object!" << endl << flush;
		}
		return -1;
	}

	if (VERBOSE) {
		cout << "Client, made connection..." << endl << flush;
	}

	mylink.SendString((char*) "rm^");

	string r_path = remote_path;
	size_t found = r_path.find('\0');
	if (found == string::npos) { // No NULL char found
		r_path.push_back('^');
	} else {
		r_path.insert(found, '^');
	}
	mylink.SendString((char*) r_path.c_str());

	int response = 0;
	mylink.RecvInts(&response, 1);

	mylink.Close();

	if (VERBOSE) {
		cout << "Client, done..." << endl << flush;
	}

	return response;
}
