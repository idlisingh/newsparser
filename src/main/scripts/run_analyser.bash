#!/bin/bash

. ~/.bash_profile

NOW=`date +"%Y-%m-%d-%H-%M"`
LOG_FILE=~/logs/analyser/$NOW.log

echo "Running Analyser for `date`"

CLASS=com.newsbubble.newsparser.runners.RunAnalyser

java -cp ~/prod/newsparser-prod.jar  -Ddb.url=$DB_URL -Ddb.user=$DB_USER -Ddb.password=$DB_PASSWORD -Ddb.driver=$DB_DRIVER $CLASS > $LOG_FILE

echo "Done processing `date`"