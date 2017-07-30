package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class MacroHardServer {

	private ServerSocket ss;
	private MacroHardServerGUI sGUI;
	private Updater updater;
	
	public MacroHardServer() {
		sGUI = new MacroHardServerGUI(this);
		ss = null;
	}
	
	public boolean start() {
		Scanner s = null;
		try {
			s = new Scanner(new File("resources/files/Server.config"));
			s.next();
			try {
				int portNumber = Integer.parseInt(s.next());
				s.next();
				int updateInterval = Integer.parseInt(s.next());
				try {
					ss = new ServerSocket(portNumber);
					sGUI.appendToLog("Server started on port " + portNumber + "");
					ServerListener listener = new ServerListener(ss, sGUI, this);
					listener.start();
					updater = new Updater(updateInterval, listener);
					updater.start();
				} catch (IOException ioe) {
					sGUI.appendToLog("Invalid port number");
					return false;
				}
			} catch (Exception e) {
				sGUI.appendToLog("Invalid port format");
			}
		} catch (FileNotFoundException e) {
			sGUI.appendToLog("Server config file not found");
		} finally {
			if(s != null) s.close();
		}
		return true;
	}
	
	public void stop() {
		if(ss != null) {
			try {
				ss.close();
			} catch (IOException e) {
				System.out.println("Error closing server socket");
			}
		}
	}
	
	public Updater getUpdater() {
		return updater;
	}
	
	public static void main(String[] args) {
		new MacroHardServer();
	}
}
