#!/bin/bash

. ~/.bash_profile

SERVICE_PS=`ps auxxggww | grep "com.newsbubble.newsparser.service.NewsService" | grep -v grep | awk '{ print $2 }'`

if [ "$SERVICE_PS" == "" ]; then

	echo "Service was done. Bring up service again"

	NOW=`date +"%Y-%m-%d-%H-%M"`
	LOG_FILE=~/logs/service/$NOW.log

	echo "Running Service for `date`"

	CLASS=com.newsbubble.newsparser.service.NewsService

	nohup java -cp ~/prod/newsparser-prod.jar -Ddb.url=$DB_URL -Ddb.user=$DB_USER -Ddb.password=$DB_PASSWORD -Ddb.driver=$DB_DRIVER $CLASS server ~/prod/newsconfig.yml > $LOG_FILE &

	echo "Exiting serice `date`"

else
	echo "Service already running. PID is $SERVICE_PS"
fi