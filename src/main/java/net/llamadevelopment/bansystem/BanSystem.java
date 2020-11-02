package net.llamadevelopment.bansystem;

import cn.nukkit.command.CommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;
import net.llamadevelopment.bansystem.commands.*;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.api.SystemSettings;
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
    private static final Map<String, Provider> providers = new HashMap<>();

    @Getter
    private static BanSystem instance;

    @Override
    public void onEnable() {
        instance = this;
        try {
            this.saveDefaultConfig();
            BanSystemAPI api = new BanSystemAPI();
            api.initBanSystemAPI();
            providers.put("MongoDB", new MongodbProvider());
            providers.put("MySql", new MysqlProvider());
            providers.put("Yaml", new YamlProvider());
            if (!providers.containsKey(this.getConfig().getString("Provider"))) {
                this.getLogger().error("§4Please specify a valid provider: Yaml, MySql, MongoDB");
                return;
            }
            this.provider = providers.get(this.getConfig().getString("Provider"));
            this.provider.connect(this);
            this.getLogger().info("§aSuccessfully loaded " + this.provider.getProvider() + " provider.");
            BanSystemAPI.setProvider(this.provider);
            Language.init();
            this.loadPlugin();
            this.getLogger().info("§aBanSystem successfully started.");
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().error("§4Failed to load BanSystem.");
        }
    }

    private void loadReasons() {
        Config c = this.getConfig();
        SystemSettings settings = BanSystemAPI.getSystemSettings();
        for (String s : c.getSection("Reasons.BanReasons").getAll().getKeys(false)) settings.banReasons.put(s, new BanReason(c.getString("Reasons.BanReasons." + s + ".Reason"), s, c.getInt("Reasons.BanReasons." + s + ".Seconds")));
        for (String s : c.getSection("Reasons.MuteReasons").getAll().getKeys(false)) settings.muteReasons.put(s, new MuteReason(c.getString("Reasons.MuteReasons." + s + ".Reason"), s, c.getInt("Reasons.MuteReasons." + s + ".Seconds")));
    }

    private void loadPlugin() {
        Config config = this.getConfig();
        CommandMap map = this.getServer().getCommandMap();
        map.register(config.getString("Commands.BanCommand"), new BanCommand(this));
        map.register(config.getString("Commands.TempbanCommand"), new TempbanCommand(this));
        map.register(config.getString("Commands.BanlogCommand"), new BanlogCommand(this));
        map.register(config.getString("Commands.CheckbanCommand"), new CheckbanCommand(this));
        map.register(config.getString("Commands.ClearbanlogCommand"), new ClearbanlogCommand(this));
        map.register(config.getString("Commands.UnbanCommand"), new UnbanCommand(this));
        map.register(config.getString("Commands.MuteCommand"), new MuteCommand(this));
        map.register(config.getString("Commands.TempmuteCommand"), new TempmuteCommand(this));
        map.register(config.getString("Commands.MutelogCommand"), new MutelogCommand(this));
        map.register(config.getString("Commands.CheckmuteCommand"), new CheckmuteCommand(this));
        map.register(config.getString("Commands.ClearmutelogCommand"), new ClearmutelogCommand(this));
        map.register(config.getString("Commands.UnmuteCommand"), new UnmuteCommand(this));
        map.register(config.getString("Commands.WarnCommand"), new WarnCommand(this));
        map.register(config.getString("Commands.WarnlogCommand"), new WarnlogCommand(this));
        map.register(config.getString("Commands.ClearwarningsCommand"), new ClearwarningsCommand(this));
        map.register(config.getString("Commands.EditbanCommand"), new EditbanCommand(this));
        map.register(config.getString("Commands.EditmuteCommand"), new EditmuteCommand(this));
        map.register(config.getString("Commands.KickCommand"), new KickCommand(this));

        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.loadReasons();
    }

    @Override
    public void onDisable() {
        this.provider.disconnect(this);
    }

}
