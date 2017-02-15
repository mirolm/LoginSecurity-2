package com.lenis0012.bukkit.ls;

import com.lenis0012.bukkit.ls.commands.ChangePassCommand;
import com.lenis0012.bukkit.ls.commands.LoginCommand;
import com.lenis0012.bukkit.ls.commands.RegisterCommand;
import com.lenis0012.bukkit.ls.data.Converter;
import com.lenis0012.bukkit.ls.data.Converter.FileType;
import com.lenis0012.bukkit.ls.data.DataManager;
import com.lenis0012.bukkit.ls.data.MySQL;
import com.lenis0012.bukkit.ls.data.SQLite;
import com.lenis0012.bukkit.ls.encryption.PasswordManager;
import com.lenis0012.bukkit.ls.encryption.EncryptionType;
import com.lenis0012.bukkit.ls.util.Translation;
import com.lenis0012.bukkit.ls.util.Config;
import com.lenis0012.bukkit.ls.util.LoggingFilter;
import com.lenis0012.bukkit.ls.thread.LockoutThread;
import com.lenis0012.bukkit.ls.thread.TimeoutThread;
import org.apache.logging.log4j.LogManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.UUID;

public class LoginSecurity extends JavaPlugin {
	public DataManager data;
	public PasswordManager passmgr;
	public Translation lang;
	public Config conf;
    public EncryptionType hasher;
    public LockoutThread lockout;
    public TimeoutThread timeout;

	@Override
	public void onEnable() {
		//intalize fields
        conf = new Config(this);
        lang = new Translation(this);
        hasher = EncryptionType.fromString(conf.hasher);
        data = this.getDataManager();
        passmgr = new PasswordManager(this);
        lockout = new LockoutThread(this);
        timeout = new TimeoutThread(this);

		//convert everything
		checkConverter();

		//register events
        getServer().getPluginManager().registerEvents(new LoginListener(this), this);

        //register commands
        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("changepass").setExecutor(new ChangePassCommand(this));

        //register filter
        org.apache.logging.log4j.core.Logger logger;

        logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        logger.addFilter(new LoggingFilter());

        //run threads
        lockout.runTaskTimer(this, 20L, 20L);
        timeout.runTaskTimer(this, 1200L, 1200L);

    }

	@Override
	public void onDisable() {
		data.close();

		lockout.cancel();
        timeout.cancel();
	}

	private DataManager getDataManager() {
		if (conf.usemysql) {
			return new MySQL(this);
		} else {
			return new SQLite("users.db", this);
		}
	}

	private void checkConverter() {
		File file = new File(this.getDataFolder(), "users.db");
 		if (file.exists() && data instanceof MySQL) {
			Converter conv = new Converter(FileType.SQLite, "users.db", this);
			conv.convert();
		}
	}

	public String getFullUUID(String uuid, String addr) {
                return UUID.nameUUIDFromBytes(("|#" + uuid + "^|^" + addr + "#|").getBytes()).toString();
	}

	public void debilitatePlayer(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1), true);
	}

	public void rehabPlayer(Player player) {
		player.removePotionEffect(PotionEffectType.BLINDNESS);

		// ensure that player does not drown after logging in
		player.setRemainingAir(player.getMaximumAir());
	}
}
