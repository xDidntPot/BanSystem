package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.Configuration;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

import java.util.concurrent.CompletableFuture;

public class BanlogCommand extends Command {

    public BanlogCommand(String name) {
        super(name, "Get the ban history of a player.");
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        setPermission("bansystem.command.banlog");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        Provider api = BanSystemAPI.getProvider();
        if (sender.hasPermission(getPermission())) {
            if (args.length == 1) {
                String player = args[0];
                CompletableFuture.runAsync(() -> {
                    int i = api.getBanlog(player).size();
                    if (i == 0) {
                        sender.sendMessage(Configuration.getAndReplace("NoDataFound"));
                        return;
                    }
                    sender.sendMessage(Configuration.getAndReplace("BanlogInfo", player, i));
                    api.getBanlog(player).forEach(ban -> {
                        sender.sendMessage(Configuration.getAndReplace("BanlogPlaceholder"));
                        sender.sendMessage(Configuration.getAndReplace("BanlogReason", ban.getReason()));
                        sender.sendMessage(Configuration.getAndReplace("BanlogID", ban.getBanID()));
                        sender.sendMessage(Configuration.getAndReplace("BanlogBanner", ban.getBanner()));
                        sender.sendMessage(Configuration.getAndReplace("BanlogDate", ban.getDate()));
                    });
                });
            } else sender.sendMessage(Configuration.getAndReplace("BanlogCommandUsage", getName()));
        } else sender.sendMessage(Configuration.getAndReplace("NoPermission"));
        return false;
    }
}
