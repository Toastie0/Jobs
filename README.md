# JobsFabric - Minecraft Fabric Mod

A comprehensive serverside jobs system with progression, economy rewards, and customizable tasks for Minecraft 1.20.1 Fabric servers.

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-green.svg)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-0.18.3+-blue.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-CC0--1.0-blue.svg)](LICENSE)

## 🌟 Features

- **Multiple Jobs System**: Players can have up to 3 active jobs simultaneously
- **Progressive Leveling**: Custom formula-based progression system with configurable requirements
- **Economy Integration**: Seamless Impactor Economy integration for monetary rewards
- **Modded Content Support**: Automatically compatible with any mod - no configuration needed
- **Serverside Only**: No client-side mod required - works with vanilla clients
- **Interactive GUIs**: Server-side GUI menus for job management and progress tracking
- **Performance Optimized**: Formula caching, dirty tracking, and automatic cleanup

## 📋 Available Jobs

### Phase 1 (Implemented)
- **⛏️ Miner**: Break ores and stones to level up
- **🌾 Farmer**: Harvest crops and plants
- **🏗️ Builder**: Place blocks to earn progress

### Phase 2 (Planned)
17+ additional jobs including Fighter, Fisherman, Hunter, Tamer, and more!

## 🔧 Dependencies

### Required
- **Minecraft**: 1.20.1
- **Fabric Loader**: 0.18.3+
- **Fabric API**: 0.92.6+1.20.1
- **Java**: 21+
- **Impactor Economy**: 5.3.0+

### Bundled (Included)
- SgUi 1.2.2+1.20 (server-side GUIs)
- Fabric Permissions API 0.2-SNAPSHOT

## 📥 Installation

1. Download the latest release: `JobsFabric-0.1-1.20.1.jar`
2. Place in your server's `mods/` folder
3. Install Fabric API (0.92.6+1.20.1) and Impactor Economy (5.3.0+)
4. Start your server - configs generate automatically in `config/jobs/`

## ⚙️ Configuration

Configuration files are located in `config/jobs/`:

### Main Config (`config.json`)
```json
{
  "jobs": {
    "maxActiveJobs": 3,
    "leaveCooldownMinutes": 15,
    "saveProgressOnLeave": true
  },
  "storage": {
    "autoSaveIntervalSeconds": 180
  },
  "autoDetection": {
    "enabled": true,
    "defaultOreProgress": 1.0,
    "defaultCropProgress": 1.0
  }
}
```

### Job Definitions (`jobs/*.json`)
Each job has its own JSON configuration with:
- **Progress Formula**: Custom expressions (e.g., `10 * (%level% ^ 2)`)
- **Action Types**: `break`, `harvest`, `place`
- **Special Progress**: Material-specific multipliers (optional)
- **Rewards**: Economy rewards linked to rewards.json
- **GUI Configuration**: Custom icons and descriptions

Example:
```json
{
  "id": "miner",
  "name": "§6§lMiner",
  "requiredProgressFormula": "2 * (%level% ^ 2)",
  "actionType": "break",
  "defaultRewards": ["1"],
  "specialProgress": {
    "diamond_ore": 5.0,
    "coal_ore": 1.0
  }
}
```

### Rewards System (`rewards.json`)
Define economy rewards with formulas:
```json
{
  "1": [
    {
      "type": "command",
      "name": "$%value%",
      "variables": {
        "value": "(3 * (5 * (%level% ^ 2)))"
      },
      "commands": ["deposit %value%"]
    }
  ]
}
```

## 🎮 Usage

### Player Commands
- `/jobs` - Open the jobs menu GUI to join/leave jobs and view progress

### Admin Commands
- `/jobsadmin reload` - Reload configuration files
- `/jobsadmin save` - Force save all player data
- `/jobsadmin test` - Test economy integration
- `/jobsadmin reset <player> <job>` - Reset specific job progress
- `/jobsadmin resetall <player>` - Reset all jobs for a player
- `/jobsadmin info` - View loaded jobs and config info

## 🚀 Performance

- **Formula Caching**: 50-80% reduction in calculations
- **Dirty Tracking**: Only saves modified player data
- **Async Economy**: Non-blocking transactions
- **Auto-Cleanup**: Memory management with time-based expiry

## 🧩 Modded Content Support

Automatically works with ANY Fabric mod - no configuration needed:
- **Miner**: Detects any block with `_ore` in the name
- **Farmer**: Detects any block extending CropBlock
- **Builder**: Accepts all placeable blocks

Server owners can optionally customize rewards for specific modded blocks in job JSON files.

## 🏗️ Development

### Building from Source

```bash
# Clone the repository
git clone https://github.com/Toastie0/Jobs.git
cd Jobs

# Build with Gradle
./gradlew build

# Output: build/libs/JobsFabric-0.1-1.20.1.jar
```

### Project Structure
```
src/main/java/net/advancedjobs/
├── AdvancedJobsMod.java          # Main mod entry point
├── config/
│   └── ConfigManager.java         # Configuration loader
├── controller/
│   └── JobController.java         # Core job logic
├── economy/
│   └── ImpactorEconomyHandler.java
├── gui/
│   ├── JobListMenu.java           # Main jobs GUI
│   └── JobProgressMenu.java       # Job details GUI
├── handlers/
│   ├── BlockBreakHandler.java     # Miner job
│   ├── CropHarvestHandler.java    # Farmer job
│   └── BlockPlaceHandler.java     # Builder job
├── objects/
│   ├── Job.java                   # Job data model
│   ├── User.java                  # Player data model
│   └── UserJobInfo.java           # Job progress data
└── storage/
    └── JsonStorageManager.java    # JSON persistence
```

### Tech Stack
- **Language**: Java 21
- **Build Tool**: Gradle 8.8
- **Mod Loader**: Fabric
- **GUI Library**: SgUi (server-side)
- **Economy**: Impactor API
- **Storage**: JSON with dirty tracking

## 📊 Roadmap

### Phase 1: Core System ✅ **COMPLETE**
- [x] Job framework with progression
- [x] Economy integration (Impactor)
- [x] Three jobs (Miner, Farmer, Builder)
- [x] Interactive GUIs
- [x] Admin commands
- [x] Performance optimizations
- [x] Modded content support

### Phase 2: Expansion 📋
- [ ] 17+ additional jobs (Fighter, Fisherman, Hunter, Tamer, etc.)
- [ ] Job-specific achievements
- [ ] Leaderboards
- [ ] Boosters & multipliers

### Phase 3: Advanced Features 🔮
- [ ] Permission integration
- [ ] Item rewards
- [ ] Job quests/milestones
- [ ] Statistics dashboard

## 🤝 Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## 📄 License

This project is licensed under the CC0 1.0 Universal (Public Domain) License - see the [LICENSE](LICENSE) file for details.

This means you can use, modify, and distribute this mod freely without any restrictions or attribution requirements.

## 👤 Author

**Toastie**
- GitHub: [@Toastie0](https://github.com/Toastie0)

## 🙏 Acknowledgments

- **Fabric Team**: For the excellent modding framework
- **Impactor**: For the economy API
- **SgUi**: For server-side GUI capabilities
- **Minecraft Modding Community**: For resources and support

## 📞 Support

For issues, questions, or suggestions:
- Open an issue on [GitHub Issues](https://github.com/Toastie0/Jobs/issues)

---

**Note**: This mod is serverside only. Players with vanilla Minecraft clients can join and use all features without installing anything.
