SRC=src
BIN=bin
.PHONY: clean all
DEF= -D CMDNAME="\"qurac\""
DEFDEBUG=-D __DEBUG=1
all: release debug
clean:
	rm -f $(BIN)/release/*
	rm -F $(BIN)/debug/*
release: $(SRC)/main.cpp
	g++ $(DEF) $(SRC)/main.cpp -o $(BIN)/release/qurac
debug: $(SRC)/main.cpp
	g++ $(DEF) $(DEFDEBUG) $(SRC)/main.cpp -o $(BIN)/debug/qurac

