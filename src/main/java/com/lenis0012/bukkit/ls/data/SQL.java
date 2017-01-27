package com.lenis0012.bukkit.ls.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import com.lenis0012.bukkit.ls.LoginSecurity;

public abstract class SQL implements DataManager {
	private Logger logger;
	private HikariDataSource dataSource;
	
        private String PING_CONN = "SELECT 1";

	private String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS <TABLE> ("
					+ "unique_user_id VARCHAR(130) NOT NULL UNIQUE,"
					+ "password VARCHAR(300) NOT NULL,"
					+ "encryption INT)";

        private String CHECK_REG = "SELECT 1 FROM <TABLE> WHERE unique_user_id = ?";
        private String INSERT_LOGIN = "INSERT INTO <TABLE>(unique_user_id, password, encryption) VALUES(?, ?, ?)";
        private String UPDATE_PASS = "UPDATE <TABLE> SET password = ?, encryption = ? WHERE unique_user_id = ?";
        private String SELECT_LOGIN = "SELECT unique_user_id, password, encryption FROM <TABLE> WHERE unique_user_id = ?";
        private String SELECT_USERS = "SELECT unique_user_id, password, encryption FROM <TABLE>";

	public SQL(String driver) {
		logger = LoginSecurity.instance.getLogger();

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Failed to load driver", e);
		}
	}

	public void initConn(String table, HikariConfig config) {
		CREATE_TABLE = CREATE_TABLE.replace("<TABLE>", table);
		CHECK_REG = CHECK_REG.replace("<TABLE>", table);
		INSERT_LOGIN = INSERT_LOGIN.replace("<TABLE>", table);
		UPDATE_PASS = UPDATE_PASS.replace("<TABLE>", table);
		SELECT_LOGIN = SELECT_LOGIN.replace("<TABLE>", table);
		SELECT_USERS = SELECT_USERS.replace("<TABLE>", table);

		ThreadFactory threadFactory = new ThreadFactoryBuilder();
                threadFactory.setNameFormat(getName() + " Database Pool Thread #%1$d");
                threadFactory.setDaemon(true);
                threadFactory.build();
		
		Properties properties = new Properties();
		properties.setProperty("date_string_format", "yyyy-MM-dd HH:mm:ss");
        	
		config.setDataSourceProperties(properties);
		config.setConnectionTestQuery(PING_CONN);
		config.setThreadFactory(threadFactory);
		
		this.dataSource = new HikariDataSource(config);
		
		createTables();
	}

	private void createTables() {
		PreparedStatement stmt = null;

		try {
			stmt = con.prepareStatement(CREATE_TABLE);
			stmt.setQueryTimeout(30);
			stmt.executeUpdate();
		} catch(SQLException e) {
			logger.log(Level.SEVERE, "Failed to create tables", e);
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
			logger.log(Level.SEVERE, "Failed to check user", e);
		} finally {
			closeQuietly(result);
			closeQuietly(stmt);
		}

		return false;
	}

	@Override
	public void regUser(LoginData login) {
		PreparedStatement stmt = null;

		try {
			stmt = con.prepareStatement(INSERT_LOGIN);
			stmt.setString(1, login.uuid.replaceAll("-", ""));
			stmt.setString(2, login.password);
			stmt.setInt(3, login.encryption);

			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Failed to create user", e);
		} finally {
			closeQuietly(stmt);
		}
	}

	@Override
	public void updateUser(LoginData login) {
		PreparedStatement stmt = null;

		try {
			stmt = con.prepareStatement(UPDATE_PASS);
			stmt.setString(1, login.password);
			stmt.setInt(2, login.encryption);
			stmt.setString(3, login.uuid.replaceAll("-", ""));

			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Failed to update user", e);
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
			if(result.next()) {
				return parseData(result);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Failed to get user", e);
		} finally {
			closeQuietly(result);
			closeQuietly(stmt);
		}

		return null;
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

			return new LoginData(uuid, password, encryption);
		} catch (SQLException e) {
			return null;
		}
	}

        private void closeQuietly(AutoCloseable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
	                }
                } catch (Exception e) {
                        logger.log(Level.SEVERE, "Failed to close", e);
                }
        }
}
