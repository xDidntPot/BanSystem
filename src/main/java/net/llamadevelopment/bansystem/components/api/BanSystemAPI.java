package net.llamadevelopment.bansystem.components.api;

import lombok.Getter;
import lombok.Setter;
import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class BanSystemAPI {

    private final BanSystem banSystem = BanSystem.getInstance();

    @Getter @Setter
    private static Provider provider;
    @Getter
    private static SystemSettings systemSettings;

    public void initBanSystemAPI() {
        systemSettings = new SystemSettings(banSystem.getDescription().getVersion(), banSystem.getConfig().getInt("Settings.JoinDelay"), banSystem.getConfig().getBoolean("Settings.Waterdog"));
    }

    public static String getRandomIDCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder stringBuilder = new StringBuilder();
        Random rnd = new Random();
        while (stringBuilder.length() < 5) {
            int index = (int) (rnd.nextFloat() * chars.length());
            stringBuilder.append(chars.charAt(index));
        }
        return stringBuilder.toString();
    }

    public static String getDate() {
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        return dateFormat.format(now);
    }
}
