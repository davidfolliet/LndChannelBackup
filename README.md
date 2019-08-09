# LndChannelBackup
Lnd Channel Backups With Java
Author: David Folliet
Created: August 7, 2019
Last Updated: August 8, 2019

Purpose: 
Keep a copy of channel.backup file on your lnd node using a tcp/ip connection

Assumptions:
-You are farmiliar with using linux and the command line and have java runtime envoironment on the machines runing the client and the server
-You are running the commands to start the programs on the included executable jar files
Note: If you are having issues installing default-jre on a raspberry pi try running sudo apt-get install oracle-java8-jdk instead

Description: 
Server Program: Keeps a backup of your channel.backup file ex) running on your general use PC

Client Program: Runs on machine with LND node ex) your raspberry pi to send the channel.backup file

Protocal:
Server Program: Should be running at all times waiting for the client to connect.  When a client connects the server expects to read the size of the channel.backup file in bytes followed by the bytes in the channel.backup file.  After reading the file the server starts to listen for another client

Client Program: Connects to the server when the command is issued by a user running the LND node (or by the operating system as scheduled by the user); client sends the channel backup file size followed by the file then disconects and is free to reconnect


Usage:

Server Program:
To start the server program use windows or linux command line to navigate to the destination directory of the server jar file.
Run the command: java -jar LndChannelBackupServer.jar [port number] [destination of bakup file]
Note: if you run the command with no command line arguments i.e. LndChannelBackupServer.jar
It will run with the defaults being equivalent of java -jar LndChannelBackupServer.jar 6063 C:/lndbak/channel.backup
Note: You must start the server program before the client to connect to it and leave it running if you want to do automatic backups.
Note: Client program need to know the ip address of the machine running the server, if you are connecting over a local area connection suggest you assign your computer a static local ip address

Client Program:
To start the client program (after starting the server program) switch to the user that owns the channel.backup file.
Run the command: java -jar [path to the client jar file]/LndChannelBackupClient.jar [port number] [server ip or hostname] [absolute path to channel.backup]
Ex) java -jar /home/admin/tools/LndChannelBackupClient.jar 6063 192.168.1.79 /home/bitcoin/.lnd/data/chain/bitcoin/mainnet/channel.backup
Note: The port number must match the port number for the server and you must use the servers ip address or hostname
Note: This command can be scheudled to run automatically every so often ex) once per hour
Note: It may be useful to run the command to backup the channel file after preforming actions as a user on the lightning network
Note: the backup file is useless to an interceptor without having the coresponding seed


Setting up automatic backups:
To update automatically setup a cron job that will run the client program on a regular basis.  You need to setup the cron job on a user that owns the channel.backup file
ex)
sudo su bitcoin
crontab -e (select your favorite installed text editor)
add a line (followed by an empty line) at the end of the file (replace host, etc)

ex) 
@hourly java -jar /home/admin/tools/LndChannelBackupClient.jar 6063 192.168.1.79 /home/bitcoin/.lnd/data/chain/bitcoin/mainnet/channel.backup

Save and exit the text editor

Donations: (BTC)
3HwDSHSmMYUZmuEVDwUKoCsovPJfKCSAvQ
