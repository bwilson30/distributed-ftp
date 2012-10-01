OBJS = client_file_test.o Client.o
CC = g++
DEBUG = -g
CFLAGS = -Wall -c $(DEBUG)
LFLAGS = -Wall $(DEBUG)

all : client_file_test

client_file_test : $(OBJS)
	$(CC) $(LFLAGS) $(OBJS) -o $@

client_file_test.o : client_file_test.cpp Client.h
	$(CC) $(CFLAGS) client_file_test.cpp -o $@

Client.o : Client.h Client.cpp
	$(CC) $(CFLAGS) Client.cpp -o $@

clean:
	\rm *.o client_file_test
