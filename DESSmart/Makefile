#set your project name
PRJ_NAME = ad_match_exe
#LDFLAGS =  $(SFLAGS) -O3 -lm -Wall -v 
#set your project type : choose one below
PRJ_TYPE = g++ 
#PRJ_TYPE = g++ -shared
#PRJ_TYPE = ar -r 

#set Debug or Release
#Compile_Flag = Release
Compile_Flag = Debug

#set your output path
#Output:= bin
Output:=./

#set your source folder
SRC:= src

#add the lib you used here

#LIBS := -lLib1 -lLib2 -lLib3
LIBS :=  -levent -lpthread 

#LIBPATH := -Lpath1 -Lpath2 -Lpath3
LIBPATH := -L ./include/libevent/lib

# INCLUDEPATH := -I/usr/lib/XXX/include
INCLUDEPATH := -I./include/libevent/include/

###################################
#DON"T MODIFY THE BELOWS

#combine output folder
FinalOutput := $(Output)/$(Compile_Flag)/

#list all dirs
SUBDIRS := $(shell find $(SRC) -type d)

#flags in makefile
DEBUG_FLAG = -O0 -g -Wall -c -fmessage-length=0
RELEASE_FLAG = -O3 -Wall -c -fmessage-length=0

RM := rm -rf

#set compile flag
ifeq ($(Compile_Flag),Debug)
CFLAGS := $(DEBUG_FLAG)
else
CFLAGS := $(RELEASE_FLAG)
endif

#prepare files
CPP_SRCS:=$(shell find $(SRC) -name *.cpp)

OBJS:=$(CPP_SRCS:%.cpp=$(FinalOutput)%.o)

#all target
#all: dir $(FinalOutput)$(PRJ_NAME)
all: dir $(Output)/$(PRJ_NAME)

dir:
	mkdir -p $(FinalOutput);
	for val in $(SUBDIRS);do \
	  mkdir -p $(FinalOutput)$${val}; \
	done;

#tool invocations
#$(FinalOutput)$(PRJ_NAME):$(OBJS)
$(Output)/$(PRJ_NAME):$(OBJS)
	@echo 'Building target: $@'
	@echo 'Invoking:GCC C++ Linker'
	$(PRJ_TYPE) $(LIBPATH) -o"$@" $^ $(LIBS) 
	@echo 'Finished building target: $@'
	@echo ' '

$(FinalOutput)%o:./%cpp
	@echo 'Building file: $<'
	@echo 'Invoking:GCC C++ Compiler'
	g++ $(CFLAGS)  $(INCLUDEPATH) -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o"$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

#other targets
clean:
	#-$(RM) $(Output)/*
	-$(RM) $(FinalOutput)/*
	-$(RM) $(Output)/$(PRJ_NAME)
	-@echo ' '
.PHONY:all clean
.SECONDARY:

