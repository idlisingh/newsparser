#!/bin/bash

. ~/.bash_profile

NOW=`date +"%Y-%m-%d-%H-%M"`
LOG_FILE=~/logs/analyser/$NOW.log

CLASS=com.newsbubble.newsparser.runners.RunAnalyser

echo "Running Analyser for `date`"

java -cp newsparser/target/news-parser-1.0-SNAPSHOT-jar-with-dependencies.jar -Ddb.url=$DB_URL -Ddb.user=$DB_USER -Ddb.password=$DB_PASSWORD -Ddb.driver=$DB_DRIVER $CLASS > $LOG_FILE

echo "Done processing `date`"