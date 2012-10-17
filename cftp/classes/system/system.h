#ifndef SYSTEM_H
#define SYSTEM_H
#include <iostream>
#include <string>
#ifdef WINDOWS
#else
        #include <unistd.h>
        #define _getcwd getcwd
#endif

#include "messaging.h"


#define DECLARE_COMMAND(cmd) unsigned char cmd(char* argv[],unsigned int argc)
#define MAX_USERPASSLENGTH 20
#define MAX_PATH 150
class PROGNAME{
public:
	PROGNAME();
	~PROGNAME(){};
	PROGNAME(char* username){};
	unsigned char runcmd(char*argv[],unsigned int argc);
	const char* getLwd(){return rwd.c_str();};
	const char* getRwd(){return rwd.c_str();}
	unsigned char isExitted(){return done;}
// List of commands
	DECLARE_COMMAND(quit){done = 1;return 0;}
	DECLARE_COMMAND(login);
	DECLARE_COMMAND(help);
	DECLARE_COMMAND(lls);
	DECLARE_COMMAND(lcd);
	DECLARE_COMMAND(lpwd){std::cout << lwd << std::endl; return 0;}
// Navigation Commands
	DECLARE_COMMAND(cd){return 0;}
	DECLARE_COMMAND(ls){return 0;}
	DECLARE_COMMAND(pwd){std::cout << rwd << std::endl; return 0;}
// Transactions on the filesystem
	DECLARE_COMMAND(mkdir){return 0;}
	DECLARE_COMMAND(rmdir){return 0;}
	DECLARE_COMMAND(rm){return 0;}
	DECLARE_COMMAND(put){return 0;}
	DECLARE_COMMAND(get){return 0;}
// Get types 
	typedef enum{
		GET_DIR,	// alias of the ls command get -d path
		MKDIR,		// make a directory 	put -n dir
		SINGLE_FILE,	// put/get a single file put file/get_file
		RMDIR,		// rm a directory 	put -r path
		RMFILE		// remove a file	put -rm -rf path
	}ACTIONS;
private:
	void authenticate(){};
	void clear_login();
protected:
	typedef struct _state{
		char lwd[MAX_PATH];
		char rwd[MAX_PATH];
		char username[1+MAX_USERPASSLENGTH]; // 1+ accounts for the endding null
		char password[1+MAX_USERPASSLENGTH]; // uesrname and password have a max length
	} prog_state;	
	prog_state state;
	std::string lwd;	// Local Working directory
	std::string rwd;	// Remote Working directory
	unsigned char done; // System is exitted

};
#endif
