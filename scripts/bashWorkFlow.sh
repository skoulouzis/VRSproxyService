#!/bin/sh

MERGER=http://elab.science.uva.nl:8080/axis/services/ProxyService
ENDPOINT1=http://ow164.science.uva.nl:8080/axis/services/ProxyService
ENDPOINT2=http://ow165.science.uva.nl:8080/axis/services/ProxyService

# ENDPOINT1=http://elab.science.uva.nl:8080/axis/services/ProxyService
# ENDPOINT2=http://elab.science.uva.nl:8080/axis/services/ProxyService

# -------------------------------------PRE STAGE---------------------------------------

MEDLINEFILES="/home/skoulouz/workspace/netbeans/AIDA/medLineFiles/testMedLine"
STOP_WORDS_FILE="/home/skoulouz/workspace/netbeans/AIDA/cfg/stopwords.cfg"
CONF_FILE="/home/skoulouz/workspace/netbeans/AIDA/Search/Indexer/indexconfig.xml"

# 1 Upload docs
# DATA_PATH1=`./proxyService.sh  $ENDPOINT1 uploadFiles2 medlineDir $MEDLINEFILES/medline09n0001_1.xml.gz,$MEDLINEFILES/medline09n0001_2.xml.gz`
# echo "Data Ref arg $DATA_PATH1"
# 
# DATA_PATH2=`./proxyService.sh $ENDPOINT2 uploadFiles2 medlineDir $MEDLINEFILES/medline09n0001_3.xml.gz,$MEDLINEFILES/medline09n0001_4.xml.gz`
# echo "Data Ref arg $DATA_PATH2"
# 
# 
# 
# # 2 upload stop word file
# STOP_FILE_PATH1=`./proxyService.sh $ENDPOINT1 uploadFiles2 conf $STOP_WORDS_FILE`
# echo "Stop words Ref arg $STOP_FILE_PATH1"
# 
# STOP_FILE_PATH2=`./proxyService.sh $ENDPOINT2 uploadFiles2 conf $STOP_WORDS_FILE`
# echo "Stop words Ref arg $STOP_FILE_PATH2"
# 
# 
# # 3 upload conf file
# CONF_FILE_PATH1=`./proxyService.sh $ENDPOINT1 uploadFiles2 conf $CONF_FILE`
# echo "Conf Ref arg $CONF_FILE_PATH1"
# 
# CONF_FILE_PATH2=`./proxyService.sh $ENDPOINT2 uploadFiles2 conf $CONF_FILE`
# echo "Conf Ref arg $CONF_FILE_PATH1"
# 
# 
# #--------------------------- PROCESS -------------------------------------
# # 1 Run the indexer
# SERVICE_NAME="IndexBaselineWS"
# METHOD_NAME="indexDocs"
# 
# 
# ARGS1="<boolean>true</boolean>,<string>SubIndex</string>,<string>$STOP_FILE_PATH1</string>,<string>$DATA_PATH1</string>"
# echo $ARGS1
# 
# KEY1=`./proxyService.sh $ENDPOINT1 asyncCallServiceReturnObject $SERVICE_NAME $METHOD_NAME $ARGS1`
# echo "Ref Key1 $KEY1"
# 
# 
# ARGS2="<boolean>true</boolean>,<string>SubIndex</string>,<string>$STOP_FILE_PATH2</string>,<string>$DATA_PATH2</string>"
# KEY2=`./proxyService.sh $ENDPOINT2 asyncCallServiceReturnObject $SERVICE_NAME $METHOD_NAME $ARGS2` 
# echo "Ref Key1 $KEY2"
# 
# 
# # 2 Pole for results 
# 
# INDEX_LOC1=`./proxyService.sh $ENDPOINT1 getReturnedValue $KEY1`
# INDEX_LOC2=`./proxyService.sh $ENDPOINT2 getReturnedValue $KEY2`
# 
# WS_DONE=0
# SUBINDEX1=subIndexFiles1.out
# SUBINDEX2=subIndexFiles2.out
# while true; do
# 
# 	if [ $INDEX_LOC1 != "<null/>" ]; then
# 		INDEX_LOC1="file://"$INDEX_LOC1"/"
# 		echo "URI $INDEX_LOC1"
# 		./proxyService.sh $ENDPOINT1 list $INDEX_LOC1 > $SUBINDEX1
# 		let WS_DONE=WS_DONE+1
# 	fi
# 
# 
# 	if [ $INDEX_LOC2 != "<null/>" ]; then
# 		INDEX_LOC2="file://"$INDEX_LOC2"/"
# 		echo "URI $INDEX_LOC1"
# 		./proxyService.sh $ENDPOINT2 list $INDEX_LOC2 > $SUBINDEX2
# 		let WS_DONE=WS_DONE+1
# 	fi	
# 
# 	if [ $WS_DONE == 2 ]; then
# 		echo "Done!!! "
# 		break
# 	fi
# 	
# # 	echo "Done??? $WS_DONE"
# 	echo "Sleeping"
# 	sleep 1
# 	INDEX_LOC1=`./proxyService.sh $ENDPOINT1 getReturnedValue $KEY1`
# 	INDEX_LOC2=`./proxyService.sh $ENDPOINT2 getReturnedValue $KEY2`
# done
# 
# 
# PATHS=""
# for i in $( cat subIndexFiles1.out ); do
# 	PATHS=$PATHS`./proxyService.sh $ENDPOINT1 getFileURI $i`
# done


CONFIG=`cat $CONF_FILE`
#  \n have to go 
# cat $CONF_FILE
# CONFIG=`tr -d '\n ' < $CONF_FILE`

PATHS="wsdt://input.ref.dir/subindex_1?"$PATHS"#"


ARGS="<string>$CONFIG</string>,<string>universalIndex</string>,<string-array><string>$PATHS</string></string-array>"
  
SERVICE="IndexWS2"
METHOD="mergeIndexFromFileInFile"
./proxyService.sh $MERGER callServiceReturnObject $SERVICE $METHOD $ARGS




# MARGE_ARG1=inputargs/confString.xml
# echo "<string>$CONFIG</string>" > $MARGE_ARG1
# 
# MARGE_ARG2=inputargs/indexName.xml
# echo "<string>universalIndex</string>" > $MARGE_ARG2
# 
# MARGE_ARG3=inputargs/indexPaths.xml
# echo "<string-array><string>$PATHS</string></string-array>" > $MARGE_ARG3
# 
# 
# MARGE_ARGS_FILE=/home/skoulouz/workspace/netbeans/VRSproxyService/scripts/inputargs/mergeWSArgs.in
# 
# echo $MARGE_ARG1 >> $MARGE_ARGS_FILE
# echo $MARGE_ARG2 >> $MARGE_ARGS_FILE
# echo $MARGE_ARG2 >> $MARGE_ARGS_FILE
# ./proxyService.sh $MERGER callServiceReturnObject $SERVICE $METHOD $MARGE_ARGS_FILE
