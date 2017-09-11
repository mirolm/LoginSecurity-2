package com.lenis0012.loginsecurity.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SQL implements SQLManager {
    private final Logger logger;
    private HikariDataSource dataSource;

    private String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS <TABLE> ("
            + "unique_user_id VARCHAR(64) NOT NULL UNIQUE,"
            + "password VARCHAR(256) NOT NULL, encryption TINYINT,"
            + "last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

    private String MODIFY_LOGIN = "REPLACE INTO <TABLE>(unique_user_id, password, encryption) VALUES(?, ?, ?)";
    private String UPDATE_DATE = "UPDATE <TABLE> SET last_login = CURRENT_TIMESTAMP WHERE unique_user_id = ?";
    private String SELECT_LOGIN = "SELECT unique_user_id, password, encryption FROM <TABLE> WHERE unique_user_id = ?";
    private String SELECT_USERS = "SELECT unique_user_id, password, encryption FROM <TABLE>";

    SQL(Plugin plugin) {
        this.logger = plugin.getLogger();
    }

    void init(String table, HikariConfig config) {
        CREATE_TABLE = CREATE_TABLE.replace("<TABLE>", table);
        MODIFY_LOGIN = MODIFY_LOGIN.replace("<TABLE>", table);
        UPDATE_DATE = UPDATE_DATE.replace("<TABLE>", table);
        SELECT_LOGIN = SELECT_LOGIN.replace("<TABLE>", table);
        SELECT_USERS = SELECT_USERS.replace("<TABLE>", table);

        dataSource = new HikariDataSource(config);

        createTables();
    }

    @Override
    public void close() {
        closeQuietly(dataSource);
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            return null;
        }
    }

    private void createTables() {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dataSource.getConnection();

            stmt = conn.prepareStatement(CREATE_TABLE);
            stmt.setQueryTimeout(30);

            stmt.executeUpdate();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create tables");
        } finally {
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    @Override
    public void modifyLogin(LoginData login) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dataSource.getConnection();

            stmt = conn.prepareStatement(MODIFY_LOGIN);
            stmt.setString(1, cleanUUID(login.uuid));
            stmt.setString(2, login.password);
            stmt.setInt(3, login.encryption);

            stmt.executeUpdate();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to modify user");
        } finally {
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    @Override
    public void updateDate(String uuid) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dataSource.getConnection();

            stmt = conn.prepareStatement(UPDATE_DATE);
            stmt.setString(1, cleanUUID(uuid));

            stmt.executeUpdate();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update date");
        } finally {
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    @Override
    public LoginData getLogin(String uuid) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;

        try {
            conn = dataSource.getConnection();

            stmt = conn.prepareStatement(SELECT_LOGIN);
            stmt.setString(1, cleanUUID(uuid));

            result = stmt.executeQuery();
            if (result.next()) {
                return parseLogin(result);
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get user");
            return null;
        } finally {
            closeQuietly(result);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    @Override
    public void convertAllLogin(SQLManager manager) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;

        try {
            conn = manager.getConnection();

            stmt = conn.prepareStatement(SELECT_USERS);
            result = stmt.executeQuery();

            logger.log(Level.INFO, "Starting to convert.");

            while (result.next()) {
                modifyLogin(parseLogin(result));
            }

            logger.log(Level.INFO, "Finished.");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to convert.");
        } finally {
            closeQuietly(result);
            closeQuietly(stmt);
            closeQuietly(conn);
        }
    }

    private LoginData parseLogin(ResultSet data) {
        try {
            String uuid = data.getString("unique_user_id");
            String password = data.getString("password");
            int encryption = data.getInt("encryption");

            return new LoginData(uuid, password, encryption);
        } catch (Exception e) {
            return null;
        }
    }

    private String cleanUUID(String uuid) {
        return uuid.replaceAll("-", "");
    }

    private void closeQuietly(AutoCloseable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to close");
        }
    }
}