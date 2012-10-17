PROGNAME=qurac
SRC=src
BIN=bin
CLASSES=classes
INC=inc
OBJ=obj
CPP=g++
c=gcc
#################
CLASS_LIST= $(patsubst classes/%,%, $(wildcard classes/*))
ALL_INC = $(addprefix -I ,$(wildcard classes/*)) -I inc/
#################
get_class=$(shell echo $(1) | sed -ne "s/classes\/\(\w*\)\/\w*.h/\1/p")
get_class_list= $(foreach path,$(filter classes/%.h,$(1)),$(call get_class, $(path)))
get_class_dep=$(call get_class_list, $(shell g++ $(ALL_INC) -MM $(1) 2>/dev/null | sed -ne 's/[^:]*:\(.*\)/\1/p')) 
get_header_dep=$(filter %.h,$(shell g++ $(ALL_INC) -MM $(1) 2>/dev/null | sed -ne 's/[^:]*:\(.*\)/\1/p'))
#get_class= $(filter classes,$(1))
#$(patsubst classes/%/%.h,%,
#$(shell echo $(path) | sed 's#classes/\([^/]*\)/.*#\1#'))
#)


#.PHONY: test all clean release debug
DEF= -D CMDNAME="\"$(PROGNAME)\""
DEF+= -D TERMINATING_CHARACTER="\"^\""
DEFDEBUG=-D __DEBUG=1
.PHONY: all release debug clean build_main

	# $(CLASSES)/%/%.h $(CLASSES)/%/%.cpp
all: release debug
clean:
	rm -f $(BIN)/release/*
	rm -f $(BIN)/debug/*
	rm -f $(OBJ)/release/*
	rm -f $(OBJ)/debug/*
release: OUTDIR=release
release: build_main 
debug: OUTDIR=debug
debug: DEF:=$(DEFDEBUG) $(DEF) 
debug: build_main
build_main: $(addsuffix _class.o, $(call get_class_dep, $(SRC)/main.cpp)) $(SRC)/main.cpp
	g++ $(DEF) $(ALL_INC)							\
	$(addprefix $(OBJ)/$(OUTDIR)/,$(addsuffix .o, $(call get_class_dep, $(SRC)/main.cpp)))	\
	$(SRC)/main.cpp -o $(BIN)/$(OUTDIR)/$(PROGNAME)
%_class.o: $(call get_class_dep, $(addprefix $(CLASSES)/%,%.cpp))
	g++ $(DEF) $(ALL_INC) -c 	\
	$(CLASSES)/$*/$*.cpp							\
	-o $(OBJ)/$(OUTDIR)/$*.o 
	echo "$(call get_class_dep, $(addprefix $(CLASSES)/%,%.cpp))"
