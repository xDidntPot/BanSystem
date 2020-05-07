package net.llamadevelopment.bansystem.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.network.protocol.ScriptCustomEventPacket;
import net.llamadevelopment.bansystem.Configuration;
import net.llamadevelopment.bansystem.components.api.BanSystemAPI;
import net.llamadevelopment.bansystem.components.api.SystemSettings;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class KickCommand extends Command {

    public KickCommand(String name) {
        super(name, "Kick a player.");
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("reason", CommandParamType.TEXT, false)
        });
        setPermission("bansystem.command.kick");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        SystemSettings settings = BanSystemAPI.getSystemSettings();
        if (sender.hasPermission(getPermission())) {
            if (args.length >= 2) {
                String player = args[0];
                String reason = "";
                for (int i = 1; i < args.length; ++i) reason = reason + args[i] + " ";
                if (settings.isWaterdog() && sender instanceof Player) {
                    Player player1 = (Player) sender;
                    ScriptCustomEventPacket customEventPacket = new ScriptCustomEventPacket();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                    try {
                        dataOutputStream.writeUTF("kickplayer");
                        dataOutputStream.writeUTF(player);
                        dataOutputStream.writeUTF(reason);
                        dataOutputStream.writeUTF(sender.getName());
                        customEventPacket.eventName = "bansystembridge:main";
                        customEventPacket.eventData = outputStream.toByteArray();
                        player1.dataPacket(customEventPacket);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Player onlinePlayer = Server.getInstance().getPlayer(player);
                if (onlinePlayer != null) {
                    onlinePlayer.kick(Configuration.getAndReplaceNP("KickScreen", reason, sender.getName()), false);
                    sender.sendMessage(Configuration.getAndReplace("PlayerKicked", player));
                } else sender.sendMessage(Configuration.getAndReplace("PlayerNotOnline"));
            } else sender.sendMessage(Configuration.getAndReplace("KickCommandUsage", getName()));
        } else sender.sendMessage(Configuration.getAndReplace("NoPermission"));
        return false;
    }
}
