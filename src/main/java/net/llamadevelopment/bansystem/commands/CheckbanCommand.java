package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;

public class CheckbanCommand extends PluginCommand<BanSystem> {

    public CheckbanCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Checkban.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TEXT, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Checkban.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Checkban.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Checkban.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                String player = args[0];
                this.getPlugin().provider.playerIsBanned(player, isBanned -> {
                    if (isBanned) {
                        this.getPlugin().provider.getBan(player, ban -> {
                            sender.sendMessage(Language.get("CheckbanInfo", player));
                            sender.sendMessage(Language.get("CheckbanReason", ban.getReason()));
                            sender.sendMessage(Language.get("CheckbanID", ban.getBanID()));
                            sender.sendMessage(Language.get("CheckbanBanner", ban.getBanner()));
                            sender.sendMessage(Language.get("CheckbanDate", ban.getDate()));
                            sender.sendMessage(Language.get("CheckbanTime", this.getPlugin().provider.getRemainingTime(ban.getTime())));
                        });
                    } else {
                        this.getPlugin().provider.banIdExists(player, false, exists -> {
                            if (exists) {
                                this.getPlugin().provider.getBanById(player, false, ban -> {
                                    sender.sendMessage(Language.get("CheckbanIdInfo", ban.getBanID()));
                                    sender.sendMessage(Language.get("CheckbanIdPlayer", ban.getPlayer()));
                                    sender.sendMessage(Language.get("CheckbanIdReason", ban.getReason()));
                                    sender.sendMessage(Language.get("CheckbanIdBanner", ban.getBanner()));
                                    sender.sendMessage(Language.get("CheckbanIdDate", ban.getDate()));
                                    sender.sendMessage(Language.get("CheckbanIdTime", this.getPlugin().provider.getRemainingTime(ban.getTime())));
                                });
                            } else sender.sendMessage(Language.get("PlayerNotBanned"));
                        });
                    }
                });
            } else sender.sendMessage(Language.get("CheckbanCommandUsage", this.getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}
