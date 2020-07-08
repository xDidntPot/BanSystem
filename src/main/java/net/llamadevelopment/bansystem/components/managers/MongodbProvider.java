package net.llamadevelopment.bansystem.components.managers;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.ScriptCustomEventPacket;
import cn.nukkit.utils.Config;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.tools.Language;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.api.SystemSettings;
import net.llamadevelopment.bansystem.components.data.Ban;
import net.llamadevelopment.bansystem.components.data.Mute;
import net.llamadevelopment.bansystem.components.data.Warn;
import net.llamadevelopment.bansystem.components.managers.database.Provider;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongodbProvider extends Provider {

    SystemSettings settings = BanSystemAPI.getSystemSettings();
    Config config = BanSystem.getInstance().getConfig();

    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> banCollection, banlogCollection, muteCollection, mutelogCollection, warnCollection;

    @Override
    public void connect(BanSystem server) {
        CompletableFuture.runAsync(() -> {
            MongoClientURI uri = new MongoClientURI(config.getString("MongoDB.Uri"));
            mongoClient = new MongoClient(uri);
            mongoDatabase = mongoClient.getDatabase(config.getString("MongoDB.Database"));
            banCollection = mongoDatabase.getCollection("bans");
            banlogCollection = mongoDatabase.getCollection("banlog");
            muteCollection = mongoDatabase.getCollection("mutes");
            mutelogCollection = mongoDatabase.getCollection("mutelog");
            warnCollection = mongoDatabase.getCollection("warns");
            Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
            mongoLogger.setLevel(Level.OFF);
            server.getLogger().info("[MongoClient] Connection opened.");
        });
    }

    @Override
    public void disconnect(BanSystem server) {
        mongoClient.close();
        server.getLogger().info("[MongoClient] Connection closed.");
    }

    @Override
    public boolean playerIsBanned(String player) {
        Document document = banCollection.find(new Document("player", player)).first();
        return document != null;
    }

    @Override
    public boolean playerIsMuted(String player) {
        Document document = muteCollection.find(new Document("player", player)).first();
        return document != null;
    }

    @Override
    public void banPlayer(String player, String reason, String banner, int seconds) {
        CompletableFuture.runAsync(() -> {
            long end = System.currentTimeMillis() + seconds * 1000L;
            if (seconds == -1) end = -1L;
            String id = BanSystemAPI.getRandomIDCode();
            String date = BanSystemAPI.getDate();
            Document document = new Document("player", player)
                    .append("reason", reason)
                    .append("id", id)
                    .append("banner", banner)
                    .append("date", date)
                    .append("time", end);
            banCollection.insertOne(document);
            createBanlog(new Ban(player, reason, id, banner, date, end));
            Player player1 = Server.getInstance().getPlayer(banner);
            if (settings.isWaterdog() && player1.isOnline()) {
                Ban ban = getBan(player);
                ScriptCustomEventPacket customEventPacket = new ScriptCustomEventPacket();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                try {
                    dataOutputStream.writeUTF("banplayer");
                    dataOutputStream.writeUTF(player);
                    dataOutputStream.writeUTF(ban.getReason());
                    dataOutputStream.writeUTF(ban.getBanID());
                    dataOutputStream.writeUTF(getRemainingTime(ban.getTime()));
                    customEventPacket.eventName = "bansystembridge:main";
                    customEventPacket.eventData = outputStream.toByteArray();
                    player1.dataPacket(customEventPacket);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Player onlinePlayer = Server.getInstance().getPlayer(player);
            if (onlinePlayer != null) {
                Ban ban = getBan(player);
                onlinePlayer.kick(Language.getNP("BanScreen", ban.getReason(), ban.getBanID(), getRemainingTime(ban.getTime())), false);
            }
        });
    }

    @Override
    public void mutePlayer(String player, String reason, String banner, int seconds) {
        CompletableFuture.runAsync(() -> {
            long end = System.currentTimeMillis() + seconds * 1000L;
            if (seconds == -1) end = -1L;
            String id = BanSystemAPI.getRandomIDCode();
            String date = BanSystemAPI.getDate();
            Document document = new Document("player", player)
                    .append("reason", reason)
                    .append("id", id)
                    .append("banner", banner)
                    .append("date", date)
                    .append("time", end);
            muteCollection.insertOne(document);
            createMutelog(new Mute(player, reason, id, banner, date, end));
        });
    }

    @Override
    public void warnPlayer(String player, String reason, String creator) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("player", player)
                    .append("reason", reason)
                    .append("id", BanSystemAPI.getRandomIDCode())
                    .append("creator", creator)
                    .append("date", BanSystemAPI.getDate());
            warnCollection.insertOne(document);
            Player player1 = Server.getInstance().getPlayer(creator);
            if (settings.isWaterdog() && player1.isOnline()) {
                ScriptCustomEventPacket customEventPacket = new ScriptCustomEventPacket();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                try {
                    dataOutputStream.writeUTF("warnplayer");
                    dataOutputStream.writeUTF(player);
                    dataOutputStream.writeUTF(reason);
                    dataOutputStream.writeUTF(creator);
                    customEventPacket.eventName = "bansystembridge:main";
                    customEventPacket.eventData = outputStream.toByteArray();
                    player1.dataPacket(customEventPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Player onlinePlayer = Server.getInstance().getPlayer(player);
            if (onlinePlayer != null) onlinePlayer.kick(Language.getNP("WarnScreen", reason, creator), false);
        });
    }

    @Override
    public void unbanPlayer(String player) {
        CompletableFuture.runAsync(() -> {
            MongoCollection<Document> collection = banCollection;
            collection.deleteOne(new Document("player", player));
        });
    }

    @Override
    public void unmutePlayer(String player) {
        CompletableFuture.runAsync(() -> {
            MongoCollection<Document> collection = muteCollection;
            collection.deleteOne(new Document("player", player));
        });
    }

    @Override
    public Ban getBan(String player) {
        Document document = banCollection.find(new Document("player", player)).first();
        if (document != null) {
            return new Ban(player, document.getString("reason"), document.getString("id"), document.getString("banner"), document.getString("date"), document.getLong("time"));
        }
        return null;
    }

    @Override
    public Mute getMute(String player) {
        Document document = muteCollection.find(new Document("player", player)).first();
        if (document != null) {
            return new Mute(player, document.getString("reason"), document.getString("id"), document.getString("banner"), document.getString("date"), document.getLong("time"));
        }
        return null;
    }

    @Override
    public void createBanlog(Ban ban) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("player", ban.getPlayer())
                    .append("reason", ban.getReason())
                    .append("id", ban.getBanID())
                    .append("banner", ban.getBanner())
                    .append("date", ban.getDate());
            banlogCollection.insertOne(document);
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
            mutelogCollection.insertOne(document);
        });
    }

    @Override
    public List<Ban> getBanlog(String player) {
        List<Ban> list = new ArrayList<>();
        for (Document doc : banlogCollection.find(new Document("player", player))) {
            Ban ban = new Ban(player, doc.getString("reason"), doc.getString("id"), doc.getString("banner"), doc.getString("date"), 0);
            list.add(ban);
        }
        return list;
    }

    @Override
    public List<Mute> getMutelog(String player) {
        List<Mute> list = new ArrayList<>();
        for (Document doc : mutelogCollection.find(new Document("player", player))) {
            Mute mute = new Mute(player, doc.getString("reason"), doc.getString("id"), doc.getString("banner"), doc.getString("date"), 0);
            list.add(mute);
        }
        return list;
    }

    @Override
    public List<Warn> getWarnings(String player) {
        List<Warn> list = new ArrayList<>();
        for (Document doc : warnCollection.find(new Document("player", player))) {
            Warn warn = new Warn(player, doc.getString("reason"), doc.getString("id"), doc.getString("creator"), doc.getString("date"));
            list.add(warn);
        }
        return list;
    }

    @Override
    public void clearBanlog(String player) {
        CompletableFuture.runAsync(() -> {
            for (Document doc : banlogCollection.find(new Document("player", player))) {
                MongoCollection<Document> collection = banlogCollection;
                collection.deleteOne(new Document("id", doc.getString("id")));
            }
        });
    }

    @Override
    public void clearMutelog(String player) {
        CompletableFuture.runAsync(() -> {
            for (Document doc : mutelogCollection.find(new Document("player", player))) {
                MongoCollection<Document> collection = mutelogCollection;
                collection.deleteOne(new Document("id", doc.getString("id")));
            }
        });
    }

    @Override
    public void clearWarns(String player) {
        CompletableFuture.runAsync(() -> {
            for (Document doc : warnCollection.find(new Document("player", player))) {
                MongoCollection<Document> collection = warnCollection;
                collection.deleteOne(new Document("id", doc.getString("id")));
            }
        });
    }

    @Override
    public void setBanReason(String player, String reason) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("player", player);
            Document found = banCollection.find(document).first();
            Bson newEntry = new Document("reason", reason);
            Bson newEntrySet = new Document("$set", newEntry);
            banCollection.updateOne(found, newEntrySet);
        });
    }

    @Override
    public void setMuteReason(String player, String reason) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("player", player);
            Document found = muteCollection.find(document).first();
            Bson newEntry = new Document("reason", reason);
            Bson newEntrySet = new Document("$set", newEntry);
            muteCollection.updateOne(found, newEntrySet);
        });
    }

    @Override
    public void setBanTime(String player, long time) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("player", player);
            Document found = banCollection.find(document).first();
            Bson newEntry = new Document("time", time);
            Bson newEntrySet = new Document("$set", newEntry);
            banCollection.updateOne(found, newEntrySet);
        });
    }

    @Override
    public void setMuteTime(String player, long time) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("player", player);
            Document found = muteCollection.find(document).first();
            Bson newEntry = new Document("time", time);
            Bson newEntrySet = new Document("$set", newEntry);
            muteCollection.updateOne(found, newEntrySet);
        });
    }

    @Override
    public String getRemainingTime(long duration) {
        if (duration == -1L) {
            return Language.getNP("Permanent");
        } else {
            SimpleDateFormat today = new SimpleDateFormat("dd.MM.yyyy");
            today.format(System.currentTimeMillis());
            SimpleDateFormat future = new SimpleDateFormat("dd.MM.yyyy");
            future.format(duration);
            long time = future.getCalendar().getTimeInMillis() - today.getCalendar().getTimeInMillis();
            int days = (int) (time / 86400000L);
            int hours = (int) (time / 3600000L % 24L);
            int minutes = (int) (time / 60000L % 60L);
            String day = Language.getNP("Days");
            if (days == 1) {
                day = Language.getNP("Day");
            }

            String hour = Language.getNP("Hours");
            if (hours == 1) {
                hour = Language.getNP("Hour");
            }

            String minute = Language.getNP("Minutes");
            if (minutes == 2) {
                minute = Language.getNP("Minute");
            }

            if (minutes < 1 && days == 0 && hours == 0) {
                return Language.getNP("Seconds");
            } else if (hours == 0 && days == 0) {
                return minutes + " " + minute;
            } else {
                return days == 0 ? hours + " " + hour + " " + minutes + " " + minute : days + " " + day + " " + hours + " " + hour + " " + minutes + " " + minute;
            }
        }
    }

    @Override
    public String getProvider() {
        return "MongoDB";
    }
}
