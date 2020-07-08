package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.components.tools.Language;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.data.Ban;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

import java.util.concurrent.CompletableFuture;

public class CheckbanCommand extends Command {

    public CheckbanCommand(String name) {
        super(name, "Check if a player is banned.");
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TEXT, false)
        });
        setPermission("bansystem.command.checkban");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        Provider api = BanSystemAPI.getProvider();
        if (sender.hasPermission(getPermission())) {
            if (args.length == 1) {
                String player = args[0];
                CompletableFuture.runAsync(() -> {
                    if (api.playerIsBanned(player)) {
                        Ban ban = api.getBan(player);
                        sender.sendMessage(Language.get("CheckbanInfo", player));
                        sender.sendMessage(Language.get("CheckbanReason", ban.getReason()));
                        sender.sendMessage(Language.get("CheckbanID", ban.getBanID()));
                        sender.sendMessage(Language.get("CheckbanBanner", ban.getBanner()));
                        sender.sendMessage(Language.get("CheckbanDate", ban.getDate()));
                        sender.sendMessage(Language.get("CheckbanTime", api.getRemainingTime(ban.getTime())));
                    } else sender.sendMessage(Language.get("PlayerNotBanned"));
                });
            } else sender.sendMessage(Language.get("CheckbanCommandUsage", getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }
}
