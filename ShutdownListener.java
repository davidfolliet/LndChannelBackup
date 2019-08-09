/*
 * 	LND Channel Backups With Java
 *	Author: David Folliet
 *	Created: August 7, 2019
 *	Last Updated: August 8, 2019
 *
 */

import java.util.Scanner;

//Listen for user to execute shutdown command from keyboard (CLI)
public class ShutdownListener extends Thread{
	public ShutdownListener(LndChannelBackupServer lndChannelBackupServer) {
		this.lndChannelBackupServer = lndChannelBackupServer;
		lndChannelBackupServer.isClosing=false;
	}
	public void run(){
		Scanner reader = new Scanner(System.in);
		System.out.println("Type shutdown to exit");
		String cmd = reader.nextLine();
		while(!cmd.equals("shutdown")){
		    System.out.println(cmd + " is not an availible command");
		    System.out.println("Type shutdown to exit");
		    cmd = reader.nextLine();
		}
		reader.close();
		lndChannelBackupServer.isClosing=true;
		lndChannelBackupServer.closeAndExit();
	    }
	
	LndChannelBackupServer lndChannelBackupServer;
}
