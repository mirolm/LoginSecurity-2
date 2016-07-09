package com.lenis0012.bukkit.ls.data;

import java.sql.ResultSet;

public interface DataManager {

	/**
	 * Open SQL connection
	 */
	public void openConn();

	/**
	 * Close SQL connection
	 */
	public void closeConn();

	/**
	 * Check SQL connection
	 *
	 * @return connection alive
	 */
	public boolean pingConn();

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
	public ResultSet getAllUsers();

	/**
	 * Parse single user row
	 *
	 * @param data ResultSet
	 * @return LoginData
	 */
	public LoginData parseData(ResultSet data);
}
