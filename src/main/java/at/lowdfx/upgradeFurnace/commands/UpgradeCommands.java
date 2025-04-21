package at.lowdfx.upgradeFurnace.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import at.lowdfx.upgradeFurnace.UpgradeFurnace;
import at.lowdfx.upgradeFurnace.util.Configuration;
import at.lowdfx.upgradeFurnace.util.Perms;
import at.lowdfx.upgradeFurnace.util.Perms.Perm;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class UpgradeCommands implements Listener {

    private static final NamespacedKey KEY_LEVEL = new NamespacedKey("upgradefurnace", "level");
    private static final NamespacedKey KEY_HOLO = new NamespacedKey("upgradefurnace", "hologram");
    private static final Random RANDOM = new Random();

    /**
     * /upgrade furnace
     * erhöht das Upgrade-Level um 1 bis max 4
     */
    public static LiteralCommandNode<CommandSourceStack> furnaceCommand() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("upgrade")
                .requires(src -> {
                    CommandSender s = src.getSender();
                    return s instanceof Player && Perms.check(src, Perm.UPGRADE_FURNACE);
                })
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("furnace")
                        .executes(ctx -> {
                            Player player = (Player) ctx.getSource().getSender();
                            Furnace furnace = getTargetFurnace(player);
                            if (furnace == null) {
                                player.sendMessage(UpgradeFurnace.serverMessage(
                                        Component.text("Schau auf einen Ofen!", NamedTextColor.RED)
                                ));
                                return 0;
                            }
                            PersistentDataContainer pdc = furnace.getPersistentDataContainer();
                            int current = pdc.getOrDefault(KEY_LEVEL, PersistentDataType.INTEGER, 0);
                            if (current >= 4) {
                                player.sendMessage(UpgradeFurnace.serverMessage(
                                        Component.text("Dieser Ofen ist bereits auf höchstem Level!", NamedTextColor.YELLOW)
                                ));
                                return 1;
                            }
                            int next = current + 1;

                            // Config auslesen
                            FileConfiguration cfg = Configuration.get();
                            String matName = cfg.getString("requirements." + next + ".material");
                            int req = cfg.getInt("requirements." + next + ".amount");
                            Material mat;
                            try {
                                mat = Material.valueOf(matName.toUpperCase());
                            } catch (Exception e) {
                                player.sendMessage(UpgradeFurnace.serverMessage(
                                        Component.text("Fehler in der Config: Material ungültig", NamedTextColor.RED)
                                ));
                                return 0;
                            }
                            if (!player.getInventory().contains(mat, req)) {
                                player.sendMessage(UpgradeFurnace.serverMessage(
                                        Component.text("Du brauchst " + req + " " + matName.toLowerCase() + " für Level " + next, NamedTextColor.RED)
                                ));
                                return 0;
                            }
                            player.getInventory().removeItem(new ItemStack(mat, req));

                            // Level setzen
                            pdc.set(KEY_LEVEL, PersistentDataType.INTEGER, next);
                            furnace.update();

                            // Hologramm aktualisieren
                            removeHologram(furnace);
                            spawnHologram(furnace, next);

                            player.sendMessage(UpgradeFurnace.serverMessage(
                                    Component.text("Ofen auf Level " + next + " geupgraded!", NamedTextColor.GREEN)
                            ));
                            return 1;
                        })
                )
                .build();
    }

    private static Furnace getTargetFurnace(Player player) {
        var block = player.getTargetBlockExact(5, FluidCollisionMode.NEVER);
        if (block == null || !(block.getState() instanceof Furnace)) return null;
        return (Furnace) block.getState();
    }

    private static void removeHologram(Furnace furnace) {
        PersistentDataContainer pdc = furnace.getPersistentDataContainer();
        if (!pdc.has(KEY_HOLO, PersistentDataType.STRING)) return;
        String uuidStr = pdc.get(KEY_HOLO, PersistentDataType.STRING);
        try {
            UUID uuid = UUID.fromString(uuidStr);
            Entity e = furnace.getWorld().getEntity(uuid);
            if (e != null) e.remove();
        } catch (Exception ignored) {}
        pdc.remove(KEY_HOLO);
        furnace.update();
    }

    private static void spawnHologram(Furnace furnace, int level) {
        Location loc = furnace.getBlock().getLocation().add(0.5, 1.2, 0.5);
        ArmorStand holo = (ArmorStand) furnace.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        holo.customName(Component.text("Level " + level, NamedTextColor.RED));
        holo.setCustomNameVisible(true);
        holo.setGravity(false);
        holo.setVisible(false);
        holo.setMarker(true);

        PersistentDataContainer pdc = furnace.getPersistentDataContainer();
        pdc.set(KEY_HOLO, PersistentDataType.STRING, holo.getUniqueId().toString());
        furnace.update();

        // Spawn particle effect based on level
        Location center = furnace.getBlock().getLocation().add(0.5, 0.5, 0.5);
        Particle particle;
        switch (level) {
            case 1 -> particle = Particle.SMOKE;
            case 2 -> particle = Particle.FLAME;
            case 3 -> particle = Particle.CLOUD;
            case 4 -> particle = Particle.SOUL_FIRE_FLAME;
            default -> particle = Particle.SMOKE;
        }
        // swirl around furnace: larger horizontal offsets, slight upward
        double radius = 1.0;
        int count = 20;
        double extraSpeed = 0.05;
        furnace.getWorld().spawnParticle(
                particle,
                center,
                count,
                radius, // offsetX
                0.5,    // offsetY
                radius, // offsetZ
                extraSpeed
        );
    }

    @EventHandler
    public void onStartSmelt(FurnaceStartSmeltEvent evt) {
        Furnace furnace = (Furnace) evt.getBlock().getState();
        int lvl = furnace.getPersistentDataContainer().getOrDefault(KEY_LEVEL, PersistentDataType.INTEGER, 0);
        if (lvl <= 1) return;
        evt.setTotalCookTime(evt.getTotalCookTime() / (1 + lvl));
    }

    @EventHandler
    public void onSmelt(FurnaceSmeltEvent evt) {
        Block block = evt.getBlock();
        if (!(block.getState() instanceof Furnace furnace)) return;
        Integer level = furnace.getPersistentDataContainer().get(KEY_LEVEL, PersistentDataType.INTEGER);
        if (level == null || level <= 1) return;

        // Ergebnis anpassen
        ItemStack result = evt.getResult();
        int amount = result.getAmount();
        if (level == 4) {
            // 1–3× Multiplikator
            amount *= (1 + RANDOM.nextInt(3));
        } else {
            // einfach +1 Chance pro Level
            for (int i = 0; i < level; i++) {
                if (RANDOM.nextDouble() < 0.3) amount++;
            }
        }
        result.setAmount(amount);
        evt.setResult(result);

        // Partikel an der Feuerstelle spawnen
        spawnSmeltParticles(furnace, level);
    }


    private void spawnSmeltParticles(Furnace furnace, int level) {
        // Blockzentrum plus y‑Offset
        Location loc = furnace.getBlock().getLocation().add(0.5, 0.3, 0.5);

        // Facing‑Direction ermitteln (Front des Ofens)
        BlockData data = furnace.getBlock().getBlockData();
        if (data instanceof Directional directional) {
            Vector dir = directional.getFacing().getDirection();
            loc.add(dir.multiply(0.52)); // knapp vor der Front
        }

        Particle particle;
        switch (level) {
            case 1 -> particle = Particle.SMOKE;
            case 2 -> particle = Particle.FLAME;
            case 3 -> particle = Particle.CLOUD;
            case 4 -> particle = Particle.SOUL_FIRE_FLAME;
            default -> particle = Particle.SMOKE;
        }

        furnace.getWorld().spawnParticle(
                particle,
                loc,
                8,    // count
                0.15, // offsetX
                0.15, // offsetY
                0.15, // offsetZ
                0.02  // extra
        );
    }


}
