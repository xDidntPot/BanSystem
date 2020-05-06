package net.llamadevelopment.bansystem.components.api;

import net.llamadevelopment.bansystem.BanSystem;
import net.llamadevelopment.bansystem.components.managers.database.Provider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class BanSystemAPI {

    private BanSystem banSystem = BanSystem.getInstance();
    private static Provider provider;
    private static SystemSettings systemSettings;

    public void initBanSystemAPI() {
        systemSettings = new SystemSettings(banSystem.getConfig().getBoolean("Settings.Debug"), banSystem.getDescription().getVersion(), banSystem.getConfig().getInt("Settings.JoinDelay"), banSystem.getConfig().getBoolean("Settings.Waterdog"));
    }

    public void setProvider(Provider provider) {
        BanSystemAPI.provider = provider;
    }

    public static Provider getProvider() {
        return provider;
    }

    public static SystemSettings getSystemSettings() {
        return systemSettings;
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
