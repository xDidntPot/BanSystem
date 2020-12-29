package net.llamadevelopment.bansystem.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;

public class UnmuteCommand extends PluginCommand<BanSystem> {

    public UnmuteCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Unmute.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Unmute.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Unmute.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Unmute.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length == 1) {
                String player = args[0];
                this.getPlugin().provider.playerIsMuted(player, isMuted -> {
                    if (isMuted) {
                        this.getPlugin().provider.unmutePlayer(player);
                        Player onlinePlayer = Server.getInstance().getPlayer(player);
                        if (onlinePlayer != null) {
                            this.getPlugin().provider.cachedMutes.remove(onlinePlayer.getName());
                        }
                        sender.sendMessage(Language.get("PlayerUnmuted", player));
                    } else sender.sendMessage(Language.get("PlayerIsNotMuted"));
                });
            } else sender.sendMessage(Language.get("UnmuteCommandUsage", this.getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}
