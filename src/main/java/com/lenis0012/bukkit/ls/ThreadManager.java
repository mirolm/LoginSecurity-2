package com.lenis0012.bukkit.ls;

import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.UUID;

import com.google.common.collect.Maps;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class ThreadManager {

	private LoginSecurity plugin;
	private BukkitTask main, msg, ses, lck, to;
	private final ConcurrentMap<String, Integer> session = Maps.newConcurrentMap();
	private final ConcurrentMap<String, Integer> lockout = Maps.newConcurrentMap();
	private final ConcurrentMap<String, Integer> timeout = Maps.newConcurrentMap();
	private long nextRefresh;

	public ThreadManager(LoginSecurity plugin) {
		this.plugin = plugin;
	}

	public synchronized Map<String, Integer> getSession() {
		return this.session;
	}

	public synchronized Map<String, Integer> getLockout() {
		return this.lockout;
	}

	public synchronized Map<String, Integer> getTimeout() {
		return this.timeout;
	}

	public void startMainTask() {
		this.nextRefresh = System.currentTimeMillis() + 300000;
		main = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
			public void run() {
				long time = System.currentTimeMillis();
				if (time >= nextRefresh) {
					if (plugin != null) {
						if (plugin.data != null) {
							if (!plugin.data.pingConn()) {
								plugin.data.openConn();
							}
						}
					}

					nextRefresh = System.currentTimeMillis() + 300000;
				}
			}
		}, 200L, 200L);
	}

	public void stopMainTask() {
		if (this.main != null) {
			this.main.cancel();
			this.main = null;
		}
	}

	public void startMsgTask() {
		msg = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
			public void run() {
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					String uuid = player.getUniqueId().toString();
					if (plugin.authList.containsKey(uuid)) {
						boolean register = plugin.authList.get(uuid);
						if (register) {
							player.sendMessage(ChatColor.RED + Lang.REG_MSG.toString());
						} else {
							player.sendMessage(ChatColor.RED + Lang.LOG_MSG.toString());
						}
					}
				}
			}
		}, 200L, 200L);
	}

	public void stopMsgTask() {
		if (msg != null) {
			msg.cancel();
		}

		msg = null;
	}

	public void startSessionTask() {
		ses = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
			public void run() {
				Iterator<String> it = getSession().keySet().iterator();
				while (it.hasNext()) {
					String puuid = it.next();
					int current = getSession().get(puuid);
					if (current >= 1) {
						current -= 1;
						getSession().put(puuid, current);
					} else {
						it.remove();
					}
				}
			}
		}, 20, 20);
	}

	public void stopSessionTask() {
		if (ses != null) {
			ses.cancel();
		}

		ses = null;
	}

	public void startLockTask() {
		lck = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
			public void run() {
				Iterator<String> it = getLockout().keySet().iterator();
				while (it.hasNext()) {
					String puuid = it.next();
					int current = getLockout().get(puuid);
					if (current >= 1) {
						current -= 1;
						getLockout().put(puuid, current);
					} else {
						it.remove();
					}
				}
			}
		}, 1200L, 1200L);
	}

	public void stopLockTask() {
		if (lck != null) {
			lck.cancel();
		}

		lck = null;
	}

	public void startTimeoutTask() {
		to = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
			public void run() {
				Iterator<String> it = getTimeout().keySet().iterator();
				while (it.hasNext()) {
					String puuid = it.next();
					int current = getTimeout().get(puuid);
					if (current >= 1) {
						current -= 1;
						getTimeout().put(puuid, current);
					} else {
						it.remove();
						Player player = Bukkit.getPlayer(UUID.fromString(puuid));
						if (player != null && player.isOnline()) {
							player.kickPlayer(Lang.TIMED_OUT.toString());
						}
					}
				}
			}
		}, 20, 20);
	}

	public void stopTimeoutTask() {
		if (to != null) {
			to.cancel();
		}

		to = null;
	}
}
