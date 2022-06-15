@echo off
if [%1]==[] (echo enter -run, -stop, -test or -reset as argument)
if %1%==-run (docker-compose up -d)
if %1%==-stop (docker-compose down)
if %1%==-test (docker-compose -f docker-compose.yml -f runTests.yml up -d)
if %1%==-reset (docker-compose down --rmi all)
