package server;

import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import server.diff_match_patch.Patch;

public class Updater extends Thread {

	private int updateInterval;
	private ServerListener sL;
	private Map<MHFile, Vector<String>> allFiles;
	private Map<MHFile, String> last;
	private Map<String, Vector<MHFile>> clientFiles;
	private diff_match_patch dmp;
	private Lock lock;
	
	public Updater(int updateInterval, ServerListener sL) {
		this.updateInterval = updateInterval;
		this.sL = sL;
		allFiles = new ConcurrentHashMap<MHFile, Vector<String>>();
		last = new ConcurrentHashMap<MHFile, String>();
		clientFiles = new ConcurrentHashMap<String, Vector<MHFile>>();
		this.sL.setUpdater(this);
		dmp = new diff_match_patch();
		lock = new ReentrantLock();
	}
	
	public void giveUpdate(String username, String fileName, String owner, String contents) {
		MHFile key = new MHFile(fileName, owner);
		if(allFiles.containsKey(key)) {
			allFiles.get(key).add(contents);
		} else {
			Vector<String> allContents = new Vector<String>();
			allContents.add(contents);
			allFiles.put(key, allContents);
		}
		if(clientFiles.containsKey(username)) {
			clientFiles.get(username).add(key);
		} else {
			Vector<MHFile> files = new Vector<MHFile>();
			files.add(key);
			clientFiles.put(username, files);
		}
	}
	
	public void update() {
		lock.lock();
		Map<MHFile, Vector<String>> usersWithFileOpen = new ConcurrentHashMap<MHFile, Vector<String>>();
		for(String user : clientFiles.keySet()) {
			for(MHFile file : clientFiles.get(user)) {
				if(usersWithFileOpen.containsKey(file)) {
					usersWithFileOpen.get(file).add(user);
				} else {
					Vector<String> users = new Vector<String>();
					users.add(user);
					usersWithFileOpen.put(file, users);
				}
			}
		}
		for(MHFile file : allFiles.keySet()) {
			String lastContents;
			if(last.containsKey(file)) {
				lastContents = last.get(file);
			} else {
				lastContents = sL.getFileContents(file.getOwner(), file.getFileName());
				if(lastContents == null) continue;
			}
			String merge = lastContents;
			for(String fileContents : allFiles.get(file)) {
				LinkedList<Patch> patch = dmp.patch_make(lastContents, fileContents);
				merge = (String)(dmp.patch_apply(patch, merge)[0]);
			}
			last.put(file, merge);
			for(String user : usersWithFileOpen.get(file)) {
				sL.sendFileContents(user, file.getFileName(), file.getOwner(), merge);
			}
		}
		allFiles.clear();
		clientFiles.clear();
		lock.unlock();
	}
	
	public void run() {
		while(true) {
			try {
				Thread.sleep(updateInterval);
				sL.requestUpdate();
			} catch(InterruptedException ie) {
				System.out.println("Updater Interrupted");
			}
		}
	}
	
	private class MHFile {
		
		private String fileName;
		private String owner;
		
		public MHFile(String fileName, String owner) {
			this.fileName = fileName;
			this.owner = owner;
		}
		
		@Override
		public boolean equals(Object other) {
			if(other instanceof MHFile) {
				if(((MHFile) other).getFileName().equals(fileName) && ((MHFile) other).getOwner().equals(owner))
					return true;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return fileName.hashCode();
		}
		
		public String getFileName() {
			return fileName;
		}
		
		public String getOwner() {
			return owner;
		}
	}
}
