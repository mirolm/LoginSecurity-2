package com.lenis0012.bukkit.ls.data;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lenis0012.bukkit.ls.encryption.EncryptionType;

public class SQLite implements DataManager {
	private final Logger log = Logger.getLogger("Minecraft.LoginSecurity");
	private File file;
	private Connection con;

        private PreparedStatement psSelectRegistered;
        private PreparedStatement psSelectLogin;
        private PreparedStatement psInsertLogin;
        private PreparedStatement psUpdatePassword;
        private PreparedStatement psUpdateIp;
        private PreparedStatement psDeleteLogin;
        private PreparedStatement psGetAllUsers;

	public SQLite(File file) {
		this.file = file;
		File dir = file.getParentFile();
		dir.mkdir();
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch(IOException e) {
				log.log(Level.SEVERE, "Failed to create file", e);
			}
		}

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, "Failed to load SQLite driver", e);
		}

		this.openConnection();
	}

	@Override
	public void openConnection() {
		Statement stCreateTable = null;

		try {
			this.con = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());
			
			stCreateTable = con.createStatement();
			stCreateTable.setQueryTimeout(30);
			stCreateTable.executeUpdate("CREATE TABLE IF NOT EXISTS users (unique_user_id VARCHAR(130) NOT NULL UNIQUE, password VARCHAR(300) NOT NULL, encryption INT, ip VARCHAR(130) NOT NULL);");

			// Prepare statements
			psSelectRegistered = con.prepareStatement("SELECT 1 FROM users WHERE unique_user_id = ?;");
			psSelectLogin = con.prepareStatement("SELECT ip, password, encryption FROM users WHERE unique_user_id = ?;");
			psInsertLogin = con.prepareStatement("INSERT INTO users(unique_user_id, password, encryption, ip) VALUES(?, ?, ?, ?);");
			psUpdatePassword = con.prepareStatement("UPDATE users SET password = ?, encryption = ? WHERE unique_user_id = ?;");
			psUpdateIp = con.prepareStatement("UPDATE users SET ip = ? WHERE unique_user_id = ?;");
			psDeleteLogin = con.prepareStatement("DELETE FROM users WHERE unique_user_id = ?;");
			psGetAllUsers = con.prepareStatement("SELECT unique_user_id, password FROM users;");
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to open SQLite connection", e);
		} finally {
			closeQuietly(stCreateTable);			
		}
	}

	@Override
	public void closeConnection() {
		// Release prepared
		closeQuietly(psSelectRegistered);
		closeQuietly(psSelectLogin);
		closeQuietly(psInsertLogin);
		closeQuietly(psUpdatePassword);
		closeQuietly(psUpdateIp);
		closeQuietly(psDeleteLogin);
		closeQuietly(psGetAllUsers);

		closeQuietly(con);
	}

	@Override
	public synchronized boolean isRegistered(String uuid) {
		ResultSet result = null;

		try {
			psSelectRegistered.setString(1, uuid.replaceAll("-", ""));
			result = psSelectRegistered.executeQuery();
			return result.next();
		} catch(SQLException e) {
			log.log(Level.SEVERE, "Failed to get data from SQLite db", e);
			return false;
		} finally {
			closeQuietly(result);
		}
	}

	@Override
	public synchronized void register(String uuid, String password, int encryption, String ip) {
		try {
			psInsertLogin.setString(1, uuid.replaceAll("-", ""));
			psInsertLogin.setString(2, password);
			psInsertLogin.setInt(3, encryption);
			psInsertLogin.setString(4, ip);
			psInsertLogin.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to create user", e);
		}
	}

	@Override
	public synchronized void updatePassword(String uuid, String password, int encryption) {
		try {
			psUpdatePassword.setString(1, password);
			psUpdatePassword.setInt(2, encryption);
			psUpdatePassword.setString(3, uuid.replaceAll("-", ""));
			psUpdatePassword.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to update user password", e);
		}
	}

	@Override
	public synchronized void updateIp(String uuid, String ip) {
		try {
			psUpdateIp.setString(1, ip);
			psUpdateIp.setString(2, uuid.replaceAll("-", ""));
			psUpdateIp.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to update user ip", e);
		}
	}

	@Override
	public synchronized String getPassword(String uuid) {
		ResultSet result = null;

		try {
			psSelectLogin.setString(1, uuid.replaceAll("-", ""));
			result = psSelectLogin.executeQuery();
			if(result.next())
				return result.getString("password");
			else
				return null;
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to get user password", e);
			return null;
		} finally {
			closeQuietly(result);
		}
	}

	@Override
	public synchronized int getEncryptionTypeId(String uuid) {
		ResultSet result = null;

		try {
			psSelectLogin.setString(1, uuid.replaceAll("-", ""));
			result = psSelectLogin.executeQuery();
			if(result.next())
				return result.getInt("encryption");
			else
				return EncryptionType.MD5.getTypeId();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to get user encryption type", e);
			return EncryptionType.MD5.getTypeId();
		} finally {
			closeQuietly(result);
		}
	}

	@Override
	public synchronized String getIp(String uuid) {
		ResultSet result = null;

		try {
			psSelectLogin.setString(1, uuid.replaceAll("-", ""));
			result = psSelectLogin.executeQuery();
			if(result.next())
				return result.getString("ip");
			else
				return null;
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to get user ip", e);
			return null;
		} finally {
			closeQuietly(result);
		}
	}

	@Override
	public synchronized void removeUser(String uuid) {
		try {
			psDeleteLogin.setString(1, uuid.replaceAll("-", ""));
			psDeleteLogin.executeUpdate();
		} catch(SQLException e) {
			log.log(Level.SEVERE, "Failed to remove user", e);
		}
	}

	@Override
	public ResultSet getAllUsers() {
		try {
			return psGetAllUsers.executeQuery();
		} catch (SQLException e) {
			return null;
		}
	}

	@Override
	public void closeQuietly(AutoCloseable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
				log.log(Level.SEVERE, "Failed to close connection", e);
			}
		}
	}
}
