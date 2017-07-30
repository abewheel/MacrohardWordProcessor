package resource;

import java.io.Serializable;
import java.util.Map;
import java.util.Vector;

public class AccessComm implements Serializable {
	
	public enum AType {
		GRANTED, DENIED, FILES, FILECONTENTS, SUCCESS, FAILURE, COLLABSTATUS, COLLABS, KICKED, SHARERS,
		SHAREDFILES, REQUEST, UPDATE
	}
	private static final long serialVersionUID = 1L;
	private AType t;
	private Map<String, String> files;
	private Vector<String> collabs;
	private String fileContents, username, filename, collab;
	
	public AccessComm(AType t) {
		this.t = t;
	}
	
	public AccessComm(AType t, Map<String, String> files) {
		this.t = t;
		this.files = files;
	}
	
	public AccessComm(AType t, Vector<String> collabs) {
		this.t = t;
		this.collabs = collabs;
	}
	
	public AccessComm(AType t, String fileContents) {
		this.t = t;
		this.fileContents = fileContents;
	}
	
	public AccessComm(AType t, String username, String filename) {
		this.t = t;
		this.username = username;
		this.filename = filename;
	}
	
	public AccessComm(AType t, String username, String collab, String filename) {
		this.t = t;
		this.username = username;
		this.collab = collab;
		this.filename = filename;
	}
	
	public AType getType() {
		return t;
	}
	
	public Vector<String> getCollabs() {
		return collabs;
	}
	
	public Map<String, String> getFiles() {
		return files;
	}
	
	public String getFileContents() {
		return fileContents;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getFileName() {
		return filename;
	}
	
	public String getCollab() {
		return collab;
	}
}
