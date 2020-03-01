package net.llamadevelopment.bansystem.components.managers.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.llamadevelopment.bansystem.BanSystem;
import org.bson.Document;

public class MongoDBProvider {

    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;
    private static MongoCollection<Document> banCollection, muteCollection, banlogCollection, mutelogCollection, warnCollection;

    public static void connect(BanSystem instance) {
        try {
            MongoClientURI uri = new MongoClientURI(instance.getConfig().getString("MongoDB.Uri"));
            mongoClient = new MongoClient(uri);
            mongoDatabase = mongoClient.getDatabase(instance.getConfig().getString("MongoDB.Database"));
            banCollection = mongoDatabase.getCollection("bans");
            muteCollection = mongoDatabase.getCollection("mutes");
            banlogCollection = mongoDatabase.getCollection("banlogs");
            mutelogCollection = mongoDatabase.getCollection("mutelogs");
            warnCollection = mongoDatabase.getCollection("warnings");
            instance.getLogger().info("§aConnected successfully to database!");
        } catch (Exception e) {
            instance.getLogger().error("§4Failed to connect to database.");
            instance.getLogger().error("§4Please check your details in the config.yml.");
            e.printStackTrace();
            instance.dataError = true;
        }
    }

    public static MongoCollection<Document> getMutelogCollection() {
        return mutelogCollection;
    }

    public static MongoCollection<Document> getBanlogCollection() {
        return banlogCollection;
    }

    public static MongoCollection<Document> getMuteCollection() {
        return muteCollection;
    }

    public static MongoCollection<Document> getBanCollection() {
        return banCollection;
    }

    public static MongoCollection<Document> getWarnCollection() {
        return warnCollection;
    }

    public static MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }
}
