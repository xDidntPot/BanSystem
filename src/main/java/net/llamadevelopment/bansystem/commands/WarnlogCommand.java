package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.components.tools.Language;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

import java.util.concurrent.CompletableFuture;

public class WarnlogCommand extends Command {

    public WarnlogCommand(String name) {
        super(name, "Get the warn history of a player.");
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        setPermission("bansystem.command.warnlog");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        Provider api = BanSystemAPI.getProvider();
        if (sender.hasPermission(getPermission())) {
            if (args.length == 1) {
                String player = args[0];
                CompletableFuture.runAsync(() -> {
                    int i = api.getWarnings(player).size();
                    if (i == 0) {
                        sender.sendMessage(Language.get("NoDataFound"));
                        return;
                    }
                    sender.sendMessage(Language.get("WarnlogInfo", player, i));
                    api.getWarnings(player).forEach(warn -> {
                        sender.sendMessage(Language.get("WarnlogPlaceholder"));
                        sender.sendMessage(Language.get("WarnlogReason", warn.getReason()));
                        sender.sendMessage(Language.get("WarnlogID", warn.getWarnID()));
                        sender.sendMessage(Language.get("WarnlogCreator", warn.getCreator()));
                        sender.sendMessage(Language.get("WarnlogDate", warn.getDate()));
                    });
                });
            } else sender.sendMessage(Language.get("WarnlogCommandUsage", getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }
}
