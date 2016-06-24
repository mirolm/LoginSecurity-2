package com.lenis0012.bukkit.ls.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lenis0012.bukkit.ls.encryption.EncryptionType;

public class SQLite implements DataManager {
	private final Logger log = Logger.getLogger("Minecraft.LoginSecurity");
	private Connection con;
	private final String jdbcUrl;	

	private final String CREATE_TABLE;
        private final String SELECT_REGISTERED;
        private final String SELECT_LOGIN;
        private final String INSERT_LOGIN;
        private final String UPDATE_PASSWORD;
        private final String UPDATE_IP;
        private final String DELETE_LOGIN;
        private final String GET_USERS;

	public SQLite(File file) {
		String table = "users";

		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + table + " (unique_user_id VARCHAR(130) NOT NULL UNIQUE, password VARCHAR(300) NOT NULL, encryption INT, ip VARCHAR(130) NOT NULL);";
		SELECT_REGISTERED = "SELECT 1 FROM " + table + " WHERE unique_user_id = ?;";
		SELECT_LOGIN = "SELECT ip, password, encryption FROM " + table + " WHERE unique_user_id = ?;";
		INSERT_LOGIN = "INSERT INTO " + table + "(unique_user_id, password, encryption,ip) VALUES(?, ?, ?, ?);";
		UPDATE_PASSWORD = "UPDATE " + table + " SET password = ?, encryption = ? WHERE unique_user_id = ?;";
		UPDATE_IP = "UPDATE " + table + " SET ip = ? WHERE unique_user_id = ?;";
		DELETE_LOGIN = "DELETE FROM " + table + " WHERE unique_user_id = ?;";
		GET_USERS = "SELECT unique_user_id, password FROM " + table + ";";

		jdbcUrl = "jdbc:sqlite://" + file.getAbsolutePath();

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, "Failed to load SQLite driver", e);
		}

		this.openConnection();
	}

	@Override
	public void openConnection() {
		PreparedStatement stmt = null;

		try {
			this.con = DriverManager.getConnection(jdbcUrl);
			
			stmt = con.prepareStatement(CREATE_TABLE);
			stmt.setQueryTimeout(30);
			stmt.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to open SQLite connection", e);
		} finally {
			closeQuietly(stmt);			
		}
	}

	@Override
	public void closeConnection() {
		closeQuietly(con);
	}

	@Override
	public boolean isRegistered(String uuid) {
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {
			stmt = con.prepareStatement(SELECT_REGISTERED);
			stmt.setString(1, uuid.replaceAll("-", ""));
			result = stmt.executeQuery();
			return result.next();
		} catch(SQLException e) {
			log.log(Level.SEVERE, "Failed to get data from SQLite db", e);
			return false;
		} finally {
			closeQuietly(result);
			closeQuietly(stmt);
		}
	}

	@Override
	public void register(String uuid, String password, int encryption, String ip) {
		PreparedStatement stmt = null;

		try {
			stmt = con.prepareStatement(INSERT_LOGIN);
			stmt.setString(1, uuid.replaceAll("-", ""));
			stmt.setString(2, password);
			stmt.setInt(3, encryption);
			stmt.setString(4, ip);
			stmt.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to create user", e);
		} finally {
			closeQuietly(stmt);			
		}
	}

	@Override
	public void updatePassword(String uuid, String password, int encryption) {
		PreparedStatement stmt = null;

		try {
			stmt = con.prepareStatement(UPDATE_PASSWORD);
			stmt.setString(1, password);
			stmt.setInt(2, encryption);
			stmt.setString(3, uuid.replaceAll("-", ""));
			stmt.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to update user password", e);
		} finally {
			closeQuietly(stmt);			
		}
	}

	@Override
	public void updateIp(String uuid, String ip) {
		PreparedStatement stmt = null;

		try {
			stmt = con.prepareStatement(UPDATE_IP);
			stmt.setString(1, ip);
			stmt.setString(2, uuid.replaceAll("-", ""));
			stmt.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to update user ip", e);
		} finally {
			closeQuietly(stmt);			
		}
	}

	@Override
	public String getPassword(String uuid) {
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {
			stmt = con.prepareStatement(SELECT_LOGIN);
			stmt.setString(1, uuid.replaceAll("-", ""));
			result = stmt.executeQuery();
			if(result.next())
				return result.getString("password");
			else
				return null;
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to get user password", e);
			return null;
		} finally {
			closeQuietly(result);
			closeQuietly(stmt);
		}
	}

	@Override
	public int getEncryptionTypeId(String uuid) {
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {
			stmt = con.prepareStatement(SELECT_LOGIN);
			stmt.setString(1, uuid.replaceAll("-", ""));
			result = stmt.executeQuery();
			if(result.next())
				return result.getInt("encryption");
			else
				return EncryptionType.MD5.getTypeId();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to get user encryption type", e);
			return EncryptionType.MD5.getTypeId();
		} finally {
			closeQuietly(result);
			closeQuietly(stmt);
		}
	}

	@Override
	public String getIp(String uuid) {
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {
			stmt = con.prepareStatement(SELECT_LOGIN);
			stmt.setString(1, uuid.replaceAll("-", ""));
			result = stmt.executeQuery();
			if(result.next())
				return result.getString("ip");
			else
				return null;
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to get user ip", e);
			return null;
		} finally {
			closeQuietly(result);
			closeQuietly(stmt);
		}
	}

	@Override
	public void removeUser(String uuid) {
		PreparedStatement stmt = null;

		try {
			stmt = con.prepareStatement(DELETE_LOGIN);
			stmt.setString(1, uuid.replaceAll("-", ""));
			stmt.executeUpdate();
		} catch(SQLException e) {
			log.log(Level.SEVERE, "Failed to remove user", e);
		} finally {
			closeQuietly(stmt);			
		}
	}

	@Override
	public ResultSet getAllUsers() {
		try {
			stmt = con.prepareStatement(GET_USERS);
			return stmt.executeQuery();
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
