package at.lowdfx.upgradeFurnace.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class Utilities {

    public static void positiveSound(@NotNull Player player) {
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    public static void negativeSound(@NotNull Player player) {
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
    }


}
