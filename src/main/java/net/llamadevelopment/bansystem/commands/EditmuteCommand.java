package net.llamadevelopment.bansystem.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.language.Language;

public class EditmuteCommand extends PluginCommand<BanSystem> {

    public EditmuteCommand(BanSystem owner) {
        super(owner.getConfig().getString("Commands.Editmute.Name"), owner);
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("editType", false, new String[]{"reason", "time"})
        });
        this.setDescription(owner.getConfig().getString("Commands.Editmute.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Editmute.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Editmute.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission(this.getPermission())) {
            if (args.length >= 3) {
                String player = args[0];
                if (args[1].equalsIgnoreCase("reason")) {
                    this.getPlugin().provider.playerIsMuted(player, isMuted -> {
                        if (isMuted) {
                            String reason = "";
                            for (int i = 2; i < args.length; ++i) reason = reason + args[i] + " ";
                            this.getPlugin().provider.setMuteReason(player, reason);
                            Player onlinePlayer = Server.getInstance().getPlayer(player);
                            if (onlinePlayer != null) {
                                this.getPlugin().provider.getMute(player, mute -> {
                                    this.getPlugin().provider.cachedMutes.remove(player);
                                    this.getPlugin().provider.cachedMutes.put(player, mute);
                                });
                            }
                            sender.sendMessage(Language.get("ReasonSet"));
                        } else sender.sendMessage(Language.get("PlayerIsNotMuted"));
                    });
                } else if (args.length == 4 && args[1].equalsIgnoreCase("time")) {
                    this.getPlugin().provider.playerIsMuted(player, isMuted -> {
                        if (isMuted) {
                            try {
                                String type = args[2];
                                int time = Integer.parseInt(args[3]);
                                int seconds;
                                if (type.equalsIgnoreCase("days")) seconds = time * 86400;
                                else if (type.equalsIgnoreCase("hours")) seconds = time * 3600;
                                else {
                                    sender.sendMessage(Language.get("EditmuteCommandUsage1", this.getName()));
                                    sender.sendMessage(Language.get("EditmuteCommandUsage2", this.getName()));
                                    return;
                                }
                                long end = System.currentTimeMillis() + seconds * 1000L;
                                this.getPlugin().provider.setMuteTime(player, end);
                                Player onlinePlayer = Server.getInstance().getPlayer(player);
                                if (onlinePlayer != null) {
                                    this.getPlugin().provider.getMute(player, mute -> {
                                        this.getPlugin().provider.cachedMutes.remove(player);
                                        this.getPlugin().provider.cachedMutes.put(player, mute);
                                    });
                                }
                                sender.sendMessage(Language.get("TimeSet"));
                            } catch (NumberFormatException exception) {
                                sender.sendMessage(Language.get("InvalidNumber"));
                            }
                        } else sender.sendMessage(Language.get("PlayerIsNotMuted"));
                    });
                } else {
                    sender.sendMessage(Language.get("EditmuteCommandUsage1", this.getName()));
                    sender.sendMessage(Language.get("EditmuteCommandUsage2", this.getName()));
                }
            } else {
                sender.sendMessage(Language.get("EditmuteCommandUsage1", this.getName()));
                sender.sendMessage(Language.get("EditmuteCommandUsage2", this.getName()));
            }
        } else sender.sendMessage(Language.get("NoPermission"));
        return false;
    }

}
