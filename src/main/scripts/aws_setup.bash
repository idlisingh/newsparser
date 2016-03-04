#!/bin/bash


sudo yum install wget

# Courtesy: http://blog.de-gouveia.eu/2014/05/21/java-8-jdk-linux-installation-in-ec2-linux-instance/
wget -c --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u5-b13/jdk-8u5-linux-x64.rpm" --output-document="jdk-8u5-linux-x64.rpm"

sudo rpm -i jdk-8u5-linux-x64.rpm

sudo alternatives --install /usr/bin/java java /usr/java/default/bin/java 20000

export JAVA_HOME=/usr/java/default

rm jdk-8u5-linux-x64.rpm

# Courtesy: https://gist.github.com/sebsto/19b99f1fa1f32cae5d00
sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven
mvn --version

sudo yum intall git

git clone https://github.com/idlisingh/newsparser

cd newsparser

mvn clean install

cd

sudo yum install postgresql postgresql-contrib

psql --host=news.c9xx45aul7gn.us-east-1.rds.amazonaws.com --port=5432 --username=newsprocess --password --dbname=news -f newsparser/src/main/resources/tables/article_summary.table
psql --host=news.c9xx45aul7gn.us-east-1.rds.amazonaws.com --port=5432 --username=newsprocess --password --dbname=news -f newsparser/src/main/resources/tables/candidate_details.table
psql --host=news.c9xx45aul7gn.us-east-1.rds.amazonaws.com --port=5432 --username=newsprocess --password --dbname=news -f newsparser/src/main/resources/tables/candidate_summary.table
psql --host=news.c9xx45aul7gn.us-east-1.rds.amazonaws.com --port=5432 --username=newsprocess --password --dbname=news -f newsparser/src/main/resources/tables/last_run.table

