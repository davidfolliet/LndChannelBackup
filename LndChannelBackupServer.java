/*
 * 	LND Channel Backups With Java
 *	Author: David Folliet
 *	Created: August 7, 2019
 *	Last Updated: August 8, 2019
 *
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class LndChannelBackupServer {		//Backup lnd channel file using a temporary tcp/ip socket connection
	public LndChannelBackupServer() {
		startShutdownListener();
		openSocket();
		listenForClient();
		closeAndExit();
	}
	
	public LndChannelBackupServer(int port, String backupFilePath) {		//2 parameter constructor for command line arguments
		this();
		this.port=port;
		this.backupFilePath=backupFilePath;
	}
	
	public void startShutdownListener() {
		new ShutdownListener(this).start();		//Listen for user keyboard output for shutdown command on another thread to avoid interference with network connectivity
	}
	
	private void openSocket() {
		try{
		    serverSocket = new ServerSocket(port);
		}catch(IOException e){
		    System.out.println("Error creating Server Socket");
		    e.printStackTrace();
		    System.exit(-1);
		}
		System.out.println("Server socket created");
	}
	
	private void listenForClient() {			//Wait for lnd node to connect
		System.out.println("Waiting for client...");
		try {
			socket = serverSocket.accept();
		} catch (IOException e) {
			if(!isClosing)		//program may be still executing and be closed by concurrent thread while waiting to accept client connection
				System.out.println("IO error occured opening Socket with client");
			e.printStackTrace();
		}
		System.out.println("Client connected");
		backupChannelFile();
	}
	
	private int getFileSize(InputStream in) {
		//read # of bytes in channel file from client
		InputStreamReader inReader = new InputStreamReader(in);
		BufferedReader reader = null;
		reader = new BufferedReader(inReader);
		String line = "";
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(line);
	}
	
	private byte[] readFile(InputStream in, int numBytes) {
		System.out.println("Reading channel file with " + numBytes + " bytes...");
		byte bytes[] = new byte[numBytes];
		int numCharsRead = 0;
		try {
			numCharsRead = in.read(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("read " + numCharsRead + " bytes from file");
		if(numBytes != numCharsRead){
			System.out.println("ERROR READING FILE");
			return null;
		}
		return bytes;
	}
	
	private void writeFile(byte[] bytes) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(backupFilePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			out.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void backupChannelFile() {    //receive and store channel backup file (user requires seed corresponding to backup file to recover, view readme for recovery information)
		InputStream in = null;
		try {
			in = socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int numBytes = getFileSize(in);
		byte[] bytes = readFile(in, numBytes);
		if(bytes != null)
			writeFile(bytes);
		listenForClient(); 				//wait for client to reconnect to backup file again
	}
	
	public void closeAndExit() {
		try {
			if(socket != null)
				socket.close();
		} catch (IOException e) {
			System.out.println("Error closing Socket");
			e.printStackTrace();
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.out.println("Error closing Server Socket");
			e.printStackTrace();
		}
		System.out.println("Exiting");
		System.exit(0);
	}
	
	
	private static int port = 6063;
	private static String backupFilePath = "C:/lndbak/channel.backup";
	private ServerSocket serverSocket;
	private Socket socket;
	public boolean isClosing;
	
	public static void main(String args[]) {
		System.out.println("Welcome");
		if(args.length == 0) {
			System.out.println("Usage: First Argument: port number, Second Argument: path to store backup file ");
			System.out.println("No command line arguments provided, using default parameters");
			System.out.println("Port: " + port);
			System.out.println("Backup Location: " + backupFilePath);
			new LndChannelBackupServer();
		}else if (args.length == 2) {
			System.out.println("Using port: " + args[0] + " backup filepath: " + args[1]);
			new LndChannelBackupServer(Integer.parseInt(args[0]), args[1]);
		}else 
			System.out.println("Error, expected either zero or two command line arguments");
	}
}
