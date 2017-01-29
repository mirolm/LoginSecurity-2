package com.lenis0012.bukkit.ls;

import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;
import java.util.UUID;

import com.google.common.collect.Maps;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class ThreadManager {
	private LoginSecurity plugin;
	private BukkitTask msg, lck, to;
	private final ConcurrentMap<String, Integer> lockout = Maps.newConcurrentMap();
	private final ConcurrentMap<String, Integer> timeout = Maps.newConcurrentMap();

	public ThreadManager(LoginSecurity plugin) {
		this.plugin = plugin;
	}

	public synchronized ConcurrentMap<String, Integer> getLockout() {
		return this.lockout;
	}

	public synchronized ConcurrentMap<String, Integer> getTimeout() {
		return this.timeout;
	}

	public void start() {
		startMsgTask();
		startLockTask();
		startTimeoutTask();
	}

	public void stop() {
		if (msg != null) {
			msg.cancel();
		}

		if (lck != null) {
			lck.cancel();
		}

		if (to != null) {
			to.cancel();
		}
	}
	
	private void startMsgTask() {
		msg = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
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

	private void startLockTask() {
		lck = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
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

	private void startTimeoutTask() {
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
}
