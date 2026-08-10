## Overview

This plugin adds essential commands for Survival Multiplayer servers.
It uses SQLite for data storage. All database operations run async.

## Requirements

- Paper 1.21 or higher
- Java 21

## Installation

1. Download the plugin JAR file.
2. Place the JAR in your `plugins` folder.
3. Start the server.
4. Stop the server.
5. Edit `config.yml` and `messages.yml`.
6. Start the server again.

## Commands

### Player Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/home` | `smp.home` | Teleport to your home. |
| `/home <name>` | `smp.home` | Teleport to a named home. |
| `/sethome <name>` | `smp.sethome` | Set a new home location. |
| `/tpa <player>` | `smp.tpa` | Request teleport to a player. |
| `/tpahere <player>` | `smp.tpahere` | Request a player to teleport to you. |
| `/warp <name>` | `smp.warp` | Teleport to a warp point. |
| `/kit <name>` | `smp.kit` | Claim an item kit. |
| `/spawn` | `smp.spawn` | Teleport to the spawn point. |

### Admin Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/setwarp <name>` | `smp.admin.warp` | Create a new warp point. |
| `/delwarp <name>` | `smp.admin.warp` | Delete a warp point. |
| `/setkit <name>` | `smp.admin.kit` | Create a new item kit. |
| `/delkit <name>` | `smp.admin.kit` | Delete an item kit. |
| `/setspawn` | `smp.admin.spawn` | Set the server spawn point. |
| `/reload` | `smp.admin.reload` | Reload plugin configuration. |

## Configuration

### config.yml

Edit `config.yml` to change plugin behavior.

```yaml
homes:
  max-homes: 3
  cooldown-seconds: 30

teleport:
  delay-seconds: 3
  warmup-message: "Teleporting in {seconds} seconds..."

warps:
  enabled: true

kits:
  enabled: true
  cooldown-minutes: 60

spawn:
  enabled: true
  force-spawn-on-join: false
```

### messages.yml

Edit `messages.yml` to change player messages.
The plugin uses Adventure MiniMessage format.

```yaml
prefix: "<gray>[<gold>SMP<gray>] </gold>"

home:
  set: "<green>Home '{name}' set at your location.</green>"
  limit: "<red>You reached your home limit ({max}).</red>"
  not-found: "<red>Home '{name}' does not exist.</red>"
  teleported: "<green>Teleported to home '{name}'.</green>"

tpa:
  sent: "<green>Teleport request sent to {player}.</green>"
  received: "<green>{player} wants to teleport to you.</green>"
  accepted: "<green>Teleport request accepted.</green>"
  denied: "<red>Teleport request denied.</red>"
  expired: "<red>Teleport request expired.</red>"

warp:
  teleported: "<green>Teleported to warp '{name}'.</green>"
  not-found: "<red>Warp '{name}' does not exist.</red>"

kit:
  claimed: "<green>Kit '{name}' claimed!</green>"
  cooldown: "<red>Kit on cooldown. Wait {minutes} minutes.</red>"
  not-found: "<red>Kit '{name}' does not exist.</red>"

spawn:
  teleported: "<green>Teleported to spawn.</green>"

error:
  no-permission: "<red>You lack permission for this command.</red>"
  player-not-found: "<red>Player '{player}' not found.</red>"
  console-only: "<red>Only players can use this command.</red>"
```

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `smp.home` | true | Use home commands. |
| `smp.sethome` | true | Set home locations. |
| `smp.tpa` | true | Send teleport requests. |
| `smp.tpahere` | true | Receive teleport requests. |
| `smp.warp` | true | Use warp points. |
| `smp.kit` | true | Claim item kits. |
| `smp.spawn` | true | Teleport to spawn. |
| `smp.admin.warp` | op | Manage warp points. |
| `smp.admin.kit` | op | Manage item kits. |
| `smp.admin.spawn` | op | Set server spawn. |
| `smp.admin.reload` | op | Reload plugin config. |

## Data Storage

The plugin stores data in SQLite.
The database file is `plugins/PaperSMPEssentials/data.db`.
Data persists after server restarts.

## Troubleshooting

### Plugin fails to load

- Check Java version. Use Java 21.
- Check Paper version. Use Paper 1.21+.
- Check console for error messages.

### Commands do not work

- Check player permissions.
- Check if commands are disabled in `config.yml`.
- Reload the plugin with `/reload`.

### Data does not save

- Check file permissions on the `data.db` file.
- Check disk space on the server.
- Review console logs for SQL errors.

## Support

Report issues on the project repository.
Include server logs and steps to reproduce.

## License

This project is open source.
Check the LICENSE file for details.
