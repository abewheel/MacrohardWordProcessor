package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;

import resource.AccessComm;
import resource.UserComm;
import resource.AccessComm.AType;

public class ServerThread extends Thread {

	private ServerListener sL;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String username;
	
	public ServerThread(Socket s, ServerListener sL) {
		this.sL = sL;
		username = "";
		try {
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	public String getUsername() {
		return username;
	}
	
	public void kicked(String username, String collab, String filename, Iterator<ServerThread> itr) {
		try {
			oos.writeObject(new AccessComm(AType.KICKED, username, collab, filename));
			oos.flush();
		} catch (IOException e) {
			itr.remove();
		}
	}
	
	public void requestUpdate(Iterator<ServerThread> itr) {
		try {
			oos.writeObject(new AccessComm(AType.REQUEST));
			oos.flush();
		} catch(IOException e) {
			itr.remove();
		}
	}
	
	public void updateDoc(String fileName, String owner, String contents) {
		try {
			oos.writeObject(new AccessComm(AType.UPDATE, fileName, owner, contents));
			oos.flush();
		} catch(IOException e) {
			System.out.println("Update doc server sending failed");
		}
	}
	
	public void run() {
		try {
			while(true) {
				UserComm c = (UserComm) (ois.readObject());
				switch(c.getType())  {
				case SIGNUP:
					if(sL.register(c)) {
						username = c.getUsername();
						oos.writeObject(new AccessComm(AType.GRANTED));
					}
					else oos.writeObject(new AccessComm(AType.DENIED));
					oos.flush();
					break;
				case LOGIN:
					if(sL.authenticate(c)) {
						username = c.getUsername();
						oos.writeObject(new AccessComm(AType.GRANTED));
					}
					else oos.writeObject(new AccessComm(AType.DENIED));
					oos.flush();
					break;
				case FILES:
					oos.writeObject(new AccessComm(AType.FILES, sL.getFiles(username)));
					oos.flush();
					break;
				case FILESELECTED:
					oos.writeObject(new AccessComm(AType.FILECONTENTS, sL.getFileContents(c.getUsername(), c.getFile())));
					oos.flush();
					break;
				case SAVE:
					if(sL.save(c)) {
						oos.writeObject(new AccessComm(AType.SUCCESS));
					}
					else {
						oos.writeObject(new AccessComm(AType.FAILURE));
					}
					oos.flush();
					break;
				case ADDCOLLAB:
					if(sL.addCollab(c)) oos.writeObject(new AccessComm(AType.COLLABSTATUS, "Y"));
					else oos.writeObject(new AccessComm(AType.COLLABSTATUS, "N"));
					oos.flush();
					break;
				case GETCOLLABS:
					oos.writeObject(new AccessComm(AType.COLLABS, sL.getCollabs(c.getUsername(), c.getPassword())));
					oos.flush();
					break;
				case REMOVECOLLAB:
					sL.removeCollab(c.getUsername(), c.getFileName(), c.getFileContent());
					break;
				case GETSHARERS:
					oos.writeObject(new AccessComm(AType.SHARERS, sL.getSharers(c.getUsername())));
					oos.flush();
					break;
				case SHAREDFILES:
					oos.writeObject(new AccessComm(AType.SHAREDFILES, sL.getSharedFiles(c.getUsername(), c.getPassword())));
					oos.flush();
					break;
				case UPDATE:
					sL.giveUpdate(c.getUsername(), c.getInfo());
					break;
				default:
					System.out.println("Unknown client request");
					break;
				}
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe in run: " + cnfe.getMessage());
		} catch (IOException ioe) {
			
		}
	}
}
