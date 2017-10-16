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

// CVE-2017-15361 bogus commit

public class LoginSecurity extends JavaPlugin {
    public AccountManager account;
    public TranslationLoader lang;
    public ConfigLoader config;
    public LockoutTask lockout;
    public TimeoutTask timeout;
    public CacheTask cache;

    @Override
    public void onEnable() {
        config = new ConfigLoader(this);
        lang = new TranslationLoader(this);

        timeout = new TimeoutTask(this);
        lockout = new LockoutTask(this);
        cache = new CacheTask(this);

        account = new AccountManager(this);

        getServer().getPluginManager().registerEvents(new EventHook(this), this);

        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("changepass").setExecutor(new ChangeCommand(this));

        Logger logger = (Logger) LogManager.getRootLogger();
        logger.addFilter(new LogFilter());

        getServer().getScheduler().runTaskTimer(this, timeout, 100L, 200L);
        getServer().getScheduler().runTaskTimer(this, lockout, 100L, 1200L);
        getServer().getScheduler().runTaskTimer(this, cache, 100L, 1200L);
    }

    @Override
    public void onDisable() {
        cache.disable();

        getServer().getScheduler().cancelTasks(this);
    }
}
