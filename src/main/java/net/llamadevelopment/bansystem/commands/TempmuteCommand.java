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

public class TempmuteCommand extends PluginCommand<BanSystem> {

    public TempmuteCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Tempmute.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("timeType", false, new String[]{"days", "hours"}),
                new CommandParameter("time", CommandParamType.INT, false),
                new CommandParameter("reason", CommandParamType.TEXT, false)
        });
        this.setDescription(owner.getConfig().getString("Commands.Tempmute.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Tempmute.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Tempmute.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length >= 4) {
                String player = args[0];
                if (args[1].equalsIgnoreCase("days") || args[1].equalsIgnoreCase("hours")) {
                    String timeString = args[1];
                    try {
                        int time = Integer.parseInt(args[2]);
                        int seconds = 0;
                        String reason = "";
                        for (int i = 3; i < args.length; ++i) reason = reason + args[i] + " ";
                        if (timeString.equalsIgnoreCase("days")) seconds = time * 86400;
                        if (timeString.equalsIgnoreCase("hours")) seconds = time * 3600;
                        String finalReason1 = reason;
                        int finalSeconds1 = seconds;
                        this.getPlugin().provider.playerIsMuted(player, isMuted -> {
                            if (isMuted) {
                                sender.sendMessage(Language.get("PlayerIsMuted"));
                                return;
                            }
                            this.getPlugin().provider.mutePlayer(player, finalReason1, sender.getName(), finalSeconds1);
                            sender.sendMessage(Language.get("PlayerMuted", player));
                            Player onlinePlayer = Server.getInstance().getPlayer(player);
                            if (onlinePlayer != null) {
                                this.getPlugin().provider.getMute(player, mute -> {
                                    SystemSettings settings = BanSystemAPI.getSystemSettings();
                                    settings.cachedMute.put(player, mute);
                                });
                            }
                        });
                    } catch (NumberFormatException exception) {
                        sender.sendMessage(Language.get("InvalidNumber"));
                    }
                } else sender.sendMessage(Language.get("TempmuteCommandUsage", this.getName()));
            } else sender.sendMessage(Language.get("TempmuteCommandUsage", this.getName()));
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}
