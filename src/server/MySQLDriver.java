package server;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.mysql.jdbc.Driver;

public class MySQLDriver {

	private Connection con;
	private final static String selectName = "SELECT * FROM USERS WHERE USERNAME=?";
	private final static String addUser = "INSERT INTO USERS(USERNAME, PASS) VALUES(?,?)";
	private final static String save = "INSERT INTO $username(FILENAME, FILECONTENTS) VALUES(?,?) ON DUPLICATE KEY UPDATE "
			+ "FILECONTENTS=VALUES(FILECONTENTS)";
	private final static String addCollab = "INSERT INTO collaborators(username, fileName, collabName) VALUES(?, ?, ?)";
	private final static String getCollabs = "SELECT collabName FROM collaborators WHERE username=? AND fileName=?";
	private final static String removeCollab = "DELETE FROM collaborators WHERE username=? AND fileName=? AND collabName=? LIMIT 1";
	private final static String getSharers = "SELECT username FROM collaborators WHERE collabName=?";
	private final static String getSharedFiles = "SELECT fileName FROM collaborators WHERE collabName=? AND username=?";
	
	public MySQLDriver() {
		try {
			new Driver();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void connect() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ajwheele_hw4?user=root&password=root");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		try {
			con.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean doesExist(String username) {
		try {
			PreparedStatement ps = con.prepareStatement(selectName);
			ps.setString(1, username);
			ResultSet result = ps.executeQuery();
			if(result.next()) {
				return true;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void add(String username, String password) {
		try {
			PreparedStatement ps = con.prepareStatement(addUser);
			ps.setString(1, username);
			ps.setString(2, password);
			ps.executeUpdate();
			ps = con.prepareStatement("CREATE TABLE " + username + "("
					+ "FILENAME VARCHAR(20) NOT NULL, "
					+ "FILECONTENTS VARCHAR(10000) NOT NULL, "
					+ "PRIMARY KEY (FILENAME) "
					+ ")");
			ps.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean authenticate(String username, String password) {
		try {
			PreparedStatement ps = con.prepareStatement(selectName);
			ps.setString(1, username);
			ResultSet result = ps.executeQuery();
			result.next();
			return result.getString(2).equals(password);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean save(String username, String fileName, String fileContents) {
		try {
			String thisSave = save.replace("$username", username);
			PreparedStatement ps = con.prepareStatement(thisSave);
			ps.setString(1, fileName);
			ps.setString(2, fileContents);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean addCollab(String username, String fileName, String collab) {
		try {
			if(doesExist(collab)) {
				if(getCollabs(username, fileName).contains(collab) || username.equals(collab)) {
					return false;
				}
				PreparedStatement ps = con.prepareStatement(addCollab);
				ps.setString(1, username);
				ps.setString(2, fileName);
				ps.setString(3, collab);
				ps.executeUpdate();
				return true;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Vector<String> getCollabs(String username, String fileName) {
		Vector<String> collaborators = new Vector<String>();
		try {
			PreparedStatement ps = con.prepareStatement(getCollabs);
			ps.setString(1, username);
			ps.setString(2, fileName);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				collaborators.addElement(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return collaborators;
	}
	
	public Vector<String> getSharers(String username) {
		Vector<String> collaborators = new Vector<String>();
		try {
			PreparedStatement ps = con.prepareStatement(getSharers);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				collaborators.addElement(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return collaborators;
	}
	
	public Vector<String> getSharedFiles(String username, String sharer) {
		Vector<String> sharedFiles = new Vector<String>();
		try {
			PreparedStatement ps = con.prepareStatement(getSharedFiles);
			ps.setString(1, username);
			ps.setString(2, sharer);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				sharedFiles.addElement(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sharedFiles;
	}
	
	public void removeCollab(String username, String collab, String fileName) {
		try {
			PreparedStatement ps = con.prepareStatement(removeCollab);
			ps.setString(1, username);
			ps.setString(2, fileName);
			ps.setString(3, collab);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Map<String, String> getFiles(String username) {
		Map<String, String> files = new HashMap<String, String>();
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM " + username);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				files.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return files;
	}
	
	public String getFileContents(String username, String fileName) {
		try {
			PreparedStatement ps = con.prepareStatement("SELECT FILECONTENTS FROM " + username + " WHERE FILENAME=?");
			ps.setString(1, fileName);
			ResultSet rs = ps.executeQuery();
			rs.next();
			return rs.getString(1);
		} catch (SQLException e) {
		}
		return null;
	}
	
	public boolean insertFile(String username, String fname, String contents) {
		try {
			PreparedStatement ps = con.prepareStatement("INSERT INTO " + username +
					"(FILENAME, FILECONTENTS) VALUES (" + fname + "," + contents + ")");
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
