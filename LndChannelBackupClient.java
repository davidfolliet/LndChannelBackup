/*
 * 	LND Channel Backups With Java
 *	Author: David Folliet
 *	Created: August 7, 2019
 *	Last Updated: August 8, 2019
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class LndChannelBackupClient {
	
	public LndChannelBackupClient() {				//Client to run on lnd node to send channel file over a temporary tcp/ip connection
		file = new File(path);
		fileSize = (int) file.length();
		openConnection();
		backupChannelFile();
		closeConnection();
	}
	
	public LndChannelBackupClient(int port, String host, String path) {
		this();
		this.port=port;
		this.host=host;
		this.path=path;
	}
	
	private void openConnection() {
		try {
			socket = new Socket(host, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        System.out.println("Socket connection has been created");
	}

	private void writeFileSize() {					//write the size of the channel backup file before sending 
		PrintWriter writer=null;
		try {
			writer = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		writer.println(fileSize);
	}
	
	private byte[] getFile(){						//read channel backup file from device storage
		byte[] bytes = new byte[fileSize];
		FileInputStream in = null;
		try {
			in = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			in.read(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	private void writeChannelBackupFile() {			//send channel backup file
		byte[] bytes = getFile();
		OutputStream out = null;
		try {
			out = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void backupChannelFile() {
		writeFileSize();
		writeChannelBackupFile();
	}
	
	private void closeConnection() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Exiting");
	}

	private static int port = 6063;
	private static String host = "192.168.1.79";
	private static String path = "/home/bitcoin/.lnd/data/chain/bitcoin/mainnet/channel.backup";
	private File file;
	private int fileSize;
	private Socket socket;
	
	public static void main(String args[]) {
		if(args.length == 0) {
			System.out.println("Usage: First Argument: port number, Second Argument: hostname or ip address of server, Third Argument: path to channel.backup");
			System.out.println("No command line arguments provided, using default parameters");
			System.out.println("Port: " + port);
			System.out.println("Host: " + host);
			System.out.println("Path to channel.backup: " + path);
			new LndChannelBackupClient();
		}else if (args.length == 3) {
			System.out.println("Using port: " + args[0] + " Host: " + host +" channel.backup path: " + args[2]);
			new LndChannelBackupClient(Integer.parseInt(args[0]), args[1], args[2]);
		}else 
			System.out.println("Error, expected either zero or three command line arguments");
	}
	
}
