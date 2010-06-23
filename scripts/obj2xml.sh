#!/bin/sh

# example args: "./obj2xml.sh string sdfjknsodf" 


if [ -n "$3" ]; then 
	OUT_PUT_FILE=`pwd`"/$3"
fi





java -cp ../dist/VRSproxyService.jar proxyWS.utils.Misc "obj2XML" $1 $2 $OUT_PUT_FILE 