package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.components.tools.Language;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.api.SystemSettings;
import net.llamadevelopment.bansystem.components.data.BanReason;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

public class BanCommand extends Command {

    public BanCommand(String name) {
        super(name, "Ban a player.");
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("reason", CommandParamType.TEXT, false)
        });
        setPermission("bansystem.command.ban");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        Provider api = BanSystemAPI.getProvider();
        SystemSettings settings = BanSystemAPI.getSystemSettings();
        if (sender.hasPermission(getPermission())) {
            if (args.length == 2) {
                String player = args[0];
                String reason = args[1];
                if (api.playerIsBanned(player)) {
                    sender.sendMessage(Language.get("PlayerIsBanned"));
                    return true;
                }
                if (settings.banReasons.get(reason) == null) {
                    sender.sendMessage(Language.get("ReasonNotFound"));
                    return true;
                }
                BanReason banReason = settings.banReasons.get(reason);
                api.banPlayer(player, banReason.getReason(), sender.getName(), banReason.getSeconds());
                sender.sendMessage(Language.get("PlayerBanned", player));
            } else {
                settings.banReasons.values().forEach(reason -> sender.sendMessage(Language.get("ReasonFormat", reason.getId(), reason.getReason())));
                sender.sendMessage(Language.get("BanCommandUsage", getName()));
            }
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }
}
