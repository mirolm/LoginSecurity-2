package com.lenis0012.loginsecurity.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SQLProvider implements SQLManager {
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

    SQLProvider(Plugin plugin) {
        this.logger = plugin.getLogger();
    }

    void init(String table, HikariConfig config) {
        CREATE_TABLE = CREATE_TABLE.replace("<TABLE>", table);
        MODIFY_LOGIN = MODIFY_LOGIN.replace("<TABLE>", table);
        UPDATE_DATE = UPDATE_DATE.replace("<TABLE>", table);
        SELECT_LOGIN = SELECT_LOGIN.replace("<TABLE>", table);
        SELECT_USERS = SELECT_USERS.replace("<TABLE>", table);

        try {
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create pool.");
        }

        createTables();
    }

    @Override
    public void close() {
        try {
            dataSource.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to close pool.");
        }
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get connection.");
            return null;
        }
    }

    private void createTables() {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREATE_TABLE)) {

            stmt.setQueryTimeout(30);

            stmt.executeUpdate();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create tables.");
        }
    }

    @Override
    public void modifyLogin(LoginData login) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(MODIFY_LOGIN)) {

            stmt.setString(1, cleanUUID(login.uuid));
            stmt.setString(2, login.password);
            stmt.setInt(3, login.encryption);

            stmt.executeUpdate();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to modify login.");
        }
    }

    @Override
    public void updateDate(String uuid) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_DATE)) {

            stmt.setString(1, cleanUUID(uuid));

            stmt.executeUpdate();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update login date.");
        }
    }

    @Override
    public LoginData getLogin(String uuid) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_LOGIN)) {

            stmt.setString(1, cleanUUID(uuid));

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return parseLogin(result);
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get login.");
            return null;
        }
    }

    @Override
    public void convertAllLogin(SQLManager manager) {
        try (Connection conn = manager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USERS)) {

            logger.log(Level.INFO, "Starting to convert.");

            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    modifyLogin(parseLogin(result));
                }
            }

            logger.log(Level.INFO, "Finished.");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to convert.");
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
}
