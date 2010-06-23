#!/bin/sh



PRODUCER="http://ow144.science.uva.nl:8080/axis/services/SimpleService"
CONSUMER="http://ow145.science.uva.nl:8080/axis/services/SimpleService"


PROXY_PRODUCER="http://ow144.science.uva.nl:8080/axis/services/ProxyService"
PROXY_CONSUMER="http://ow145.science.uva.nl:8080/axis/services/ProxyService"

MEASUREDIR="measures"

LOOPS=0


START=3072
END=3075
STEP=1024

# # START=1024

# # END=20000

# # STEP=1024
# END=512004



c=0
METHOD="soapCall"
MEASUREFILE=$MEASUREDIR"/soapCall"
for ((i=$START;i<=$END;i+=$STEP)); do
	if [  $c == 0 ]; then
		echo "Size	$i" >> $MEASUREFILE.$i.out
		echo "ProduceTime	ConsumeTime	TotalTime" >> $MEASUREFILE.$i.out
	fi
	for ((j=0;j<=$LOOPS;j+=1)); do
		echo "-----------------------------"
		cmd="./bechMarkLegacy.sh $METHOD $PRODUCER $CONSUMER $i"
		echo "COMD: "$cmd
		$cmd >> $MEASUREFILE.$i.out
		let c=c+1
	done
	c=0
done


# c=0
# METHOD="proxyCall_Obj"
# MEASUREFILE=$MEASUREDIR"/proxyCall_Obj"
# for ((i=$START;i<=$END;i+=$STEP)); do
# 	if [  $c == 0 ]; then
# 		echo "Size	$i" >> $MEASUREFILE.$i.out
# 		echo "ProduceTime	ConsumeTime	TotalTime" >> $MEASUREFILE.$i.out
# 	fi
# 	for ((j=0;j<=$LOOPS;j+=1)); do
# 		echo "-----------------------------"
# 		cmd="./bechMarkLegacy.sh $METHOD $PROXY_PRODUCER $PROXY_CONSUMER $i"
# 		echo "COMD: "$cmd
# 		$cmd >> $MEASUREFILE.$i.out
# 		let c=c+1
# 	done
# 	c=0
# done



# c=0
# METHOD="proxyCall_Ref"
# MEASUREFILE=$MEASUREDIR"/proxyCall_Ref_Obj"
# for ((i=$START;i<=$END;i+=$STEP)); do
# 	if [  $c == 0 ]; then
# 		echo "Size	$i" >> $MEASUREFILE.$i.out
# 		echo "ProduceTime	ConsumeTime	TotalTime" >> $MEASUREFILE.$i.out
# 	fi
# 	for ((j=0;j<=$LOOPS;j+=1)); do
# 		echo "-----------------------------"
# 		cmd="./bechMarkLegacy.sh $METHOD $PROXY_PRODUCER $PROXY_CONSUMER $i true"
# 		echo "COMD: "$cmd
# 		$cmd >> $MEASUREFILE.$i.out
# 		let c=c+1
# 	done
# 	c=0
# done


# c=0
# METHOD="proxyCall_Ref"
# MEASUREFILE=$MEASUREDIR"/proxyCall_Ref_File"
# for ((i=$START;i<=$END;i+=$STEP)); do
# 	if [  $c == 0 ]; then
# 		echo "Size	$i" >> $MEASUREFILE.$i.out
# 		echo "ProduceTime	ConsumeTime	TotalTime" >> $MEASUREFILE.$i.out
# 	fi
# 	for ((j=0;j<=$LOOPS;j+=1)); do
# 		echo "-----------------------------"
# 		cmd="./bechMarkLegacy.sh $METHOD $PROXY_PRODUCER $PROXY_CONSUMER $i flase"
# 		echo "COMD: "$cmd
# 		$cmd >> $MEASUREFILE.$i.out
# 		let c=c+1
# 	done
# 	c=0
# done



# c=0
# METHOD="stream"
# MEASUREFILE=$MEASUREDIR"/stream"
# for ((i=$START;i<=$END;i+=$STEP)); do
# 	if [  $c == 0 ]; then
# 		echo "Size	$i" >> $MEASUREFILE.$i.out
# 		echo "ProduceTime	ConsumeTime	TotalTime" >> $MEASUREFILE.$i.out
# 	fi
# 	for ((j=0;j<=$LOOPS;j+=1)); do
# 		echo "-----------------------------"
# 		cmd="./bechMarkLegacy.sh $METHOD $PRODUCER $CONSUMER $i"
# 		echo "COMD: "$cmd
# 		sleep 1
# 		$cmd >> $MEASUREFILE.$i.out
# 		let c=c+1
# 	done
# 	c=0
# done