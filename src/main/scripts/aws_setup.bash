#!/bin/bash


sudo yum install wget

# Courtesy: https://gist.github.com/sebsto/19b99f1fa1f32cae5d00
sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven
mvn --version

# Above command installs java 1.8. The following is used to remvoe 1.8 and make sure everything runs in 1.7
sudo rpm -e java-1.8.0-openjdk-1.8.0.71-2.b15.el7_2.x86_64
sudo rpm -e java-1.8.0-openjdk-headless-1.8.0.71-2.b15.el7_2.x86_64

sudo yum intall git

mkdir -p ~/logs/analyser
mkdir -p ~/logs/parser
mkdir -p ~/logs/service
mkdir -p ~/prod

# Cron entries
*/5 * * * * /home/ec2-user/run_parser.bash; /home/ec2-user/run_analyser.bash
*/1 * * * * /home/ec2-user/run_service.bash >> ~/logs/service/service_cron.log

# Loading tables for the first time
sudo yum install postgresql postgresql-contrib

psql --host=news.c9xx45aul7gn.us-east-1.rds.amazonaws.com --port=5432 --username=newsprocess --password --dbname=news -f newsparser/src/main/resources/tables/article_summary.table
psql --host=news.c9xx45aul7gn.us-east-1.rds.amazonaws.com --port=5432 --username=newsprocess --password --dbname=news -f newsparser/src/main/resources/tables/candidate_details.table
psql --host=news.c9xx45aul7gn.us-east-1.rds.amazonaws.com --port=5432 --username=newsprocess --password --dbname=news -f newsparser/src/main/resources/tables/candidate_summary.table
psql --host=news.c9xx45aul7gn.us-east-1.rds.amazonaws.com --port=5432 --username=newsprocess --password --dbname=news -f newsparser/src/main/resources/tables/last_run.table
