OBJS = server_file_test.o Server.o
CC = g++
DEBUG = -g
CFLAGS = -Wall -c $(DEBUG)
LFLAGS = -Wall $(DEBUG)

all : server_file_test

server_file_test : $(OBJS)
	$(CC) $(LFLAGS) $(OBJS) -o $@

server_file_test.o : server_file_test.cpp Server.h
	$(CC) $(CFLAGS) server_file_test.cpp -o $@

Server.o : Server.h Server.cpp
	$(CC) $(CFLAGS) Server.cpp -o $@

clean:
	\rm *.o server_file_test
