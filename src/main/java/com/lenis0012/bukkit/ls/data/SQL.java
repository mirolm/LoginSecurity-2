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
        private String PING_CONN;
	private String CREATE_TABLE;
        private String CHECK_REG;
        private String INSERT_LOGIN;
        private String UPDATE_PASS;
        private String UPDATE_ADDR;
        private String DELETE_LOGIN;
        private String SELECT_LOGIN;
        private String SELECT_USERS;

	public SQL(String driver) {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, "Failed to load SQL driver", e);
		}
	}

	public void initConnection(String table, String url) {
		PING_CONN = "SELECT 1;";
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + table + " (unique_user_id VARCHAR(130) NOT NULL UNIQUE, password VARCHAR(300) NOT NULL, encryption INT, ip VARCHAR(130) NOT NULL);";
		CHECK_REG = "SELECT 1 FROM " + table + " WHERE unique_user_id = ?;";
		INSERT_LOGIN = "INSERT INTO " + table + "(unique_user_id, password, encryption, ip) VALUES(?, ?, ?, ?);";
		UPDATE_PASS = "UPDATE " + table + " SET password = ?, encryption = ? WHERE unique_user_id = ?;";
		UPDATE_ADDR = "UPDATE " + table + " SET ip = ? WHERE unique_user_id = ?;";
		DELETE_LOGIN = "DELETE FROM " + table + " WHERE unique_user_id = ?;";
		SELECT_LOGIN = "SELECT unique_user_id, password, encryption, ip FROM " + table + " WHERE unique_user_id = ?;";
		SELECT_USERS = "SELECT unique_user_id, password, encryption, ip FROM " + table + ";";

		JDBC_URL = url;

		openConnection();
		createTables();
	}

	@Override
	public void openConnection() {
		try {
			closeConnection();

			con = DriverManager.getConnection(JDBC_URL);
		} catch(SQLException e) {
			log.log(Level.SEVERE, "Failed to open connection", e);
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
			stmt.setQueryTimeout(5);
			result = stmt.executeQuery();
			return result.next();
		} catch(SQLException e) {
			return false;
		} finally {
			closeQuietly(result);
			closeQuietly(stmt);
		}
	}

	private void createTables() {
		PreparedStatement stmt = null;

		try {
			stmt = con.prepareStatement(CREATE_TABLE);
			stmt.setQueryTimeout(30);
			stmt.executeUpdate();
		} catch(SQLException e) {
			log.log(Level.SEVERE, "Failed to create tables", e);
		} finally {
			closeQuietly(stmt);
		}
	}

	@Override
	public boolean checkUser(String uuid) {
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {
			stmt = con.prepareStatement(CHECK_REG);
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
	public void registerUser(LoginData login) {
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
	public void updateUser(LoginData login) {
		PreparedStatement stmt = null;

		try {
			if (login.ipaddr == null) {
				stmt = con.prepareStatement(UPDATE_PASS);
				stmt.setString(1, login.password);
				stmt.setInt(2, login.encryption);
				stmt.setString(3, login.uuid.replaceAll("-", ""));
			} else {
				stmt = con.prepareStatement(UPDATE_ADDR);
				stmt.setString(1, login.ipaddr);
				stmt.setString(2, login.uuid.replaceAll("-", ""));
			}

			stmt.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed to update user password", e);
		} finally {
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
	public LoginData getUser(String uuid) {
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
	public ResultSet getAllUsers() {
		try {
			PreparedStatement stmt = con.prepareStatement(SELECT_USERS);
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
