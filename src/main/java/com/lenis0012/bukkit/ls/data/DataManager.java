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
	 * Keep SQL connection
	 *
	 * @return Connection alive?
	 */
	public boolean pingConnection();

	/**
	 * Check if a player is registered
	 *
	 * @param user Username
	 * @return User registered?
	 */
	public boolean isRegistered(String user);

	/**
	 * Register a user
	 *
	 * @param login LoginData
	 */
	public void register(LoginData login);

	/**
	 * Update a player's password
	 *
	 * @param login LoginData
	 */
	public void updatePassword(LoginData login);

	/**
	 * Update a user ip
	 * 
	 * @param login LoginData
	 */
	public void updateIp(LoginData login);

	/**
	 * Get user stored data
	 *
	 * @param uuid Player UUID
	 * @return LoginData
	 */
	public LoginData getData(String uuid);

	/**
	 * Remove a user from the database
	 *
	 * @param user Username
	 */
	public void removeUser(String user);

	/**
	 * Get all registered users
	 *
	 * @return All registered users
	 */
	public ResultSet getAllUsers();

	/**
	 * Parse single used row
	 *
	 * @param data ResultSet
	 * @return LoginData
	 */
	public LoginData parseData(ResultSet data);
}
