# the following library is needs on SUNs from my experience
# LIBS = -lxnet

CC = g++
JAVAC = javac

default: java_client_test java_server_test client_test server_test

clean:
	rm *.o;rm *.class

java_server_test: Server.class java_server_test.class

Server.class: Server.java
	$(JAVAC) Server.java

java_server_test.class: java_server_test.java
	$(JAVAC) java_server_test.java

java_client_test: Client.class java_client_test.class

Client.class: Client.java
	$(JAVAC) Client.java

java_client_test.class: java_client_test.java
	$(JAVAC) java_client_test.java

client_test: Client.o client_test.o
	$(CC) Client.o client_test.o -o client_test $(LIBS)

server_test: Server.o server_test.o
	$(CC) Server.o server_test.o -o server_test $(LIBS)

server_test.o: server_test.cpp
	$(CC) -c server_test.cpp

Server.o: Server.cpp Server.h
	$(CC) -c Server.cpp

client_test.o: client_test.cpp
	$(CC) -c client_test.cpp

Client.o: Client.cpp Client.h
	$(CC) -c Client.cpp

# DO NOT DELETE THIS LINE -- make depend depends on it.
