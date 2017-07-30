package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Vector;

import resource.AccessComm;
import resource.UserComm;
import resource.UserComm.UType;

public class Client extends Thread {

	private Socket socket;
	private UserComm iD;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private MainPanel mP;
	private Map<String, String> files;
	private String username, fileContents;
	private String success, collabSuccess;
	private Vector<String> users, sharers, sharedFiles;
	private MacroHardClient mHO;
	
	public Client(MacroHardClient mHO, UType t, String host, int port, String username, String password, MainPanel mP) {
		this.mP = mP;
		this.mHO = mHO;
		files = null;
		fileContents = null;
		success = null;
		collabSuccess = null;
		users = null;
		sharers = null;
		sharedFiles = null;
		try {
			this.username = username;
			socket = new Socket(host, port);
			iD = new UserComm(t, username, password);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			if(t == UType.LOGIN) mP.connectionLost("Log-in Failed");
			else mP.connectionLost("Sign-up Failed");
		}
		Saver s = new Saver(this);
		s.start();
	}
	
	public void request(UserComm uC) {
		try {
			oos.writeObject(uC);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			oos.writeObject(iD);
			oos.flush();
			while(true) {
				Object o = ois.readObject();
				if(o != null) {
					AccessComm c = null;
					try {
						c = (AccessComm) (o);
					} catch(ClassCastException e) {
						System.out.println("Unrecognized message. Ignoring.");
					}
					if(c != null) {
						switch(c.getType()) {
							case GRANTED:
								mP.loginSuccess();
								break;
							case DENIED:
								mP.loginFailed();
								break;
							case FILES:
								files = c.getFiles();
								break;
							case FILECONTENTS:
								fileContents = c.getFileContents();
								break;
							case SUCCESS:
								success = "Y";
								break;
							case FAILURE:
								success = "N";
								break;
							case COLLABSTATUS:
								collabSuccess = c.getFileContents();
								break;
							case COLLABS:
								users = c.getCollabs();
								break;
							case KICKED:
								mHO.kicked(c.getUsername(), c.getCollab(), c.getFileName());
								break;
							case SHARERS:
								sharers = c.getCollabs();
								break;
							case SHAREDFILES:
								sharedFiles = c.getCollabs();
								break;
							case REQUEST:
								oos.writeObject(new UserComm(UType.UPDATE, mHO.getUsername(), mHO.getUpdate()));
								oos.flush();
								break;
							case UPDATE:
								mHO.updateDoc(c.getUsername(), c.getCollab(), c.getFileName());
								break;
							default:
								System.out.println("Unknown server request");
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("cnfe in client");
		}
	}
	
	public String getUsername() {
		return username;
	}
	
	public Map<String, String> getFiles() {
		return files;
	}
	
	public Vector<String> getSharedFiles() {
		return sharedFiles;
	}
	
	public String getFileContents() {
		return fileContents;
	}
	
	public Vector<String> getUsers() {
		return users;
	}
	
	public Vector<String> getSharers() {
		return sharers;
	}
	
	public void reset() {
		fileContents = null;
		collabSuccess = null;
		files = null;
		users = null;
		sharers = null;
		sharedFiles = null;
	}
	
	public String wasSuccess() {
		return success;
	}
	
	public String getCollabSuccess() {
		return collabSuccess;
	}
	
	public void saveFile(String username, String fileName, String contents) {
		try {
			oos.writeObject(new UserComm(UType.SAVE, username, fileName, contents));
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		mHO.save();
	}
	
	public void selectUser(String user) {
		mHO.selectUser(user);
	}
	
	public void myFiles() {
		mHO.myFiles();
	}
}
