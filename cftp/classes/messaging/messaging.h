#ifndef MASSAGEING_H
#define MESSAGING_H

class messaging {
	public:
		messaging(){};
		~messaging(){};
		int login(const char* username,const char* password){};
		int mkdir(const char* path){}; 
	// This will create the directory tree (i.e if path/the path and the directories will be created
		int rmdir(const char* path){}; 
		int put(const char* path){};
		int rm(const char* path){};
		int get(const char* path){};
};
#endif

