package at.lowdfx.upgradeFurnace;

import at.lowdfx.metrics.Metrics;
import at.lowdfx.upgradeFurnace.commands.UpgradeCommands;
import at.lowdfx.upgradeFurnace.util.Configuration;
import at.lowdfx.upgradeFurnace.util.FileUpdater;
import at.lowdfx.upgradeFurnace.util.Perms;
import at.lowdfx.upgradeFurnace.util.UpdaterJoinListener;
import com.marcpg.libpg.MinecraftLibPG;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import xyz.xenondevs.invui.InvUI;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

@SuppressWarnings({ "UnstableApiUsage", "ResultOfMethodCallIgnored" })
public final class UpgradeFurnace extends JavaPlugin {
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    public static Logger LOG;
    public static UpgradeFurnace PLUGIN;
    public static Path PLUGIN_DIR;
    public static Path DATA_DIR;



    @Override
    public void onEnable() {
        // Standardkonfigurationen und Dateien werden gemerged
        FileUpdater.updateYaml(this, "config.yml");
        FileUpdater.updateJson(this, "permissions.json");
        LOG = getSLF4JLogger();
        PLUGIN = this;
        PLUGIN_DIR = getDataPath();

        DATA_DIR = PLUGIN_DIR.resolve("data");
        DATA_DIR.toFile().mkdirs();

        InvUI.getInstance().setPlugin(this);
        MinecraftLibPG.init(this);
        Configuration.init(this);

        Perms.loadPermissions();

        // Plugin Updater
        String updateUrl = "https://raw.githubusercontent.com/LowdFX/LowdFX-Minecraft-Server-Plugin/refs/heads/master/update.txt";
        String downloadLink = "https://github.com/LowdFX/LowdFX-Minecraft-Server-Plugin/releases";
        getServer().getPluginManager().registerEvents(new UpdaterJoinListener(this, updateUrl, downloadLink), this);
        getServer().getPluginManager().registerEvents(new UpgradeCommands(), this);


        // bStats starten
        int pluginId = 25566;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("language", () -> getConfig().getString("language")));


        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands registrar = event.registrar();
            registrar.register(UpgradeCommands.furnaceCommand(), "Erstelle oder verwalte einen Kisten-Shop.");

        });

        LOG.info("UpgradeFurnace Plugin gestartet!");

    }

    @Override
    public void onDisable() {
        // onDisable
        LOG.info("UpgradeFurnace Plugin deaktiviert!");
    }

    public static @NotNull Component serverMessage(@NotNull Component message) {
        return Component.text(Configuration.BASIC_SERVER_NAME, NamedTextColor.GOLD, TextDecoration.BOLD)
                .append(Component.text(" >> ", NamedTextColor.GRAY))
                .append(message.decoration(TextDecoration.BOLD, false));
    }


}
