package com.lenis0012.bukkit.ls.data;
	
public class LoginData {
	public final String uuid;
	public final String password;
	public final int encryption;
	public final String ipaddr;
	
	public LoginData(String uuid, String password, int encryption, String ipaddr) {
		this.uuid = uuid;
		this.password = password;
		this.encryption = encryption;
		this.ipaddr = ipaddr;
	}
}
