# UpgradeFurnace

_**A PaperMC 1.21.4 plugin to upgrade furnaces for faster smelting and bonus yields.**_

## Features

- **Tiered Furnace Upgrades**: Upgrade a furnace from level 1 to 4 using configurable materials.
- **Faster Smelting**: Each upgrade level sequentially reduces smelt time.
- **Bonus Yield**: At max level, smelting yields 1–3× the normal output.
- **Holographic Display**: Shows the current upgrade level above each furnace.
- **Level-Based Particles**: Custom particle effects when upgrading and smelting.
- **Configurable Requirements**: Define materials and amounts for each level in `config.yml`.
- **Brigadier Command**: `/upgrade furnace` to perform upgrades.

## Requirements

- Java 17+
- PaperMC 1.21.4

## Installation

1. Download the latest `UpgradeFurnace.jar` from the [SpigotMC](https://www.spigotmc.org/resources/lowdfx.123832/) page. Link directs to LowdFX Plugin. Upgrade Furnace Plugin is not yet on SpigotMC because it isn`t finished.
2. Place the JAR into your server's `plugins` folder.
3. Start the server to generate default config and permissions files.

## Configuration (`config.yml`)

```yaml
basic:
  server-name: "MyServer"
  customhelp: true

requirements:
  '1':
    material: IRON_INGOT
    amount: 32
    xp_levels: 0
  '2':
    material: GOLD_INGOT
    amount: 32
    xp_levels: 0
  '3':
    material: DIAMOND
    amount: 32
    xp_levels: 0
  '4':
    material: NETHERITE_INGOT
    amount: 20
    xp_levels: 0
  '5':
    material: NETHERITE_INGOT
    amount: 30
    xp_levels: 100
```

- **`basic.server-name`**: Prefix for plugin messages.
- **`requirements.<level>.material`**: Material needed for that upgrade level.
- **`requirements.<level>.amount`**: Amount of material required.
- **`requirements.<level>.xp_levels`**: Amount of XP level required.

## Commands

| Command                   | Permission                         | Description                             |
|---------------------------|------------------------------------|-----------------------------------------|
| `/upgrade furnace`        | `upgradefurnace.upgrade.furnace`   | Upgrade the furnace you're looking at.  |

- **Usage**: Look at a furnace and run `/upgrade furnace`. The plugin checks your inventory for the required materials and performs the upgrade if possible.
- **Max Level**: 5

## Permissions

- **`upgradefurnace.upgrade.furnace`**: Allows the use of the `/upgrade furnace` command (default: OP).

## Events & Effects

- **FurnaceStartSmeltEvent**: Reduces cook time based on level.
- **FurnaceSmeltEvent**: Applies bonus yield at max level.
- **Particles**: Custom swirl effect after upgrade, and smelt particles at furnace front.

## Development & Contribution

1. Fork the repository.
2. Clone your fork and create a feature branch.
3. Implement changes and update the README if needed.
4. Submit a pull request describing your changes.

## License

This plugin is released under the GPL License. See [LICENSE](LICENSE) for details.

---
*Made with ❤️ by LowdFX*

