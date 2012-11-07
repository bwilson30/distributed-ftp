/*
 * filehash.h
 *
 *  Created on: Oct 22, 2012
 *      Author: Vinay Bharadwaj
 */
#include <stdio.h>
#include <stdlib.h>

#define MAX(x,y) (((x) > (y)) ? (x) : (y))
#ifndef uint64_t
#define uint64_t unsigned long long
#endif

uint64_t compute_hash(FILE * handle);
