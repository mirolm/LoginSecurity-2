package com.lenis0012.bukkit.ls.data;

public class LoginData {
	public final String uuid;
	public final String password;
	public final int encryption;

	public LoginData(String uuid, String password, int encryption) {
		this.uuid = uuid;
		this.password = password;
		this.encryption = encryption;
	}
}
