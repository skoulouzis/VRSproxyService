#!/bin/sh

#------------------indexWS---------------------------------------------------------


# ---------------------------------------------------searchWS---------------------------------------------------
#./proxyService.sh http://elab.science.uva.nl:8080/axis/services/ProxyService asyncCallService SearcherWS search '<string>universalIndex2</string>,<string>do</string>,<string>10</string>,<string>content</string>' 

# ./proxyService.sh http://elab.science.uva.nl:8080/axis/services/ProxyService callService SearcherWS search '<string>universalIndex2</string>,<string>do</string>,<string>10</string>,<string>content</string>'


# ./proxyService.sh http://elab.science.uva.nl:8080/axis/services/ProxyService callServiceReturnObject SearcherWS search '<string>universalIndex2</string>,<string>do</string>,<string>1</string>,<string>content</string>' outputargs/se.xml
# ...or
# ./proxyService.sh http://elab.science.uva.nl:8080/axis/services/ProxyService callServiceReturnObject SearcherWS search '<string>universalIndex2</string>,<string>do</string>,<string>1</string>,<string>content</string>'


# ./proxyService.sh http://elab.science.uva.nl:8080/axis/services/ProxyService asyncCallServiceReturnObject SearcherWS search '<string>universalIndex2</string>,<string>do</string>,<string>1</string>,<string>content</string>' 
# this will return an int and then..............
# ./proxyService.sh http://elab.science.uva.nl:8080/axis/services/ProxyService getReturnedValue 700653158
# ......or
# ./proxyService.sh http://elab.science.uva.nl:8080/axis/services/ProxyService getReturnedValue <int>700653158</int>


# /proxyService.sh http://elab.science.uva.nl:8080/axis/services/ProxyService getFileURI file:///tmp


# ./proxyService.sh http://elab.science.uva.nl:8080/axis/services/ProxyService getUploadURI 21

# /proxyService.sh http://elab.science.uva.nl:8080/axis/services/ProxyService getFileUploadURI

# ./proxyService.sh http://elab.science.uva.nl:8080/axis/services/ProxyService uploadArgs '<string>dfd</string>,<int>4343</int>'


# ./proxyService.sh http://elab.science.uva.nl:8080/axis/services/ProxyService uploadFiles nosb /tmp/demo-HPDC-2009-1.doc,/tmp/demo-HPDC-2009.doc,/home/skoulouz/docs/Poster.pdf


# -----------------------------------------NERecognizerService--------------------------------

# http://146.50.22.71:8080/axis/HTTPTransport?484471001

# ./proxyService.sh http://elab.science.uva.nl:8080/axis/services/ProxyService callServiceReturnObject NERecognizerService NErecognize '<string>wsdt://input.ref.mem/dummypath?http://146.50.22.71:8080/axis/HTTPTransport?484471001#</string>,<string>News</string>,<string>xml</string>,<string>NElist</string>'


if [ -n "$6" ]; then 
	OUT_PUT_FILE=`pwd`"/$6"
fi

java -cp ../dist/VRSproxyService.jar proxyWS.clients.ProxyServiceClient "$1" "$2" "$3" "$4" "$5" "$OUT_PUT_FILE"