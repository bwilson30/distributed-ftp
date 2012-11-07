/*
* File Hashing
* Vinay Bharadwaj (vind.1989@gatech.edu)
* Calculates hash as Size + 64 bit checksum of the first and
* last 64 bit
*
*/

#include "filehash.h"

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

/*
int main(int argc, char **argv)
{
        FILE * handle;
        uint64_t myhash;
        char filename[100];

	if(argc < 2){
        printf("Usage: ./hash \"file_name\"");
	   exit (-1);
	}

        handle = fopen(argv[1], "rb");

        if (!handle)
        {
                printf("Error opening file!");
                return 1;
        }

        myhash = compute_hash(handle);
        printf("%x", myhash);

        fclose(handle);
        return 0;
}
*/
