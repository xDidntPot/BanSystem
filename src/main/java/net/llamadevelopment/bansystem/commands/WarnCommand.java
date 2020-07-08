package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.Config;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.tools.Language;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

public class WarnCommand extends Command {

    public WarnCommand(String name) {
        super(name, "Warn a player.");
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("reason", CommandParamType.TEXT, false)
        });
        setPermission("bansystem.command.warn");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        Provider api = BanSystemAPI.getProvider();
        if (sender.hasPermission(getPermission())) {
            if (args.length >= 2) {
                String player = args[0];
                String reason = "";
                for (int i = 1; i < args.length; ++i) reason = reason + args[i] + " ";
                api.warnPlayer(player, reason, sender.getName());
                sender.sendMessage(Language.get("PlayerWarned", player));
                int i = api.getWarnings(player).size();
                Config c = BanSystem.getInstance().getConfig();
                if (c.getBoolean("WarnSystem.EnableBan")) {
                    if (i >= c.getInt("WarnSystem.MaxWarningCount")) {
                        api.banPlayer(player, c.getString("WarnSystem.BanReason"), "System", c.getInt("WarnSystem.BanSeconds"));
                    }
                }
            } else sender.sendMessage(Language.get("WarnCommandUsage", getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }
}
