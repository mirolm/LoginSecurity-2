package com.lenis0012.bukkit.ls.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;

import com.lenis0012.bukkit.ls.encryption.EncryptionType;

public class MySQL implements DataManager {
	private Logger log = Logger.getLogger("Minecraft.LoginSecruity");
	private FileConfiguration config;
	private Connection con;

        private PreparedStatement psSelectRegistered;
        private PreparedStatement psSelectLogin;
        private PreparedStatement psInsertLogin;
        private PreparedStatement psUpdatePassword;
        private PreparedStatement psUpdateIp;
        private PreparedStatement psDeleteLogin;
        private PreparedStatement psGetAllUsers;

	public MySQL(FileConfiguration config) {
		this.config = config;

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, "Failed to load MySQL driver", e);
		}

		this.openConnection();
	}

	@Override
	public void openConnection() {
		String host = config.getString("MySQL.host", "localhost");
		String port = String.valueOf(config.getInt("MySQL.port", 3306));
		String database = config.getString("MySQL.database", "bukkit");
		String user = config.getString("MySQL.username", "root");
		String pass = config.getString("MySQL.password", "");
		String table = config.getString("MySQL.prefix", "") + "users";
		Statement stCreateTable = null;

		try {
			this.con = DriverManager.getConnection("jdbc:mysql://" + host + ':' + port + '/' + database, user, pass);

			stCreateTable = con.createStatement();
			stCreateTable.setQueryTimeout(30);
			stCreateTable.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " (unique_user_id VARCHAR(130) NOT NULL UNIQUE, password VARCHAR(300) NOT NULL, encryption INT, ip VARCHAR(130) NOT NULL);");

			// Prepare statements
			psSelectRegistered = con.prepareStatement("SELECT 1 FROM " + table + " WHERE unique_user_id = ?;");
			psSelectLogin = con.prepareStatement("SELECT ip, password, encryption FROM " + table + " WHERE unique_user_id = ?;");
			psInsertLogin = con.prepareStatement("INSERT INTO " + table + "(unique_user_id, password, encryption,ip) VALUES(?, ?, ?, ?);");
			psUpdatePassword = con.prepareStatement("UPDATE " + table + " SET password = ?, encryption = ? WHERE unique_user_id = ?;");
			psUpdateIp = con.prepareStatement("UPDATE " + table + " SET ip = ? WHERE unique_user_id = ?;");
			psDeleteLogin = con.prepareStatement("DELETE FROM " + table + " WHERE unique_user_id = ?;");
			psGetAllUsers = con.prepareStatement("SELECT unique_user_id, password FROM " + table + ";");
		} catch(SQLException e) {
			log.log(Level.SEVERE, "Failed to load MySQL", e);
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
	public boolean isRegistered(String uuid) {
		ResultSet result = null;

		try {
			psSelectRegistered.setString(1, uuid.replaceAll("-", ""));
			result = psSelectRegistered.executeQuery();
			return result.next();
		} catch(SQLException e) {
			log.log(Level.SEVERE, "Failed to get data from MySQL db", e);
			return false;
		} finally {
			closeQuietly(result);
		}
	}

	@Override
	public void register(String uuid, String password, int encryption, String ip) {
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
	public void updatePassword(String uuid, String password, int encryption) {
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
	public void updateIp(String uuid, String ip) {
		try {
			psUpdateIp.setString(1, ip);
			psUpdateIp.setString(2, uuid.replaceAll("-", ""));
			psUpdateIp.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to update user ip", e);
		}
	}

	@Override
	public String getPassword(String uuid) {
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
	public int getEncryptionTypeId(String uuid) {
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
	public String getIp(String uuid) {
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
	public void removeUser(String uuid) {
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
