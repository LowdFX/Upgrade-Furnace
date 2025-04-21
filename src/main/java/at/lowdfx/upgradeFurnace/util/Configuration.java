package at.lowdfx.upgradeFurnace.util;

import at.lowdfx.upgradeFurnace.UpgradeFurnace;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class Configuration {
    public static FileConfiguration CONFIG;

    public static String BASIC_SERVER_NAME;
    public static boolean BASIC_CUSTOM_HELP;

    // Upgrade-Anforderungen aus der Config geladen
    public static final Map<Integer, Material> REQUIRE_MATERIAL = new HashMap<>();
    public static final Map<Integer, Integer> REQUIRE_AMOUNT = new HashMap<>();
    public static final Map<Integer, Integer> REQUIRE_XP_LEVELS = new HashMap<>();

    public static void init(@NotNull JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        CONFIG = plugin.getConfig();
        loadValues();
    }

    private static void loadValues() {
        BASIC_SERVER_NAME = CONFIG.getString("basic.server-name", "Server");
        BASIC_CUSTOM_HELP = CONFIG.getBoolean("basic.customhelp", true);

        // Upgrade-Anforderungen laden
        REQUIRE_MATERIAL.clear();
        REQUIRE_AMOUNT.clear();
        REQUIRE_XP_LEVELS.clear();
        ConfigurationSection section = CONFIG.getConfigurationSection("requirements");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                try {
                    int level = Integer.parseInt(key);
                    String matName = CONFIG.getString("requirements." + key + ".material");
                    int amount = CONFIG.getInt("requirements." + key + ".amount");
                    int xp = CONFIG.getInt("requirements." + key + ".xp_levels", 0);
                    REQUIRE_MATERIAL.put(level, Material.valueOf(matName.toUpperCase()));
                    REQUIRE_AMOUNT.put(level, amount);
                    REQUIRE_XP_LEVELS.put(level, xp);
                } catch (Exception ignored) {
                    // Ungültiger Eintrag überspringen
                }
            }
        }
    }

    public static Material getRequirementMaterial(int level) {
        return REQUIRE_MATERIAL.get(level);
    }

    public static int getRequirementAmount(int level) {
        return REQUIRE_AMOUNT.getOrDefault(level, 0);
    }

    public static int getRequirementXpLevels(int level) {
        return REQUIRE_XP_LEVELS.getOrDefault(level, 0);
    }

    public static FileConfiguration get() {
        return CONFIG;
    }
}
