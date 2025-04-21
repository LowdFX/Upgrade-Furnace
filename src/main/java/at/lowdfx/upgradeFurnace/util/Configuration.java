package at.lowdfx.upgradeFurnace.util;

import at.lowdfx.upgradeFurnace.UpgradeFurnace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


public class Configuration {
    public static FileConfiguration CONFIG;

    public static String BASIC_SERVER_NAME;
    public static boolean BASIC_CUSTOM_HELP;


    public static void init(@NotNull JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        CONFIG = plugin.getConfig();
        loadValues();
    }

    public static void reload() {
        UpgradeFurnace.PLUGIN.reloadConfig();
        CONFIG = UpgradeFurnace.PLUGIN.getConfig();
        loadValues();
    }

    private static void loadValues() {
        BASIC_SERVER_NAME = CONFIG.getString("basic.server-name", "Server");
        BASIC_CUSTOM_HELP = CONFIG.getBoolean("basic.customhelp", true);

    }

    public static FileConfiguration get() {
        return CONFIG;
    }
}
