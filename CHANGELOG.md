# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-08-10

### Fixed
- Fixed compile error in `KitService.java`: added missing `import com.mpessentials.repositories.CooldownRepository;` which caused "cannot find symbol" errors on the `cooldownRepo` field and constructor parameter (build was broken, now compiles).

### Added
- MPEssentials 1.0.0 — Paper 1.21 (api-version 1.21) essentials-style plugin targeting Java 21, built with Gradle Kotlin DSL.
- Core features: player homes (/home, /sethome, /delhome), warps (/warp, /setwarp, /delwarp), teleport requests (/tpa, /tpahere, /tpaccept, /tpdeny), kits (/kit, /givekit), spawn (/spawn, /setspawn).
- SQLite persistence via sqlite-jdbc with repositories for homes, warps, kits, and cooldowns (SQLite*Repository classes).
- Configurable messages through config.yml (MiniMessage format).
- Plugin metadata in plugin.yml with 14 commands, aliases, and permission nodes incl. a parent `mpessentials.admin` node.

### Notes
- The plugin jar (MPEssentials-1.0.0.jar) is a fat jar bundling sqlite-jdbc 3.45.1.0.