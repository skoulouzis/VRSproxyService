#!/bin/sh


LIB_LOCATION=$CATALINA_HOME/webapps/axis/WEB-INF/lib/
CP=$LIB_LOCATION/commons-logging.jar:$LIB_LOCATION/commons-discovery-0.2.jar:$LIB_LOCATION/jaxrpc.jar:$LIB_LOCATION/wsdl4j-1.5.1.jar:$LIB_LOCATION/axis.jar:$LIB_LOCATION/saaj.jar


java -cp $CP org.apache.axis.wsdl.WSDL2Java $1 -o ../src/stubs 