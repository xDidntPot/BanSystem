package net.llamadevelopment.bansystem.components.provider;

import cn.nukkit.Player;
import cn.nukkit.Server;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.api.API;
import net.llamadevelopment.bansystem.components.event.*;
import net.llamadevelopment.bansystem.components.language.Language;
import net.llamadevelopment.bansystem.components.data.Ban;
import net.llamadevelopment.bansystem.components.data.Mute;
import net.llamadevelopment.bansystem.components.data.Warn;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongodbProvider extends Provider {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> banCollection, banlogCollection, muteCollection, mutelogCollection, warnCollection;

    @Override
    public void connect(BanSystem server) {
        CompletableFuture.runAsync(() -> {
            MongoClientURI uri = new MongoClientURI(server.getConfig().getString("MongoDB.Uri"));
            this.mongoClient = new MongoClient(uri);
            this.mongoDatabase = this.mongoClient.getDatabase(server.getConfig().getString("MongoDB.Database"));
            this.banCollection = this.mongoDatabase.getCollection("bans");
            this.banlogCollection = this.mongoDatabase.getCollection("banlog");
            this.muteCollection = this.mongoDatabase.getCollection("mutes");
            this.mutelogCollection = this.mongoDatabase.getCollection("mutelog");
            this.warnCollection = this.mongoDatabase.getCollection("warns");
            Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
            mongoLogger.setLevel(Level.OFF);
            server.getLogger().info("[MongoClient] Connection opened.");
        });
    }

    @Override
    public void disconnect(BanSystem server) {
        this.mongoClient.close();
        server.getLogger().info("[MongoClient] Connection closed.");
    }

    @Override
    public void playerIsBanned(String player, Consumer<Boolean> isBanned) {
        CompletableFuture.runAsync(() -> {
            Document document = this.banCollection.find(new Document("player", player)).first();
            isBanned.accept(document != null);
        });
    }

    @Override
    public void playerIsMuted(String player, Consumer<Boolean> isMuted) {
        CompletableFuture.runAsync(() -> {
            Document document = this.muteCollection.find(new Document("player", player)).first();
            isMuted.accept(document != null);
        });
    }

    @Override
    public void banIdExists(String id, boolean history, Consumer<Boolean> exists) {
        CompletableFuture.runAsync(() -> {
            Document document;
            if (history) {
                document = this.banlogCollection.find(new Document("id", id)).first();
            } else {
                document = this.banCollection.find(new Document("id", id)).first();
            }
            exists.accept(document != null);
        });
    }

    @Override
    public void muteIdExists(String id, boolean history, Consumer<Boolean> exists) {
        CompletableFuture.runAsync(() -> {
            Document document;
            if (history) {
                document = this.mutelogCollection.find(new Document("id", id)).first();
            } else {
                document = this.muteCollection.find(new Document("id", id)).first();
            }
            exists.accept(document != null);
        });
    }

    @Override
    public void warnIdExists(String id, Consumer<Boolean> exists) {
        CompletableFuture.runAsync(() -> {
            Document document = this.warnCollection.find(new Document("id", id)).first();
            exists.accept(document != null);
        });
    }

    @Override
    public void banPlayer(String player, String reason, String banner, int seconds) {
        CompletableFuture.runAsync(() -> {
            long end = System.currentTimeMillis() + seconds * 1000L;
            if (seconds == -1) end = -1L;
            String id = this.getRandomIDCode();
            String date = this.getDate();
            Document document = new Document("player", player)
                    .append("reason", reason)
                    .append("id", id)
                    .append("banner", banner)
                    .append("date", date)
                    .append("time", end);
            this.banCollection.insertOne(document);
            Ban ban = new Ban(player, reason, id, banner, date, end);
            this.createBanlog(ban);
            Player onlinePlayer = Server.getInstance().getPlayer(player);
            if (onlinePlayer != null) {
                onlinePlayer.kick(Language.getNP("BanScreen", ban.getReason(), ban.getBanID(), this.getRemainingTime(ban.getTime())), false);
            }
            Server.getInstance().getPluginManager().callEvent(new PlayerBanEvent(ban));
        });
    }

    @Override
    public void mutePlayer(String player, String reason, String banner, int seconds) {
        CompletableFuture.runAsync(() -> {
            long end = System.currentTimeMillis() + seconds * 1000L;
            if (seconds == -1) end = -1L;
            String id = this.getRandomIDCode();
            String date = this.getDate();
            Document document = new Document("player", player)
                    .append("reason", reason)
                    .append("id", id)
                    .append("banner", banner)
                    .append("date", date)
                    .append("time", end);
            this.muteCollection.insertOne(document);
            Mute mute = new Mute(player, reason, id, banner, date, end);
            this.createMutelog(mute);
            Server.getInstance().getPluginManager().callEvent(new PlayerMuteEvent(mute));
        });
    }

    @Override
    public void warnPlayer(String player, String reason, String creator) {
        CompletableFuture.runAsync(() -> {
            String id = this.getRandomIDCode();
            String date = this.getDate();
            Document document = new Document("player", player)
                    .append("reason", reason)
                    .append("id", id)
                    .append("creator", creator)
                    .append("date", date);
            this.warnCollection.insertOne(document);
            Server.getInstance().getPluginManager().callEvent(new PlayerWarnEvent(new Warn(player, reason, id, creator, date)));
            Player onlinePlayer = Server.getInstance().getPlayer(player);
            if (onlinePlayer != null) onlinePlayer.kick(Language.getNP("WarnScreen", reason, creator), false);
        });
    }

    @Override
    public void unbanPlayer(String player) {
        CompletableFuture.runAsync(() -> {
            MongoCollection<Document> collection = this.banCollection;
            collection.deleteOne(new Document("player", player));
            Server.getInstance().getPluginManager().callEvent(new PlayerUnbanEvent(player, "null"));
        });
    }

    @Override
    public void unmutePlayer(String player) {
        CompletableFuture.runAsync(() -> {
            MongoCollection<Document> collection = this.muteCollection;
            collection.deleteOne(new Document("player", player));
            Server.getInstance().getPluginManager().callEvent(new PlayerUnmuteEvent(player, "null"));
        });
    }

    @Override
    public void unbanPlayer(String player, String executor) {
        CompletableFuture.runAsync(() -> {
            MongoCollection<Document> collection = this.banCollection;
            collection.deleteOne(new Document("player", player));
            Server.getInstance().getPluginManager().callEvent(new PlayerUnbanEvent(player, executor));
        });
    }

    @Override
    public void unmutePlayer(String player, String executor) {
        CompletableFuture.runAsync(() -> {
            MongoCollection<Document> collection = this.muteCollection;
            collection.deleteOne(new Document("player", player));
            Server.getInstance().getPluginManager().callEvent(new PlayerUnmuteEvent(player, executor));
        });
    }

    @Override
    public void getBan(String player, Consumer<Ban> ban) {
        CompletableFuture.runAsync(() -> {
            Document document = this.banCollection.find(new Document("player", player)).first();
            if (document != null) {
                ban.accept(new Ban(player, document.getString("reason"), document.getString("id"), document.getString("banner"), document.getString("date"), document.getLong("time")));
            }
        });
    }

    @Override
    public void getMute(String player, Consumer<Mute> mute) {
        CompletableFuture.runAsync(() -> {
            Document document = this.muteCollection.find(new Document("player", player)).first();
            if (document != null) {
                mute.accept(new Mute(player, document.getString("reason"), document.getString("id"), document.getString("banner"), document.getString("date"), document.getLong("time")));
            }
        });
    }

    @Override
    public void getBanById(String id, boolean history, Consumer<Ban> ban) {
        CompletableFuture.runAsync(() -> {
            if (history) {
                Document document = this.banlogCollection.find(new Document("id", id)).first();
                assert document != null;
                ban.accept(new Ban(document.getString("player"), document.getString("reason"), document.getString("id"), document.getString("banner"), document.getString("date"), 0));
            } else {
                Document document = this.banCollection.find(new Document("id", id)).first();
                assert document != null;
                ban.accept(new Ban(document.getString("player"), document.getString("reason"), document.getString("id"), document.getString("banner"), document.getString("date"), document.getLong("time")));
            }
        });
    }

    @Override
    public void getMuteById(String id, boolean history, Consumer<Mute> mute) {
        CompletableFuture.runAsync(() -> {
            if (history) {
                Document document = this.mutelogCollection.find(new Document("id", id)).first();
                assert document != null;
                mute.accept(new Mute(document.getString("player"), document.getString("reason"), document.getString("id"), document.getString("banner"), document.getString("date"), 0));
            } else {
                Document document = this.muteCollection.find(new Document("id", id)).first();
                assert document != null;
                mute.accept(new Mute(document.getString("player"), document.getString("reason"), document.getString("id"), document.getString("banner"), document.getString("date"), document.getLong("time")));
            }
        });
    }

    @Override
    public void createBanlog(Ban ban) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("player", ban.getPlayer())
                    .append("reason", ban.getReason())
                    .append("id", ban.getBanID())
                    .append("banner", ban.getBanner())
                    .append("date", ban.getDate());
            this.banlogCollection.insertOne(document);
        });
    }

    @Override
    public void createMutelog(Mute mute) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("player", mute.getPlayer())
                    .append("reason", mute.getReason())
                    .append("id", mute.getMuteID())
                    .append("banner", mute.getMuter())
                    .append("date", mute.getDate());
            this.mutelogCollection.insertOne(document);
        });
    }

    @Override
    public void getBanLog(String player, Consumer<Set<Ban>> banlog) {
        CompletableFuture.runAsync(() -> {
            Set<Ban> list = new HashSet<>();
            for (Document doc : this.banlogCollection.find(new Document("player", player))) {
                Ban ban = new Ban(player, doc.getString("reason"), doc.getString("id"), doc.getString("banner"), doc.getString("date"), 0);
                list.add(ban);
            }
            banlog.accept(list);
        });
    }

    @Override
    public void getMuteLog(String player, Consumer<Set<Mute>> mutelog) {
        CompletableFuture.runAsync(() -> {
            Set<Mute> list = new HashSet<>();
            for (Document doc : this.mutelogCollection.find(new Document("player", player))) {
                Mute mute = new Mute(player, doc.getString("reason"), doc.getString("id"), doc.getString("banner"), doc.getString("date"), 0);
                list.add(mute);
            }
            mutelog.accept(list);
        });
    }

    @Override
    public void getWarnLog(String player, Consumer<Set<Warn>> warnlog) {
        CompletableFuture.runAsync(() -> {
            Set<Warn> list = new HashSet<>();
            for (Document doc : this.warnCollection.find(new Document("player", player))) {
                Warn warn = new Warn(player, doc.getString("reason"), doc.getString("id"), doc.getString("creator"), doc.getString("date"));
                list.add(warn);
            }
            warnlog.accept(list);
        });
    }

    @Override
    public void clearBanlog(String player) {
        CompletableFuture.runAsync(() -> {
            for (Document doc : this.banlogCollection.find(new Document("player", player))) {
                MongoCollection<Document> collection = this.banlogCollection;
                collection.deleteOne(new Document("id", doc.getString("id")));
                Server.getInstance().getPluginManager().callEvent(new ClearBanlogEvent(player, "null"));
            }
        });
    }

    @Override
    public void clearMutelog(String player) {
        CompletableFuture.runAsync(() -> {
            for (Document doc : this.mutelogCollection.find(new Document("player", player))) {
                MongoCollection<Document> collection = this.mutelogCollection;
                collection.deleteOne(new Document("id", doc.getString("id")));
                Server.getInstance().getPluginManager().callEvent(new ClearMutelogEvent(player, "null"));
            }
        });
    }

    @Override
    public void clearWarns(String player) {
        CompletableFuture.runAsync(() -> {
            for (Document doc : this.warnCollection.find(new Document("player", player))) {
                MongoCollection<Document> collection = this.warnCollection;
                collection.deleteOne(new Document("id", doc.getString("id")));
                Server.getInstance().getPluginManager().callEvent(new ClearWarnlogEvent(player, "null"));
            }
        });
    }

    @Override
    public void clearBanlog(String player, String executor) {
        CompletableFuture.runAsync(() -> {
            for (Document doc : this.banlogCollection.find(new Document("player", player))) {
                MongoCollection<Document> collection = this.banlogCollection;
                collection.deleteOne(new Document("id", doc.getString("id")));
                Server.getInstance().getPluginManager().callEvent(new ClearBanlogEvent(player, executor));
            }
        });
    }

    @Override
    public void clearMutelog(String player, String executor) {
        CompletableFuture.runAsync(() -> {
            for (Document doc : this.mutelogCollection.find(new Document("player", player))) {
                MongoCollection<Document> collection = this.mutelogCollection;
                collection.deleteOne(new Document("id", doc.getString("id")));
                Server.getInstance().getPluginManager().callEvent(new ClearMutelogEvent(player, executor));
            }
        });
    }

    @Override
    public void clearWarns(String player, String executor) {
        CompletableFuture.runAsync(() -> {
            for (Document doc : this.warnCollection.find(new Document("player", player))) {
                MongoCollection<Document> collection = this.warnCollection;
                collection.deleteOne(new Document("id", doc.getString("id")));
                Server.getInstance().getPluginManager().callEvent(new ClearWarnlogEvent(player, executor));
            }
        });
    }

    @Override
    public void setBanReason(String player, String reason) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("player", player);
            Document found = this.banCollection.find(document).first();
            Bson newEntry = new Document("reason", reason);
            Bson newEntrySet = new Document("$set", newEntry);
            assert found != null;
            this.banCollection.updateOne(found, newEntrySet);
        });
    }

    @Override
    public void setMuteReason(String player, String reason) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("player", player);
            Document found = this.muteCollection.find(document).first();
            Bson newEntry = new Document("reason", reason);
            Bson newEntrySet = new Document("$set", newEntry);
            assert found != null;
            this.muteCollection.updateOne(found, newEntrySet);
        });
    }

    @Override
    public void setBanTime(String player, long time) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("player", player);
            Document found = this.banCollection.find(document).first();
            Bson newEntry = new Document("time", time);
            Bson newEntrySet = new Document("$set", newEntry);
            assert found != null;
            this.banCollection.updateOne(found, newEntrySet);
        });
    }

    @Override
    public void setMuteTime(String player, long time) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("player", player);
            Document found = this.muteCollection.find(document).first();
            Bson newEntry = new Document("time", time);
            Bson newEntrySet = new Document("$set", newEntry);
            this.muteCollection.updateOne(found, newEntrySet);
        });
    }

    @Override
    public void setBanReason(String player, String reason, String executor) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("player", player);
            Document found = this.banCollection.find(document).first();
            Bson newEntry = new Document("reason", reason);
            Bson newEntrySet = new Document("$set", newEntry);
            assert found != null;
            this.banCollection.updateOne(found, newEntrySet);
        });
    }

    @Override
    public void setMuteReason(String player, String reason, String executor) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("player", player);
            Document found = this.muteCollection.find(document).first();
            Bson newEntry = new Document("reason", reason);
            Bson newEntrySet = new Document("$set", newEntry);
            assert found != null;
            this.muteCollection.updateOne(found, newEntrySet);
        });
    }

    @Override
    public void setBanTime(String player, long time, String executor) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("player", player);
            Document found = this.banCollection.find(document).first();
            Bson newEntry = new Document("time", time);
            Bson newEntrySet = new Document("$set", newEntry);
            assert found != null;
            this.banCollection.updateOne(found, newEntrySet);
        });
    }

    @Override
    public void setMuteTime(String player, long time, String executor) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("player", player);
            Document found = this.muteCollection.find(document).first();
            Bson newEntry = new Document("time", time);
            Bson newEntrySet = new Document("$set", newEntry);
            this.muteCollection.updateOne(found, newEntrySet);
        });
    }

    @Override
    public void deleteBan(String id) {
        CompletableFuture.runAsync(() -> {
            this.banlogCollection.findOneAndDelete(new Document("id", id));
            Server.getInstance().getPluginManager().callEvent(new DeleteBanEvent(id, "null"));
        });
    }

    @Override
    public void deleteMute(String id) {
        CompletableFuture.runAsync(() -> {
            this.mutelogCollection.findOneAndDelete(new Document("id", id));
            Server.getInstance().getPluginManager().callEvent(new DeleteMuteEvent(id, "null"));
        });
    }

    @Override
    public void deleteWarn(String id) {
        CompletableFuture.runAsync(() -> {
            this.warnCollection.findOneAndDelete(new Document("id", id));
            Server.getInstance().getPluginManager().callEvent(new DeleteWarnEvent(id, "null"));
        });
    }

    @Override
    public void deleteBan(String id, String executor) {
        CompletableFuture.runAsync(() -> {
            this.banlogCollection.findOneAndDelete(new Document("id", id));
            Server.getInstance().getPluginManager().callEvent(new DeleteBanEvent(id, executor));
        });
    }

    @Override
    public void deleteMute(String id, String executor) {
        CompletableFuture.runAsync(() -> {
            this.mutelogCollection.findOneAndDelete(new Document("id", id));
            Server.getInstance().getPluginManager().callEvent(new DeleteMuteEvent(id, executor));
        });
    }

    @Override
    public void deleteWarn(String id, String executor) {
        CompletableFuture.runAsync(() -> {
            this.warnCollection.findOneAndDelete(new Document("id", id));
            Server.getInstance().getPluginManager().callEvent(new DeleteWarnEvent(id, executor));
        });
    }

    @Override
    public String getProvider() {
        return "MongoDB";
    }

}
