package net.llamadevelopment.bansystem.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;

public class CheckmuteCommand extends PluginCommand<BanSystem> {

    public CheckmuteCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Checkmute.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Checkmute.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Checkmute.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Checkmute.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                String player = args[0];
                this.getPlugin().provider.playerIsMuted(player, isMuted -> {
                    if (isMuted) {
                        this.getPlugin().provider.getMute(player, mute -> {
                            sender.sendMessage(Language.get("CheckmuteInfo", player));
                            sender.sendMessage(Language.get("CheckmuteReason", mute.getReason()));
                            sender.sendMessage(Language.get("CheckmuteID", mute.getMuteID()));
                            sender.sendMessage(Language.get("CheckmuteMuter", mute.getMuter()));
                            sender.sendMessage(Language.get("CheckmuteDate", mute.getDate()));
                            sender.sendMessage(Language.get("CheckmuteTime", BanSystemAPI.getRemainingTime(mute.getTime())));
                        });
                    } else {
                        this.getPlugin().provider.muteIdExists(player, false, exists -> {
                            if (exists) {
                                this.getPlugin().provider.getMuteById(player, false, mute -> {
                                    sender.sendMessage(Language.get("CheckmuteIdInfo", mute.getMuteID()));
                                    sender.sendMessage(Language.get("CheckmuteIdPlayer", mute.getPlayer()));
                                    sender.sendMessage(Language.get("CheckmuteIdReason", mute.getReason()));
                                    sender.sendMessage(Language.get("CheckmuteIdMuter", mute.getMuter()));
                                    sender.sendMessage(Language.get("CheckmuteIdDate", mute.getDate()));
                                    sender.sendMessage(Language.get("CheckmuteIdTime", BanSystemAPI.getRemainingTime(mute.getTime())));
                                });
                                return;
                            }
                            sender.sendMessage(Language.get("PlayerNotMuted"));
                        });
                    }
                });
            } else sender.sendMessage(Language.get("CheckmuteCommandUsage", this.getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}
