#!/bin/bash

cd

echo "Removing older code"
rm -rf newsparser

echo "Cloning repository"
git clone https://github.com/idlisingh/newsparser

echo "Install code"
cd newsparser
mvn clean install

echo "Moving artifact to deployable area"
cd ~/prod
NOW=`date +"%Y-%m-%d-%H-%M"`
rm -rf $NOW
mkdir $NOW
cd $NOW

cp ~/newsparser/target/news-parser-*-jar-with-dependencies.jar .
cp ~/newsparser/newsconfig.yml .
mv news-parser-*-jar-with-dependencies.jar news-parser.jar
cd ~/prod

echo "Re-linking deployables"
rm newsparser-prod.jar
rm newsconfig.yml
ln -s $NOW/news-parser.jar newsparser-prod.jar
ln -s $NOW/newsconfig.yml  newsconfig.yml
cd

echo "Copying run scripts"
cp newsparser/src/main/scripts/run_analyser.bash .
cp newsparser/src/main/scripts/run_parser.bash .
cp newsparser/src/main/scripts/run_service.bash .
cp newsparser/src/main/scripts/build_prod.bash .

echo "Done"