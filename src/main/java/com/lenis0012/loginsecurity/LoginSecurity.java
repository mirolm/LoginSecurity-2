package com.lenis0012.loginsecurity;

import com.lenis0012.loginsecurity.command.ChangeCommand;
import com.lenis0012.loginsecurity.command.LoginCommand;
import com.lenis0012.loginsecurity.command.RegisterCommand;
import com.lenis0012.loginsecurity.thread.CacheTask;
import com.lenis0012.loginsecurity.thread.LockoutTask;
import com.lenis0012.loginsecurity.thread.TimeoutTask;
import com.lenis0012.loginsecurity.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class LoginSecurity extends JavaPlugin {
    public AccountManager account;
    public TranslationLoader lang;
    public ConfigLoader config;
    public LockoutTask lockout;
    public TimeoutTask timeout;
    public CacheTask cache;

    private BukkitTask lockTask;
    private BukkitTask timeTask;
    private BukkitTask cacheTask;

    @Override
    public void onEnable() {
        //configuration
        config = new ConfigLoader(this);
        lang = new TranslationLoader(this);

        //threads
        timeout = new TimeoutTask(this);
        lockout = new LockoutTask(this);
        cache = new CacheTask(this);

        //account
        account = new AccountManager(this);

        //events
        getServer().getPluginManager().registerEvents(new EventHook(this), this);

        //command
        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("changepass").setExecutor(new ChangeCommand(this));

        //filter log
        Logger logger = (Logger) LogManager.getRootLogger();
        logger.addFilter(new LogFilter());

        //register threads
        timeTask = getServer().getScheduler().runTaskTimer(this, timeout, 100L, 200L);
        lockTask = getServer().getScheduler().runTaskTimer(this, lockout, 100L, 1200L);
        cacheTask = getServer().getScheduler().runTaskTimer(this, cache, 100L, 1200L);
    }

    @Override
    public void onDisable() {
        //cache
        cache.disable();

        //threads
        timeTask.cancel();
        lockTask.cancel();
        cacheTask.cancel();
    }
}
