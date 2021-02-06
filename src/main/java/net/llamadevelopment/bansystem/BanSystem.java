package net.llamadevelopment.bansystem;

import cn.nukkit.command.CommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;
import net.llamadevelopment.bansystem.commands.*;
import net.llamadevelopment.bansystem.components.api.API;
import net.llamadevelopment.bansystem.components.data.BanReason;
import net.llamadevelopment.bansystem.components.data.MuteReason;
import net.llamadevelopment.bansystem.components.provider.MongodbProvider;
import net.llamadevelopment.bansystem.components.provider.MysqlProvider;
import net.llamadevelopment.bansystem.components.provider.YamlProvider;
import net.llamadevelopment.bansystem.components.provider.Provider;
import net.llamadevelopment.bansystem.components.language.Language;
import net.llamadevelopment.bansystem.listeners.EventListener;

import java.util.HashMap;
import java.util.Map;

public class BanSystem extends PluginBase {

    public Provider provider;
    private final Map<String, Provider> providers = new HashMap<>();

    @Getter
    private static API api;

    @Override
    public void onEnable() {
        try {
            this.saveDefaultConfig();
            this.providers.put("MongoDB", new MongodbProvider());
            this.providers.put("MySql", new MysqlProvider());
            this.providers.put("Yaml", new YamlProvider());
            if (!this.providers.containsKey(this.getConfig().getString("Provider"))) {
                this.getLogger().error("§4Please specify a valid provider: Yaml, MySql, MongoDB");
                return;
            }
            this.provider = this.providers.get(this.getConfig().getString("Provider"));
            this.provider.connect(this);
            this.getLogger().info("§aSuccessfully loaded " + this.provider.getProvider() + " provider.");
            api = new API(this.provider, this.getConfig().getInt("Settings.JoinDelay"));
            Language.init(this);
            this.loadPlugin();
            this.getLogger().info("§aBanSystem successfully started.");
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().error("§4Failed to load BanSystem.");
        }
    }

    private void loadReasons() {
        Config c = this.getConfig();
        for (String s : c.getSection("Reasons.BanReasons").getAll().getKeys(false)) this.provider.banReasons.put(s, new BanReason(c.getString("Reasons.BanReasons." + s + ".Reason"), s, c.getInt("Reasons.BanReasons." + s + ".Seconds")));
        for (String s : c.getSection("Reasons.MuteReasons").getAll().getKeys(false)) this.provider.muteReasons.put(s, new MuteReason(c.getString("Reasons.MuteReasons." + s + ".Reason"), s, c.getInt("Reasons.MuteReasons." + s + ".Seconds")));
    }

    private void loadPlugin() {
        CommandMap map = this.getServer().getCommandMap();
        map.register("bansystem", new BanCommand(this));
        map.register("bansystem", new TempbanCommand(this));
        map.register("bansystem", new BanlogCommand(this));
        map.register("bansystem", new CheckbanCommand(this));
        map.register("bansystem", new ClearbanlogCommand(this));
        map.register("bansystem", new UnbanCommand(this));
        map.register("bansystem", new MuteCommand(this));
        map.register("bansystem", new TempmuteCommand(this));
        map.register("bansystem", new MutelogCommand(this));
        map.register("bansystem", new CheckmuteCommand(this));
        map.register("bansystem", new ClearmutelogCommand(this));
        map.register("bansystem", new UnmuteCommand(this));
        map.register("bansystem", new WarnCommand(this));
        map.register("bansystem", new WarnlogCommand(this));
        map.register("bansystem", new ClearwarningsCommand(this));
        map.register("bansystem", new EditbanCommand(this));
        map.register("bansystem", new EditmuteCommand(this));
        map.register("bansystem", new KickCommand(this));
        map.register("bansystem", new DeletebanCommand(this));
        map.register("bansystem", new DeletemuteCommand(this));
        map.register("bansystem", new DeletewarnCommand(this));
        map.register("bansystem", new HistoryCommand(this));

        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
        this.loadReasons();
    }

    @Override
    public void onDisable() {
        this.provider.disconnect(this);
    }

}
