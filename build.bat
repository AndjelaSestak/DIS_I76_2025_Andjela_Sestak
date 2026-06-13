@echo off
echo Building Hotel Reservation Microservices...
call mvn clean install -DskipTests -Djavax.net.ssl.trustStoreType=WINDOWS-ROOT
echo Build completed!
pause
