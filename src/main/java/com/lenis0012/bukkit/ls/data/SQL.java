package com.lenis0012.bukkit.ls.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SQL implements DataManager {
	private final Logger log = Logger.getLogger("Minecraft.LoginSecruity");
	private Connection con = null;

	private String JDBC_URL;
	private String CREATE_TABLE;
        private String SELECT_REGISTERED;
        private String SELECT_LOGIN;
        private String INSERT_LOGIN;
        private String UPDATE_PASSWORD;
        private String UPDATE_IP;
        private String DELETE_LOGIN;
        private String GET_USERS;
        private String PING_CONN;

	public SQL(String driver) {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, "Failed to load SQL driver", e);
		}
	}

	public void initConnection(String table, String url) {
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + table + " (unique_user_id VARCHAR(130) NOT NULL UNIQUE, password VARCHAR(300) NOT NULL, encryption INT, ip VARCHAR(130) NOT NULL);";
		SELECT_REGISTERED = "SELECT 1 FROM " + table + " WHERE unique_user_id = ?;";
		SELECT_LOGIN = "SELECT unique_user_id, password, encryption, ip FROM " + table + " WHERE unique_user_id = ?;";
		INSERT_LOGIN = "INSERT INTO " + table + "(unique_user_id, password, encryption, ip) VALUES(?, ?, ?, ?);";
		UPDATE_PASSWORD = "UPDATE " + table + " SET password = ?, encryption = ? WHERE unique_user_id = ?;";
		UPDATE_IP = "UPDATE " + table + " SET ip = ? WHERE unique_user_id = ?;";
		DELETE_LOGIN = "DELETE FROM " + table + " WHERE unique_user_id = ?;";
		GET_USERS = "SELECT unique_user_id, password, encryption, ip FROM " + table + ";";
		PING_CONN = "SELECT 1;";

		JDBC_URL = url;

		openConnection();
	}

	@Override
	public void openConnection() {
		PreparedStatement stmt = null;

		try {
			closeConnection();

			con = DriverManager.getConnection(JDBC_URL);

			stmt = con.prepareStatement(CREATE_TABLE);
			stmt.setQueryTimeout(30);
			stmt.executeUpdate();
		} catch(SQLException e) {
			log.log(Level.SEVERE, "Failed to open connection", e);
		} finally {
			closeQuietly(stmt);
		}
	}

	@Override
	public void closeConnection() {
		closeQuietly(con);
	}

	@Override
	public boolean pingConnection() {
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {
			stmt = con.prepareStatement(PING_CONN);
			result = stmt.executeQuery();
			return result.next();
		} catch(SQLException e) {
			return false;
		} finally {
			closeQuietly(result);
			closeQuietly(stmt);
		}
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
			log.log(Level.SEVERE, "Failed to check user exists", e);
			return false;
		} finally {
			closeQuietly(result);
			closeQuietly(stmt);
		}
	}

	@Override
	public void register(LoginData login) {
		PreparedStatement stmt = null;

		try {
			stmt = con.prepareStatement(INSERT_LOGIN);
			stmt.setString(1, login.uuid.replaceAll("-", ""));
			stmt.setString(2, login.password);
			stmt.setInt(3, login.encryption);
			stmt.setString(4, login.ipaddr);
			stmt.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to create user", e);
		} finally {
			closeQuietly(stmt);
		}
	}

	@Override
	public void updatePassword(LoginData login) {
		PreparedStatement stmt = null;

		try {
			stmt = con.prepareStatement(UPDATE_PASSWORD);
			stmt.setString(1, login.password);
			stmt.setInt(2, login.encryption);
			stmt.setString(3, login.uuid.replaceAll("-", ""));
			stmt.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to update user password", e);
		} finally {
			closeQuietly(stmt);
		}
	}

	@Override
	public void updateIp(LoginData login) {
		PreparedStatement stmt = null;

		try {
			stmt = con.prepareStatement(UPDATE_IP);
			stmt.setString(1, login.ipaddr);
			stmt.setString(2, login.uuid.replaceAll("-", ""));
			stmt.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to update user ipaddr", e);
		} finally {
			closeQuietly(stmt);
		}
	}

	@Override
	public LoginData getData(String uuid) {
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {
			stmt = con.prepareStatement(SELECT_LOGIN);
			stmt.setString(1, uuid.replaceAll("-", ""));
			result = stmt.executeQuery();
			if(result.next())
				return parseData(result);
			else
				return null;
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to get user data", e);
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
			PreparedStatement stmt = con.prepareStatement(GET_USERS);
			return stmt.executeQuery();
		} catch (SQLException e) {
			return null;
		}
	}

	@Override
	public LoginData parseData(ResultSet data) {
		try {
			String uuid = data.getString("unique_user_id");
			String password = data.getString("password");
			int encryption = data.getInt("encryption");
			String ipaddr = data.getString("ip");

			return new LoginData(uuid, password, encryption, ipaddr);
		} catch (SQLException e) {
			return null;
		}
	}

        private void closeQuietly(AutoCloseable closeable) {
                if (closeable != null) {
                        try {
                                closeable.close();
                        } catch (Exception e) {
                                log.log(Level.SEVERE, "Failed to close", e);
                        }
                }
        }
}
