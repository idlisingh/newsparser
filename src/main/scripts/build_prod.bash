#!/bin/bash

# Checks to see if a comment is passed in to the script
check_params()
{
	if [ "$1" == "" ]; then
		echo "Please provide a message for the release"
		echo "Usage: $0 <Release message>"
		exit 1
	fi

	cd ~/
}

# Initializes variables and files
init()
{
	MESSAGE_FILE=/tmp/message_$$.log
	GIT_LOG_FILE=/tmp/git_log_$$.log
	COMMIT_FILE=latest_commit.log

	RELEASE_MESSAGE=$@

	echo -e "Release Message: $RELEASE_MESSAGE \n" >> $MESSAGE_FILE
	cd ~/
}

# Checkout lastest from repository and builds the code
build_code()
{
	echo "Removing older code"
	rm -rf newsparser

	echo "Cloning repository"
	git clone https://github.com/idlisingh/newsparser

	echo "Install code"
	cd newsparser
	mvn clean install

	cd ~/
}

# Does the following
# 1. Makes a new directory under ~/prod with the current date and time
# 2. Creates a file with the latest commit hash in $COMMIT_FILE (this is used in migration_details routine
# 3. Creates a file with all the messages.
#       This contains the reason for the migration and all the commit details since the last migrate
# 4. Copies the jars and config files
# 5. Updates the links
migrate()
{
	cd ~/prod

	echo "Moving artifact to deployable area" cd ~/prod
	NOW=`date +"%Y-%m-%d-%H-%M"`
	rm -rf $NOW
	mkdir $NOW
	cd $NOW

	echo $LASTEST_COMMIT >> $COMMIT_FILE
	cat $MESSAGE_FILE >> release_details.txt

	cp ~/newsparser/target/news-parser-*-jar-with-dependencies.jar .
	cp ~/newsparser/newsconfig.yml .
	mv news-parser-*-jar-with-dependencies.jar news-parser.jar
	cd ~/prod

	echo "Re-linking deployables"
	rm newsparser-prod.jar
	rm newsconfig.yml
	ln -s $NOW/news-parser.jar newsparser-prod.jar
	ln -s $NOW/newsconfig.yml  newsconfig.yml

	cd ~/
}

# Does the following
# 1. Determines the prior release folder
# 2. From the prior release folder gets the last commit that was done in that release
# 3. Gets all the git log messages since that last commit
# 4. Gets the latest check-in hash
migration_details()
{
	cd ~/prod
	echo "All commits since the last release" >> $MESSAGE_FILE
	echo -e "\n****************************************\n" >> $MESSAGE_FILE

	PRIOR_RELEASE=`ls -tlhr | grep -v news | sed 's/.*2016/2016/g' | sort | grep -v total  | tail -1`
	export PRIOR_COMMIT=`cat ~/prod/$PRIOR_RELEASE/$COMMIT_FILE | head -1`
	echo "Prior commit: $PRIOR_COMMIT from $PRIOR_RELEASE"

	cd ~/newsparser
	git log > $GIT_LOG_FILE
	cat $GIT_LOG_FILE | head -`grep -n $PRIOR_COMMIT $GIT_LOG_FILE | sed 's/:.*//g'` | head -n -1 >> $MESSAGE_FILE
	export LASTEST_COMMIT=`cat $GIT_LOG_FILE | head -1 | awk '{ print $2 }'`

	cd ~/
}

# Refreshes all the scripts. This is to make sure nothing gets stale
update_scripts()
{
	echo "Copying run scripts"
	cp newsparser/src/main/scripts/run_analyser.bash .
	cp newsparser/src/main/scripts/run_parser.bash .
	cp newsparser/src/main/scripts/run_service.bash .
	cp newsparser/src/main/scripts/build_prod.bash .

	cd ~/
}

check_params $@
init $@
build_code
migration_details
migrate
update_scripts

echo "Done"