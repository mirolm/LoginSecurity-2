package com.lenis0012.bukkit.ls.data;

import java.sql.Connection;
import java.sql.ResultSet;

public interface DataManager {

	/**
 	 * Close Connection Pool
 	 */
 	public void close();

	/**
 	 * Get Connection from Pool
 	 */
 	public Connection getConn();

	/**
	 * Check if player is registered
	 *
	 * @param uuid PlayerUUID
	 * @return user registered
	 */
	public boolean checkUser(String uuid);

	/**
	 * Register a user
	 *
	 * @param login LoginData
	 */
	public void regUser(LoginData login);

	/**
	 * Update player data
	 *
	 * @param login LoginData
	 */
	public void updateUser(LoginData login);

	/**
	 * Get user stored data
	 *
	 * @param uuid PlayerUUID
	 * @return LoginData
	 */
	public LoginData getUser(String uuid);

	/**
	 * Get all users data
	 *
	 * @return All registered users
	 */
	public ResultSet getAllUsers(Connection con);

	/**
	 * Parse single user row
	 *
	 * @param data ResultSet
	 * @return LoginData
	 */
	public LoginData parseData(ResultSet data);

	/**
 	 * Close Database Object
 	 */
 	public void closeQuietly(AutoCloseable closeable);
}
