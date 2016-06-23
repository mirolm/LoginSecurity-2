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
	 * Check if a player is registered
	 * 
	 * @param user Username
	 * @return User registered?
	 */
	public synchronized boolean isRegistered(String user);
	
	/**
	 * Register a user
	 * 
	 * @param user Username
	 * @param password Password
	 * @param encryption Encryption type id
	 * @param ip User address
	 */
	public synchronized void register(String user, String password, int encryption, String ip);
	
	/**
	 * Update a player's password
	 * 
	 * @param user Username
	 * @param password New password
	 * @param encryption Encryption type id
	 */
	public synchronized void updatePassword(String user, String password, int encryption);
	
	/**
	 * Update a user ip
	 * 
	 * @param user Username
	 * @param ip New address
	 */
	public synchronized void updateIp(String user, String ip);
	
	/**
	 * Get a user password
	 * 
	 * @param user Username
	 * @return Password
	 */
	public synchronized String getPassword(String user);
	
	/**
	 * Get a user encryption type
	 * 
	 * @param user Username
	 * @return Encryption type id
	 */
	public synchronized int getEncryptionTypeId(String user);
	
	/**
	 * Get a user ip address
	 * 
	 * @param user Username
	 * @return Encryption type id
	 */
	public synchronized String getIp(String user);
	
	/**
	 * Remove a user from the database
	 * 
	 * @param user Username
	 */
	public synchronized void removeUser(String user);
	
	/**
	 * Get all registered users
	 * 
	 * @return All registered users
	 */
	public ResultSet getAllUsers();
	
	/**
	 * Close stuff
	 * 
	 */
	public void closeQuietly(AutoCloseable closeable);
}
