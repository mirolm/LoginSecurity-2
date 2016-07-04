package com.lenis0012.bukkit.ls.data;

import java.sql.ResultSet;

public interface DataManager {

	/**
	 * Open SQL connection
	 */
	public void openConnection();

	/**
	 * Close SQL connection
	 */
	public void closeConnection();

	/**
	 * Check SQL connection
	 *
	 * @return connection alive
	 */
	public boolean pingConnection();

	/**
	 * Check if player is registered
	 *
	 * @param uuid PlayerUUID
	 * @return user registered
	 */
	public boolean isRegistered(String uuid);

	/**
	 * Register a user
	 *
	 * @param login LoginData
	 */
	public void registerUser(LoginData login);

	/**
	 * Update player data
	 *
	 * @param login LoginData
	 */
	public void updateUser(LoginData login);

	/**
	 * Remove user from database
	 *
	 * @param uuid PlayerUUID
	 */
	public void removeUser(String uuid);

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
