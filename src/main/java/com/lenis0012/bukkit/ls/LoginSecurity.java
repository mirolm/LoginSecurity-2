package com.lenis0012.bukkit.ls;

import com.lenis0012.bukkit.ls.command.ChangePass;
import com.lenis0012.bukkit.ls.command.Login;
import com.lenis0012.bukkit.ls.command.Register;
import com.lenis0012.bukkit.ls.thread.Lockout;
import com.lenis0012.bukkit.ls.thread.Timeout;
import com.lenis0012.bukkit.ls.util.*;
import org.apache.logging.log4j.LogManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class LoginSecurity extends JavaPlugin {
    public Account account;
    public Translation lang;
    public Config conf;
    public Lockout lockout;
    public Timeout timeout;

    private BukkitTask locktask;
    private BukkitTask timetask;

    @Override
    public void onEnable() {
        //configuration
        conf = new Config(this);
        lang = new Translation(this);

        //account
        account = new Account(this);

        //events
        getServer().getPluginManager().registerEvents(new EventHook(this), this);

        //command
        getCommand("login").setExecutor(new Login(this));
        getCommand("register").setExecutor(new Register(this));
        getCommand("changepass").setExecutor(new ChangePass(this));

        //filter log
        org.apache.logging.log4j.core.Logger logger;

        logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        logger.addFilter(new LogFilter());

        //threads
        timeout = new Timeout(this);
        lockout = new Lockout(this);

        timetask = getServer().getScheduler().runTaskTimer(this, timeout, 0L, 200L);
        locktask = getServer().getScheduler().runTaskTimer(this, lockout, 0L, 1200L);
    }

    @Override
    public void onDisable() {
        //threads
        timetask.cancel();
        locktask.cancel();

        //account
        account.disable();
    }
}
