package resource;

import java.io.Serializable;
import java.util.Map;

public class UserComm implements Serializable {
	
	public enum UType {
		LOGIN, SIGNUP, FILES, FILESELECTED, SAVE, ADDCOLLAB, GETCOLLABS, REMOVECOLLAB, GETSHARERS,
		SHAREDFILES, UPDATE
	}
	private static final long serialVersionUID = 1L;
	private String username, password, fileName, fileContents, file, collab;
	private Map<String[], String> info;
	private UType t;
	
	public UserComm(UType t, String username) {
		this.t = t;
		this.username = username;
	}
	
	public UserComm(UType t, String username, String passwordOrFile) {
		this.t = t;
		this.username = username;
		if(t == UType.FILESELECTED) this.file = passwordOrFile;
		else this.password = passwordOrFile;
	}
	
	public UserComm(UType t, String username, Map<String[], String> info) {
		this.t = t;
		this.username = username;
		this.info = info;
	}
	
	public UserComm(UType t, String username, String fileName, String fileContents) {
		this.t = t;
		if(t == UType.ADDCOLLAB) {
			this.username = username;
			this.fileName = fileName;
			collab = fileContents;
		} else {
			this.username = username;
			this.fileName = fileName;
			this.fileContents = fileContents;
		}
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getFile() {
		return file;
	}
	
	public String getCollab() {
		return collab;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getFileContent() {
		return fileContents;
	}
	
	public Map<String[], String> getInfo() {
		return info;
	}
	
	public UType getType() {
		return t;
	}
}
