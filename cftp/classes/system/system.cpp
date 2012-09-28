#include "system.h"
#include <termios.h>
#include <unistd.h>
#include <dirent.h>
#include <errno.h>
PROGNAME::PROGNAME(){
	char path[100];
	lwd = "";
	rwd = "";
	getcwd(path,100);
	lwd+= path;
	done = 0;
}
unsigned char PROGNAME::runcmd(char *argv[],unsigned int argc){
	std::string cmd = "";
	cmd += argv[0];
	if(cmd == "quit" || cmd == "exit") 
				return quit(argv,argc); 
	else if(cmd == "login") return login(argv,argc); 
	else if(cmd == "help")  return help(argv,argc); 
	else if(cmd == "lls")   return lls(argv,argc); 
	else if(cmd == "lcd")   return lcd(argv,argc); 
	else if(cmd == "lpwd")  return lpwd(argv,argc); 
	else if(cmd == "cd")    return cd(argv,argc); 
	else if(cmd == "ls")    return ls(argv,argc); 
	else if(cmd == "pwd")   return pwd(argv,argc); 
	else if(cmd == "mkdir") return mkdir(argv,argc); 
	else if(cmd == "rmdir") return rmdir(argv,argc); 
	else if(cmd == "rm")    return rm(argv,argc); 
	else if(cmd == "put")   return put(argv,argc); 
	else if(cmd == "get")   return get(argv,argc); 
	else std::cout << "Invalid Command\n";
	return 0;
}
unsigned char PROGNAME::login(char *argv[],unsigned int argc){
	std::string username = "";
	std::string password = "";
	std::string domain = "";
	termios oldt;
	tcgetattr(STDIN_FILENO, &oldt);
	termios newt = oldt;
	newt.c_lflag &= ~ECHO;
	std::cout << "Password: ";
	tcsetattr(STDIN_FILENO, TCSANOW, &newt);
	getline(std::cin, password);
	std::cout << password << std::endl;
	tcsetattr(STDIN_FILENO, TCSANOW, &oldt);
	// The Password is stored in password
}
unsigned char PROGNAME::lls(char *argv[],unsigned int argc){
	DIR *dir;
	struct dirent *ent;
	DIR *test;
	int c;
	unsigned char l,F,a; // accepted ls flags
	l = 0; F = 0; a = 0;
	std::string path = "";
	path += lwd;
	#ifdef __DEBUG
		printf("There are %i arguments:",argc);
		for(int i =0; i <= argc; i++) std::cout << " | " << argv[i];
		std::cout << std::endl;
	#endif
	while((c = getopt(argc+1,argv,"lFa")) != -1){
	#ifdef __DEBUG
		std::cout << "The Parameter is " << c << std::endl;
	#endif
		switch(c){
			case 'l': l = 1; break;
			case 'F': F = 1; break;
			case 'a': a = 1; break;
			default: 
				std::cout << "Invalid Flag\n"; 
				return 1;	
		}
	}
	if(argc != 0 && optind <= argc && argv[optind][0] != '-'){ // if there is an input to lls
		if(argv[optind][0] == '/') 	path = "";
		else 				path += "/"; 
		path += argv[optind];
		std::cout << path << std::endl;
	}
	optind = 1; //we have to reset optind to avoid seg fault
	dir = opendir (path.c_str());
	if (dir != NULL) {
	  /* print all the files and directories within directory */
	  while ((ent = readdir (dir)) != NULL) {
	  	if(ent->d_name[0] != '.' || a){ // check for '.' paths 
			std::cout << ent->d_name;
			std::string temp = path + "/" + ent->d_name;
			if(F){		// Include the ending / if its a folder
				test = opendir(temp.c_str());
				if(test != NULL){
					std::cout << "/";
					closedir(test);
				}
			}
			if(l) std::cout << std::endl;
			else  std::cout << " ";
		}

	  }
	std::cout << std::endl;
	  closedir (dir);
	} else {
	  /* could not open directory */
	  perror ("");
	  return EXIT_FAILURE;
	}

}
unsigned char PROGNAME::lcd(char *argv[],unsigned int argc){
	std::string path = "";
	char temp[100];
	path = lwd;
	if(argc == 0) { std::cout << "lcd requires an input location\n"; return 1;}
	if(argv[1][0] == '/') 	path = ""; // reinitialize path if we aren't working with relative directory
	else 			path += "/";
	 path+=argv[1];
	if(!chdir(path.c_str())){
		getcwd(temp,100);
		lwd="";
		lwd+=temp;	
		return 0;
	}
	else std::cout << "Can't lcd to selected directory\n";
	#ifdef __DEBUG 
		char buffer[ 256 ];
		char * errorMessage = strerror_r( errno, buffer, 256 );
		std::cout << errorMessage;
	#endif

	return 1;
	
}
unsigned char PROGNAME::help(char *argv[],unsigned int argc){
	std::cout << "This program allows for read and write to a remote file repository.\n";
	std::cout << "List of Commands: \n";
	std::cout << "help: prints this help message. \n";
	std::cout << "login [username]@[domain]: reloads the login prompt for username and will \n" ;
	std::cout << "                           attempt to connect to the domain\n";
	std::cout << "quit: Exits this prompt with code 0\n";
	std::cout << "Local System Commands:\n";
	std::cout << "lls -alF [path]: Displays the local directory path relative to\n";
	std::cout << "                 the current working directory.\n";
	std::cout << "lcd [path]: Changes the local current working directory to path relative to lpwd.\n";
	std::cout << "lpwd: Displays the present working directory on the local computer.\n";
	std::cout << "Remote System Commands:\n";
        std::cout << "ls -alF [path]: Displays the directory path  on the remote system relative to\n"; 
        std::cout << "                the current working directory.\n";
        std::cout << "cd [path]: Changes the current working directory on the remote system to path.\n";
        std::cout << "pwd: Displays the present working directory on the remote system.\n";
	std::cout << "File System Interaction: \n";
	std::cout << "mkdir [name]: creates the directory name on the remote system.\n";
	std::cout << "rmdir [name]: Removes the directory name on the remote system fails if name is not empty.\n";
	std::cout << "rm -rf [name]: Removes the file name. If name is a directory -r will delete recursively.\n";
	std::cout << "put -r [name]: Puts the file name on the remote system.\n";
	std::cout << "               If name is a directory -r will add recursively\n";
	std::cout << "get -r [name]: Gets the file name from the remote system.\n";
	std::cout << "               If name is a directory -r will recursively get the directory structure\n";
}
