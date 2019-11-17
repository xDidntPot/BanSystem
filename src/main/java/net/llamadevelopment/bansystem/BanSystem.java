package net.llamadevelopment.bansystem;

import cn.nukkit.Player;
import cn.nukkit.command.CommandMap;
import cn.nukkit.plugin.PluginBase;
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

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> banCollection, muteCollection;
    public BanManager banManager;
    public MuteManager muteManager;

    @Override
    public void onEnable() {
        getLogger().info("Starting BanSystem...");
        instance = this;
        getLogger().info("Loading all components...");
        this.getServer().getPluginManager().registerEvents(new JoinListener(), this);
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.saveDefaultConfig();
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
                getLogger().error("§4Please check your details in the config.yml or check your mongodb database \"" + getConfig().getString("Database") + "\"");
                onDisable();
            }
        } else {
            getLogger().info("Using config...");
            saveResource("bans.yml");
            saveResource("mutes.yml");
            getLogger().info("§aPlugin successfully started.");
        }
    }

    private void registerCommands() {
        CommandMap map = getServer().getCommandMap();
        map.register("ban", new BanCommand(this));
        map.register("unban", new UnbanCommand(this));
        map.register("check", new CheckCommand(this));
        map.register("mute", new MuteCommand(this));
        map.register("unmute", new UnmuteCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling BanSystem...");
        if (getConfig().getBoolean("MongoDB")) {
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
