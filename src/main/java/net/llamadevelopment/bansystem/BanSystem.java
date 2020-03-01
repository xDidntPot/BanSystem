package net.llamadevelopment.bansystem;

import cn.nukkit.Player;
import cn.nukkit.command.CommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import net.llamadevelopment.bansystem.commands.*;
import net.llamadevelopment.bansystem.components.managers.database.MongoDBProvider;
import net.llamadevelopment.bansystem.components.managers.database.MySqlProvider;
import net.llamadevelopment.bansystem.listeners.ChatListener;
import net.llamadevelopment.bansystem.listeners.JoinListener;
import net.llamadevelopment.bansystem.components.utils.MutedPlayer;

import java.util.HashMap;

public class BanSystem extends PluginBase {

    private static BanSystem instance;
    public HashMap<Player, MutedPlayer> mutedCache = new HashMap<Player, MutedPlayer>();
    public boolean dataError = false;
    private boolean mysql, mongodb, yaml = false;
    private MySqlProvider mySql;

    private int version = 1;

    @Override
    public void onEnable() {
        instance = this;
        System.out.println();
        System.out.println(" ____               _____           _                 ");
        System.out.println("|  _ \\             / ____|         | |                ");
        System.out.println("| |_) | __ _ _ __ | (___  _   _ ___| |_ ___ _ __ ___  ");
        System.out.println("|  _ < / _` | '_ \\ \\___ \\| | | / __| __/ _ \\ '_ ` _ \\ ");
        System.out.println("| |_) | (_| | | | |____) | |_| \\__ \\ ||  __/ | | | | |");
        System.out.println("|____/ \\__,_|_| |_|_____/ \\__, |___/\\__\\___|_| |_| |_|");
        System.out.println("                           __/ |                      ");
        System.out.println("                          |___/                       ");
        getLogger().info("§aStarting and loading all components...");
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getLogger().info("Components successfully loaded!");
        if (getConfig().getString("Provider").equalsIgnoreCase("MongoDB")) {
            mongodb = true;
            getLogger().info("Connecting to database...");
            MongoDBProvider.connect(this);
        } else if (getConfig().getString("Provider").equalsIgnoreCase("MySql")) {
            mysql = true;
            getLogger().info("Connecting to database...");
            this.mySql = new MySqlProvider();
            this.mySql.createTables();
        } else if (getConfig().getString("Provider").equalsIgnoreCase("Yaml")) {
            yaml = true;
            getLogger().info("Using YAML as provider...");
            saveResource("bans.yml");
            saveResource("mutes.yml");
            getLogger().info("§aPlugin successfully started.");
        } else {
            getLogger().warning("§4§lFailed to load! Please specify a valid provider: MySql, MongoDB, Yaml");
        }
        updateConfig();
        registerCommands();
    }

    private void updateConfig() {
        if (getConfig().getInt("ConfigVersion") == 1) {
            getConfig().set("ConfigVersion", 2);
            getConfig().set("Messages.WarnScreen", "&3You have been warned. &3Reason: &7%reason% &3ID: &7%id% \n&3Creator: &7%creator%");
            getConfig().set("Messages.WarnSuccess", "&aThe player &e%player% &ahas been warned.");
            getConfig().set("Warning.EnableBan", false);
            getConfig().set("Warning.BanByAmount", 5);
            getConfig().set("Warning.BanReason", "You've been warned too many times");
            getConfig().set("Warning.BanTime", 259200);
            getConfig().set("Usage.WarnCommand", "&7Usage: &a%command% <Player> <Reason>");
            getConfig().set("Usage.WarnlogCommand", "&7Usage: &a%command% <Player>");
            getConfig().set("Commands.Warn", "warn");
            getConfig().set("Commands.Warnlog", "warnlog");
            getConfig().set("Warnlog.Info", "&aWarning: &e#%count%");
            getConfig().set("Warnlog.Player", "&aPlayer: &e%player%");
            getConfig().set("Warnlog.Reason", "&aReason: &e%reason%");
            getConfig().set("Warnlog.ID", "&aID: &e%id%");
            getConfig().set("Warnlog.Creator", "&aCreated by: &e%creator%");
            getConfig().set("Warnlog.Date", "&aDate: &e%date%");
            getConfig().save();
            getConfig().reload();
        }
    }

    private void registerCommands() {
        Config config = getConfig();
        CommandMap map = getServer().getCommandMap();
        map.register(config.getString("Commands.Ban"), new BanCommand(this));
        map.register(config.getString("Commands.Unban"), new UnbanCommand(this));
        map.register(config.getString("Commands.Check"), new CheckCommand(this));
        map.register(config.getString("Commands.Mute"), new MuteCommand(this));
        map.register(config.getString("Commands.Unmute"), new UnmuteCommand(this));
        map.register(config.getString("Commands.Kick"), new KickCommand(this));
        if (mongodb || mysql) {
            map.register(config.getString("Commands.Banlog"), new BanlogCommand(this));
            map.register(config.getString("Commands.Mutelog"), new MutelogCommand(this));
            map.register(config.getString("Commands.Warn"), new WarnCommand(this));
            map.register(config.getString("Commands.Warnlog"), new WarnlogCommand(this));
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling BanSystem...");
    }

    public static BanSystem getInstance() {
        return instance;
    }

    public boolean isMysql() {
        return mysql;
    }

    public boolean isMongodb() {
        return mongodb;
    }

    public boolean isYaml() {
        return yaml;
    }
}
