package com.lenis0012.bukkit.ls.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;
import java.util.logging.Level;

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

		createTables();
	}

	@Override
	public void close() {
		closeQuietly(datasrc);
	}

	@Override
	public Connection getConn() {
		try {
 			return datasrc.getConnection();
 		} catch(Exception e) {
 			return null;
 		}
	}

	private void createTables() {
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = datasrc.getConnection();

			stmt = con.prepareStatement(CREATE_TABLE);
			stmt.setQueryTimeout(30);

			stmt.executeUpdate();
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Failed to create tables");
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

		try {
			con = datasrc.getConnection();

			stmt = con.prepareStatement(CHECK_REG);
			stmt.setString(1, uuid.replaceAll("-", ""));

			result = stmt.executeQuery();
			return = result.next();
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Failed to check user");
			return false;
		} finally {
			closeQuietly(result);
			closeQuietly(stmt);
			closeQuietly(con);
		}
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
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to create user");
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
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to update user");
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

		try {
			con = datasrc.getConnection();

			stmt = con.prepareStatement(SELECT_LOGIN);
			stmt.setString(1, uuid.replaceAll("-", ""));

			result = stmt.executeQuery();
			if(result.next()) {
				return = parseData(result);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to get user");
			return null;
		} finally {
			closeQuietly(result);
			closeQuietly(stmt);
			closeQuietly(con);
		}
	}

	@Override
	public ResultSet getAllUsers(Connection con) {
		PreparedStatement stmt = null;

		try {
			stmt = con.prepareStatement(SELECT_USERS);

			return stmt.executeQuery();
		} catch (Exception e) {
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
		} catch (Exception e) {
			return null;
		}
	}

	@Override
        public void closeQuietly(AutoCloseable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
	                }
                } catch (Exception e) {
                        logger.log(Level.SEVERE, "Failed to close");
                }
        }
}
