package at.lowdfx.upgradeFurnace.util;

import at.lowdfx.upgradeFurnace.UpgradeFurnace;
import com.marcpg.libpg.storage.JsonUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.bukkit.permissions.PermissionDefault.OP;
import static org.bukkit.permissions.PermissionDefault.TRUE;

public final class Perms {
    public enum Perm {
        UPGRADE_FURNACE(              "upgradefurnace.upgrade.furnace",         "/upgrade furnace",                               TRUE);

        private final String permission;
        private final String commands;
        private final PermissionDefault def;

        Perm(String permission, String commands, PermissionDefault def) {
            this.permission = permission;
            this.commands = commands;
            this.def = def;
        }

        public String getPermission() {
            return permission;
        }
    }


    // Lädt die Berechtigungen aus der permissions.json und registriert sie.
    public static void loadPermissions() {
        try {
            if (UpgradeFurnace.PLUGIN_DIR.resolve("permissions.json").toFile().createNewFile()) {
                Map<String, Object> data = new LinkedHashMap<>();
                for (Perm perm : Perm.values()) {
                    Map<String, Object> permData = new LinkedHashMap<>();
                    permData.put("description", "Erlaubt die Benutzung von " + perm.commands);
                    permData.put("default", perm.def.name().toLowerCase());
                    data.put(perm.permission, permData);
                }
                JsonUtils.saveMapSafe(data, UpgradeFurnace.PLUGIN_DIR.resolve("permissions.json").toFile());
                UpgradeFurnace.LOG.info("Permission-Konfiguration erstellt.");
            }
        } catch (IOException e) {
            UpgradeFurnace.LOG.error("Konnte Permission-Datei nicht erstellen.");
        }

        PluginManager manager = Bukkit.getPluginManager();
        JsonUtils.loadMapSafe(UpgradeFurnace.PLUGIN_DIR.resolve("permissions.json").toFile(), Map.of()).forEach((s, o) -> {
            if (!(o instanceof Map<?, ?> map)) return;
            manager.addPermission(new Permission(s,
                    (String) map.get("description"),
                    PermissionDefault.valueOf(((String) map.get("default")).toUpperCase())));
        });
    }

    public static boolean check(@NotNull Permissible source, @NotNull Perm perm) {
        return source.hasPermission(perm.permission);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static boolean check(@NotNull CommandSourceStack source, @NotNull Perm perm) {
        return check(source.getSender(), perm);
    }
}
