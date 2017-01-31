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
	private HikariDataSource datasrc;

	private String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS <TABLE> ("
					+ "unique_user_id VARCHAR(130) NOT NULL UNIQUE,"
					+ "password VARCHAR(300) NOT NULL,"
					+ "encryption INT)";

        private String CHECK_REG = "SELECT 1 FROM <TABLE> WHERE unique_user_id = ?";
        private String INSERT_LOGIN = "INSERT INTO <TABLE>(unique_user_id, password, encryption) VALUES(?, ?, ?)";
        private String UPDATE_PASS = "UPDATE <TABLE> SET password = ?, encryption = ? WHERE unique_user_id = ?";
        private String SELECT_LOGIN = "SELECT unique_user_id, password, encryption FROM <TABLE> WHERE unique_user_id = ?";
        private String SELECT_USERS = "SELECT unique_user_id, password, encryption FROM <TABLE>";

	public void init(String table, HikariConfig config) {
		logger = LoginSecurity.instance.getLogger();

		CREATE_TABLE = CREATE_TABLE.replace("<TABLE>", table);
		CHECK_REG = CHECK_REG.replace("<TABLE>", table);
		INSERT_LOGIN = INSERT_LOGIN.replace("<TABLE>", table);
		UPDATE_PASS = UPDATE_PASS.replace("<TABLE>", table);
		SELECT_LOGIN = SELECT_LOGIN.replace("<TABLE>", table);
		SELECT_USERS = SELECT_USERS.replace("<TABLE>", table);

		datasrc = new HikariDataSource(config);
		datasrc.setAutoCommit(false);		

		createTables();
	}

	@Override
	public void close() {
		closeQuietly(datasrc);
	}

	@Override
	public Connection getConnection() {
		return datasrc.getConnection();
	}

	private void createTables() {
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = datasrc.getConnection();

			stmt = con.prepareStatement(CREATE_TABLE);
			stmt.setQueryTimeout(30);

			stmt.executeUpdate();
			con.commit();
		} catch(SQLException e) {
			con.rollback();
			logger.log(Level.SEVERE, "Failed to create tables", e);
		} finally {
			closeQuietly(stmt);
			closeQuietly(con);
		}
	}

	@Override
	public boolean checkUser(String uuid) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		boolean exists;

		try {
			con = datasrc.getConnection();

			stmt = con.prepareStatement(CHECK_REG);
			stmt.setString(1, uuid.replaceAll("-", ""));

			result = stmt.executeQuery();
			exists = result.next();
			
			con.commit();

			return exists;
		} catch(SQLException e) {
			con.rollback();
			logger.log(Level.SEVERE, "Failed to check user", e);
		} finally {
			closeQuietly(result);
			closeQuietly(stmt);
			closeQuietly(con);
		}

		return false;
	}

	@Override
	public void regUser(LoginData login) {
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = datasrc.getConnection();

			stmt = con.prepareStatement(INSERT_LOGIN);
			stmt.setString(1, login.uuid.replaceAll("-", ""));
			stmt.setString(2, login.password);
			stmt.setInt(3, login.encryption);

			stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			con.rollback();
			logger.log(Level.SEVERE, "Failed to create user", e);
		} finally {
			closeQuietly(stmt);
			closeQuietly(con);
		}
	}

	@Override
	public void updateUser(LoginData login) {
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = datasrc.getConnection();

			stmt = con.prepareStatement(UPDATE_PASS);
			stmt.setString(1, login.password);
			stmt.setInt(2, login.encryption);
			stmt.setString(3, login.uuid.replaceAll("-", ""));

			stmt.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			con.rollback();
			logger.log(Level.SEVERE, "Failed to update user", e);
		} finally {
			closeQuietly(stmt);
			closeQuietly(con);
		}
	}

	@Override
	public LoginData getUser(String uuid) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		LoginData login = null;

		try {
			con = datasrc.getConnection();

			stmt = con.prepareStatement(SELECT_LOGIN);
			stmt.setString(1, uuid.replaceAll("-", ""));

			result = stmt.executeQuery();			
			if(result.next()) {
				login = parseData(result);
			}

			con.commit();
			
			return login;
		} catch (SQLException e) {
			con.rollback();
			logger.log(Level.SEVERE, "Failed to get user", e);
		} finally {
			closeQuietly(result);
			closeQuietly(stmt);
			closeQuietly(con);
		}

		return null;
	}

	@Override
	public ResultSet getAllUsers(Connection con) {
		PreparedStatement stmt = null;

		try {
			stmt = con.prepareStatement(SELECT_USERS);

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

	@Override
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
