package server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import resource.UserComm;

public class ServerListener extends Thread {

	private ServerSocket ss;
	private Vector<ServerThread> serverThreads;
	private MacroHardServerGUI sGUI;
	private MySQLDriver mySQL;
	private int responses;
	private Updater updater;
	
	public ServerListener(ServerSocket ss, MacroHardServerGUI sGUI, MacroHardServer mHS) {
		this.sGUI = sGUI;
		this.ss = ss;
		serverThreads = new Vector<ServerThread>();
		mySQL = new MySQLDriver();
		responses = 0;
	}
	
	public boolean authenticate(UserComm c) {
		String user = c.getUsername();
		String pass = c.getPassword();
		sGUI.appendToLog("Log-in attempt User: " + user + " Pass: " + pass);
		mySQL.connect();
		if(mySQL.doesExist(user)) {
			if(mySQL.authenticate(user, pass)) {
				sGUI.appendToLog("Log-in success User: " + user);
				mySQL.stop();
				return true;
			} else {
				sGUI.appendToLog("Log-in failure User: " + user);
				mySQL.stop();
				return false;
			}
		}
		mySQL.stop();
		return false;
	}
	
	public boolean register(UserComm c) {
		String user = c.getUsername();
		String pass = c.getPassword();
		sGUI.appendToLog("Sign-up attempt User: " + user + " Pass: " + pass);
		mySQL.connect();
		if(!mySQL.doesExist(user)) {
			mySQL.add(user, pass);
			sGUI.appendToLog("Sign-up success User: " + user);
			mySQL.stop();
			return true;
		} else {
			sGUI.appendToLog("Sign-up failure User: " + user);			
		}
		mySQL.stop();
		return false;
	}
	
	public Map<String, String> getFiles(String username) {
		mySQL.connect();
		Map<String, String> files = mySQL.getFiles(username);
		mySQL.stop();
		return files;
	}
	
	public String getFileContents(String username, String fileName) {
		mySQL.connect();
		String fileContents = mySQL.getFileContents(username, fileName);
		sGUI.appendToLog("File downloaded User: " + username + " File: " + fileName);
		mySQL.stop();
		return fileContents;
	}
	
	public Vector<String> getCollabs(String username, String fileName) {
		mySQL.connect();
		Vector<String> collabs = mySQL.getCollabs(username, fileName);
		mySQL.stop();
		return collabs;
	}
	
	public Vector<String> getSharers(String username) {
		mySQL.connect();
		Vector<String> collabs = mySQL.getSharers(username);
		mySQL.stop();
		return collabs;
	}
	
	public Vector<String> getSharedFiles(String username, String sharer) {
		mySQL.connect();
		Vector<String> sharedFiles = mySQL.getSharedFiles(username, sharer);
		mySQL.stop();
		return sharedFiles;
	}
	
	public void removeCollab(String username, String collab, String fileName) {
		mySQL.connect();
		mySQL.removeCollab(username, collab, fileName);
		for(Iterator<ServerThread> itr = serverThreads.iterator(); itr.hasNext();) {
			ServerThread current = itr.next();
			if(current.getUsername().equals(collab)) {
				current.kicked(username, collab, fileName, itr);
			}
		}
		mySQL.stop();
	}
	
	public boolean save(UserComm c) {
		mySQL.connect();
		boolean success = mySQL.save(c.getUsername(), c.getFileName(), c.getFileContent());
		mySQL.stop();
		if(success) sGUI.appendToLog("File saved User: " + c.getUsername() + " File: " + c.getFileName());
		else sGUI.appendToLog("File save failed User: " + c.getUsername() + " File: " + c.getFileName());
		return success;
	}
	
	public boolean addCollab(UserComm c) {
		mySQL.connect();
		boolean success = mySQL.addCollab(c.getUsername(), c.getFileName(), c.getCollab());
		mySQL.stop();
		return success;
	}
	
	public void requestUpdate() {
		for(Iterator<ServerThread> itr = serverThreads.iterator(); itr.hasNext();) {
			ServerThread current = itr.next();
			current.requestUpdate(itr);
		}
	}
	
	public void giveUpdate(String username, Map<String[], String> info) {
		responses++;
		for(String[] fileName : info.keySet()) {
			updater.giveUpdate(username, fileName[0], fileName[1], info.get(fileName));
		}
		if(responses == serverThreads.size()) {
			updater.update();
			responses = 0;
		}
	}
	
	public void setUpdater(Updater u) {
		updater = u;
	}
	
	public void sendFileContents(String destination, String fileName, String owner, String contents) {
		for(ServerThread sT : serverThreads) {
			if(sT.getUsername() == destination) {
				sT.updateDoc(fileName, owner, contents);
			}
		}
	}
	
	public void run() {
		try {
			while(true) {
				Socket s = ss.accept();
				ServerThread serverThread = new ServerThread(s, this);
				serverThreads.add(serverThread);
			}
		} catch(BindException be) {
			System.out.println("Bind Exception in Server Listener");
		} catch (IOException ioe) {
			sGUI.appendToLog("Server stopped");
		}
	}
}
