package net.llamadevelopment.bansystem;

import cn.nukkit.command.CommandMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import net.llamadevelopment.bansystem.commands.*;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.api.SystemSettings;
import net.llamadevelopment.bansystem.components.data.BanReason;
import net.llamadevelopment.bansystem.components.data.MuteReason;
import net.llamadevelopment.bansystem.components.managers.MongodbProvider;
import net.llamadevelopment.bansystem.components.managers.MysqlProvider;
import net.llamadevelopment.bansystem.components.managers.YamlProvider;
import net.llamadevelopment.bansystem.components.managers.database.Provider;
import net.llamadevelopment.bansystem.components.tools.Language;
import net.llamadevelopment.bansystem.listeners.EventListener;

import java.util.HashMap;
import java.util.Map;

public class BanSystem extends PluginBase {

    private static BanSystem instance;
    public static Provider provider;
    private static final Map<String, Provider> providers = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        try {
            saveDefaultConfig();
            BanSystemAPI api = new BanSystemAPI();
            api.initBanSystemAPI();
            registerProvider(new MongodbProvider());
            registerProvider(new MysqlProvider());
            registerProvider(new YamlProvider());
            if (!providers.containsKey(getConfig().getString("Provider"))) {
                getLogger().error("§4Please specify a valid provider: Yaml, MySql, MongoDB");
                return;
            }
            provider = providers.get(getConfig().getString("Provider"));
            provider.connect(this);
            getLogger().info("§aSuccessfully loaded " + provider.getProvider() + " provider.");
            api.setProvider(provider);
            Language.init();
            loadPlugin();
            getLogger().info("§aBanSystem successfully started.");
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error("§4Failed to load BanSystem.");
        }
    }

    private void loadReasons() {
        Config c = getConfig();
        SystemSettings settings = BanSystemAPI.getSystemSettings();
        for (String s : c.getSection("Reasons.BanReasons").getAll().getKeys(false)) settings.banReasons.put(s, new BanReason(c.getString("Reasons.BanReasons." + s + ".Reason"), s, c.getInt("Reasons.BanReasons." + s + ".Seconds")));
        for (String s : c.getSection("Reasons.MuteReasons").getAll().getKeys(false)) settings.muteReasons.put(s, new MuteReason(c.getString("Reasons.MuteReasons." + s + ".Reason"), s, c.getInt("Reasons.MuteReasons." + s + ".Seconds")));
    }

    private void loadPlugin() {
        Config c = getConfig();
        CommandMap map = getServer().getCommandMap();
        map.register(c.getString("Commands.BanCommand"), new BanCommand(c.getString("Commands.BanCommand")));
        map.register(c.getString("Commands.TempbanCommand"), new TempbanCommand(c.getString("Commands.TempbanCommand")));
        map.register(c.getString("Commands.BanlogCommand"), new BanlogCommand(c.getString("Commands.BanlogCommand")));
        map.register(c.getString("Commands.CheckbanCommand"), new CheckbanCommand(c.getString("Commands.CheckbanCommand")));
        map.register(c.getString("Commands.ClearbanlogCommand"), new ClearbanlogCommand(c.getString("Commands.ClearbanlogCommand")));
        map.register(c.getString("Commands.UnbanCommand"), new UnbanCommand(c.getString("Commands.UnbanCommand")));
        map.register(c.getString("Commands.MuteCommand"), new MuteCommand(c.getString("Commands.MuteCommand")));
        map.register(c.getString("Commands.TempmuteCommand"), new TempmuteCommand(c.getString("Commands.TempmuteCommand")));
        map.register(c.getString("Commands.MutelogCommand"), new MutelogCommand(c.getString("Commands.MutelogCommand")));
        map.register(c.getString("Commands.CheckmuteCommand"), new CheckmuteCommand(c.getString("Commands.CheckmuteCommand")));
        map.register(c.getString("Commands.ClearmutelogCommand"), new ClearmutelogCommand(c.getString("Commands.ClearmutelogCommand")));
        map.register(c.getString("Commands.UnmuteCommand"), new UnmuteCommand(c.getString("Commands.UnmuteCommand")));
        map.register(c.getString("Commands.WarnCommand"), new WarnCommand(c.getString("Commands.WarnCommand")));
        map.register(c.getString("Commands.WarnlogCommand"), new WarnlogCommand(c.getString("Commands.WarnlogCommand")));
        map.register(c.getString("Commands.ClearwarningsCommand"), new ClearwarningsCommand(c.getString("Commands.ClearwarningsCommand")));
        map.register(c.getString("Commands.EditbanCommand"), new EditbanCommand(c.getString("Commands.EditbanCommand")));
        map.register(c.getString("Commands.EditmuteCommand"), new EditmuteCommand(c.getString("Commands.EditmuteCommand")));
        map.register(c.getString("Commands.KickCommand"), new KickCommand(c.getString("Commands.KickCommand")));

        getServer().getPluginManager().registerEvents(new EventListener(), this);
        loadReasons();
    }

    @Override
    public void onDisable() {
        provider.disconnect(this);
    }

    private void registerProvider(Provider provider) {
        providers.put(provider.getProvider(), provider);
    }

    public static BanSystem getInstance() {
        return instance;
    }
}
