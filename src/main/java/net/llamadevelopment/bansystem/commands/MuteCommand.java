package net.llamadevelopment.bansystem.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.api.SystemSettings;
import net.llamadevelopment.bansystem.components.data.MuteReason;

public class MuteCommand extends PluginCommand<BanSystem> {

    public MuteCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Mute.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("reason", CommandParamType.TEXT, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Mute.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Mute.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Mute.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        SystemSettings settings = BanSystemAPI.getSystemSettings();
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 2) {
                String player = args[0];
                String reason = args[1];
                this.getPlugin().provider.playerIsMuted(player, isMuted -> {
                    if (isMuted) {
                        sender.sendMessage(Language.get("PlayerIsMuted"));
                        return;
                    }
                    if (settings.muteReasons.get(reason) == null) {
                        sender.sendMessage(Language.get("ReasonNotFound"));
                        return;
                    }
                    MuteReason muteReason = settings.muteReasons.get(reason);
                    this.getPlugin().provider.mutePlayer(player, muteReason.getReason(), sender.getName(), muteReason.getSeconds());
                    sender.sendMessage(Language.get("PlayerMuted", player));
                    Player onlinePlayer = Server.getInstance().getPlayer(player);
                    if (onlinePlayer != null) {
                        this.getPlugin().provider.getMute(player, mute -> settings.cachedMute.put(player, mute));
                    }
                });
            } else {
                settings.muteReasons.values().forEach(reason -> sender.sendMessage(Language.get("ReasonFormat", reason.getId(), reason.getReason())));
                sender.sendMessage(Language.get("MuteCommandUsage", this.getName()));
            }
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}
