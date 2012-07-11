#! /bin/bash

GRADLE=/home/ubuntu/gradle/bin/gradle

$GRADLE assemble

$GRADLE run

cd /home/ubuntu/leis

git add .

git commit -a -m "leis em `date +%Y-%m-%d`"


