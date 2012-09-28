#include <iostream>
#include <string>
#ifdef WINDOWS
#else
	#include <unistd.h>
	#define _getcwd getcwd
#endif
#include "system.h"

using namespace std;

int main(char* argv[], unsigned int argc){

	PROGNAME cmdline;
	string cmd;
	char c;
	char** cmdargv = NULL;
	unsigned int cmdargc = 0;
	while(1){	
		// command line loop
		char* t1;
		char* t2;
		t1 = NULL; 
		t2 = NULL;
		cmdargc = 0;
		cmdargv = NULL;
		cout << CMDNAME <<"> ";
		cmd = "";
		for( cin.get(c);c !='\n';cin.get(c))cmd += c;
		if(cmd.length() == 0) continue;
		cmdargv = reinterpret_cast<char**>(malloc(sizeof(char *)));
		t1 = new char[cmd.length()+1];
		t1[cmd.length()] = '\0';
		const char* ccmd = cmd.c_str();
		cmdargv[0] = t1;
		for(int i = 0 ; i < cmd.length(); i++) t1[i] = ccmd[i];
		t2 = strtok(t1, " "); 
		for(t2 = strtok(NULL, " "); t2 != NULL;t2 = strtok(NULL, " ")){
			cmdargc++;
			// 1 to account for $0 and 1 to account for the added command	
			cmdargv = reinterpret_cast<char**>(realloc(cmdargv,sizeof(char*) * (cmdargc + 1))); 
			cmdargv[cmdargc] = t2;
		}
		cmd = "" ; cmd += cmdargv[0];
		cmdline.runcmd(cmdargv,cmdargc);
		if(cmdline.isExitted()) return 0;
		// List of functions
		delete[] t1;
		delete[] cmdargv;
	}
	return 0;
}

