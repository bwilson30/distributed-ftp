#include <iostream>
#include <string>

using namespace std;
#define DECLARE_COMMAND(cmd) unsigned char cmd(char* argv[],unsigned int argc)
// List of Commands
// General Operations
DECLARE_COMMAND(quit);
DECLARE_COMMAND(login);
DECLARE_COMMAND(help);
// Navigation Commands
DECLARE_COMMAND(cd);
DECLARE_COMMAND(ls);
// Transactions on the filesystem
DECLARE_COMMAND(mkdir);
DECLARE_COMMAND(rmdir);
DECLARE_COMMAND(rm);
DECLARE_COMMAND(put);
DECLARE_COMMAND(get);

int main(char* argv[], unsigned int argc){
	string cmd;
	char c;
	char** cmdargv = NULL;
	unsigned int cmdargc = 0;
	while(1){
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
			cmdargv = reinterpret_cast<char**>(realloc(cmdargv,sizeof(char*) * (cmdargc + 1 + 1))); 
			cmdargv[cmdargc+1] = t2;
		}
		cmd = "" ; cmd += cmdargv[0];
		// List of functions
		if(cmd == "quit" || cmd == "exit") return quit(cmdargv,cmdargc);
		else if(cmd == "login") login(cmdargv,cmdargc);
		else if(cmd == "help") 	help(cmdargv,cmdargc);
		else if(cmd == "cd") 	cd(cmdargv,cmdargc);
		else if(cmd == "ls") 	ls(cmdargv,cmdargc);
		else if(cmd == "mkdir") mkdir(cmdargv,cmdargc);
		else if(cmd == "rmdir")	rmdir(cmdargv,cmdargc);
		else if(cmd == "rm")	rm(cmdargv,cmdargc);
		else if(cmd == "put")	put(cmdargv,cmdargc);
		else if(cmd == "get")	get(cmdargv,cmdargc);
		else cout << "Invalid Command\n";
		delete[] t1;
		delete[] cmdargv;
	}
	return 0;
}
DECLARE_COMMAND(quit){
#ifdef __DEBUG
	cout << argv[0] << "|" << argc << endl;
#endif
	return 0;
}
DECLARE_COMMAND(login){return 0;}
DECLARE_COMMAND(help){return 0;}
// Navigation Commands
DECLARE_COMMAND(cd){return 0;}
DECLARE_COMMAND(ls){return 0;}
// Transactions on the filesystem
DECLARE_COMMAND(mkdir){return 0;}
DECLARE_COMMAND(rmdir){return 0;}
DECLARE_COMMAND(rm){return 0;}
DECLARE_COMMAND(put){return 0;}
DECLARE_COMMAND(get){return 0;}
