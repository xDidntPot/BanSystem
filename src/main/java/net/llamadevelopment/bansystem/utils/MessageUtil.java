package net.llamadevelopment.bansystem.utils;

import net.llamadevelopment.bansystem.BanSystem;

public class MessageUtil {

    public static String banScreen(String reason, String id, String time, String banner) {
        String str = BanSystem.getInstance().getConfig().getString("BanScreen");
        str = str.replace("%reason%", reason);
        str = str.replace("%id%", id);
        str = str.replace("%time%", time);
        str = str.replace("%banner%", banner);
        return str.replace("&", "§");
    }

    public static String muteScreen(String reason, String id, String time, String banner) {
        String str = BanSystem.getInstance().getConfig().getString("MuteScreen");
        str = str.replace("%reason%", reason);
        str = str.replace("%id%", id);
        str = str.replace("%time%", time);
        str = str.replace("%banner%", banner);
        return str.replace("&", "§");
    }
}
