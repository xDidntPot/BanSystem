package net.llamadevelopment.bansystem;

import cn.nukkit.Player;
import cn.nukkit.command.CommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.llamadevelopment.bansystem.commands.*;
import net.llamadevelopment.bansystem.listeners.ChatListener;
import net.llamadevelopment.bansystem.listeners.JoinListener;
import net.llamadevelopment.bansystem.managers.BanManager;
import net.llamadevelopment.bansystem.managers.MuteManager;
import net.llamadevelopment.bansystem.utils.MutedPlayer;
import org.bson.Document;

import java.util.HashMap;

public class BanSystem extends PluginBase {

    private static BanSystem instance;
    public HashMap<Player, MutedPlayer> mutedCache = new HashMap<Player, MutedPlayer>();
    private boolean dataError = false;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> banCollection, muteCollection;
    public BanManager banManager;
    public MuteManager muteManager;

    private int version = 1;

    @Override
    public void onEnable() {
        getLogger().info("Starting BanSystem...");
        instance = this;
        getLogger().info("Loading all components...");
        saveDefaultConfig();
        updateConfig();
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        registerCommands();
        this.banManager = new BanManager(this);
        this.muteManager = new MuteManager(this);
        getLogger().info("Components successfully loaded!");
        if (getConfig().getBoolean("MongoDB")) {
            getLogger().info("Connecting to database...");
            try {
                MongoClientURI uri = new MongoClientURI(getConfig().getString("MongoDBUri"));
                this.mongoClient = new MongoClient(uri);
                this.mongoDatabase = mongoClient.getDatabase(getConfig().getString("Database"));
                this.banCollection = mongoDatabase.getCollection(getConfig().getString("BanCollection"));
                this.muteCollection = mongoDatabase.getCollection(getConfig().getString("MuteCollection"));
                getLogger().info("§aConnected successfully to database!");
            } catch (Exception e) {
                getLogger().error("§4Failed to connect to database.");
                getLogger().error("§4Please check your details in the config.yml.");
                e.printStackTrace();
                dataError = true;
                onDisable();
            }
        } else {
            getLogger().info("Using config...");
            saveResource("bans.yml");
            saveResource("mutes.yml");
            getLogger().info("§aPlugin successfully started.");
        }
    }

    private void updateConfig() {
        Config config = getConfig();
        if (!config.exists("ConfigVersion")) {
            config.set("KickSuccess", "&aThe player &e%player% &ahas been kicked.");
            config.set("PlayerNotOnline", "&cThis player is not online.");
            config.set("KickScreen", "&3You were kicked.\\n&3Reason: &7%reason%");
            config.set("Usage.BanCommand", "&7Usage: &a%command% <Player> <ID>");
            config.set("Usage.MuteCommand", "&7Usage: &a%command% <Player> <ID>");
            config.set("Usage.UnbanCommand", "&7Usage: &a%command% <Player>");
            config.set("Usage.UnmuteCommand", "&7Usage: &a%command% <Player>");
            config.set("Usage.CheckCommand", "&7Usage: &a%command% <Player> <ban|mute>");
            config.set("Usage.KickCommand", "&7Usage: &a%command% <Player> <Reason>");
            config.set("Commands.Ban", "ban");
            config.set("Commands.Mute", "mute");
            config.set("Commands.Unban", "unban");
            config.set("Commands.Unmute", "unmute");
            config.set("Commands.Check", "check");
            config.set("Commands.Kick", "kick");
            config.set("ConfigVersion", version);
            config.save();
            config.reload();
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
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling BanSystem...");
        if (getConfig().getBoolean("MongoDB") && !dataError) {
            mongoClient.close();
        }
    }

    public MongoCollection<Document> getBanCollection() {
        return banCollection;
    }

    public MongoCollection<Document> getMuteCollection() {
        return muteCollection;
    }

    public static BanSystem getInstance() {
        return instance;
    }
}
