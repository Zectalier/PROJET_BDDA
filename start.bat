@ECHO OFF
javac -encoding ISO-8859-1 -d CODE/bin CODE/src/*
java CODE/bin/Main "../DB/"
PAUSE