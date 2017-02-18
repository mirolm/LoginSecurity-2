package com.lenis0012.bukkit.ls;

import com.lenis0012.bukkit.ls.commands.ChangePassCommand;
import com.lenis0012.bukkit.ls.commands.LoginCommand;
import com.lenis0012.bukkit.ls.commands.RegisterCommand;
import com.lenis0012.bukkit.ls.event.LoginListener;
import com.lenis0012.bukkit.ls.thread.LockoutThread;
import com.lenis0012.bukkit.ls.thread.TimeoutThread;
import com.lenis0012.bukkit.ls.util.Config;
import com.lenis0012.bukkit.ls.util.LoggingFilter;
import com.lenis0012.bukkit.ls.util.Translation;
import org.apache.logging.log4j.LogManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class LoginSecurity extends JavaPlugin {
    public AccountManager manager;
    public Translation lang;
    public Config conf;
    public LockoutThread lockout;
    public TimeoutThread timeout;

    private BukkitTask locktask;
    private BukkitTask timetask;

    @Override
    public void onEnable() {
        //configuration
        conf = new Config(this);
        lang = new Translation(this);

        //manager
        manager = new AccountManager(this);

        //events
        getServer().getPluginManager().registerEvents(new LoginListener(this), this);

        //commands
        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("changepass").setExecutor(new ChangePassCommand(this));

        //filter log
        org.apache.logging.log4j.core.Logger logger;

        logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        logger.addFilter(new LoggingFilter());

        //threads
        timeout = new TimeoutThread(this);
        lockout = new LockoutThread(this);

        timetask = getServer().getScheduler().runTaskTimer(this, timeout, 0L, 200L);
        locktask = getServer().getScheduler().runTaskTimer(this, lockout, 0L, 1200L);
    }

    @Override
    public void onDisable() {
        //threads
        timetask.cancel();
        locktask.cancel();

        //manager
        manager.disable();
    }
}
