#include <stdio.h>
#include <stdlib.h>

#define MAX(x,y) (((x) > (y)) ? (x) : (y))
#ifndef uint64_t
#define uint64_t unsigned long long
#endif

uint64_t compute_hash(FILE * handle)
{
        uint64_t hash, fsize;

        fseek(handle, 0, SEEK_END);
        fsize = ftell(handle);
        fseek(handle, 0, SEEK_SET);

        hash = fsize;

        for(uint64_t tmp = 0, i = 0; i < 65536/sizeof(tmp) && fread((char*)&tmp, sizeof(tmp), 1, handle); hash += tmp, i++);
        fseek(handle, (long)MAX(0, fsize - 65536), SEEK_SET);
        for(uint64_t tmp = 0, i = 0; i < 65536/sizeof(tmp) && fread((char*)&tmp, sizeof(tmp), 1, handle); hash += tmp, i++);
        
        return hash;
}

int main(int argc, char *argv)
{
        FILE * handle;
        uint64_t myhash;
        char filename[100];

	printf("Enter file path to calculate hash: ");
	scanf("%s",filename);
        handle = fopen(filename, "rb");
        
        if (!handle) 
        {
                printf("Error openning file!");
                return 1;
        }

        myhash = compute_hash(handle);  
        printf("%x", myhash);

        fclose(handle);
        return 0;
}