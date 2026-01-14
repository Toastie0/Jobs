# Jobs - Minecraft Fabric Mod

A comprehensive serverside jobs system with progression, economy rewards, and customizable tasks for Minecraft 1.20.1 Fabric servers.

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-green.svg)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-0.18.3+-blue.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 🌟 Features

- **Multiple Jobs System**: Players can have up to 3 active jobs simultaneously
- **Progressive Leveling**: Custom formula-based progression system with configurable requirements
- **Economy Integration**: Integrates with Impactor Economy for monetary rewards
- **Anti-Exploit Protection**: Prevents farming player-placed blocks and bonemeal abuse
- **Modded Content Support**: Fully compatible with modded blocks and items
- **Serverside Only**: No client-side mod required - works with vanilla clients
- **Customizable Jobs**: JSON-based configuration for easy job creation and modification
- **Interactive GUIs**: Server-side GUI menus for job management and progress tracking

## 📋 Available Jobs

### Phase 1 (Implemented)
- **⛏️ Miner**: Break ores and stones to level up
- **🌾 Farmer**: Harvest crops and plants
- **🏗️ Builder**: Place blocks to earn progress

### Phase 2 (Planned)
17+ additional jobs including Fighter, Fisherman, Hunter, Tamer, and more!

## 🔧 Dependencies

### Required (Must Install Separately)
- **Minecraft**: 1.20.1
- **Fabric Loader**: 0.18.3 or higher
- **Fabric API**: 0.92.6+1.20.1
- **Java**: 21 or higher
- **Impactor Economy**: 5.3.0+ (for economy rewards)

### Bundled (Already Included in the Mod)
- **SgUi**: 1.2.2+1.20 (for server-side GUIs) - *No separate installation needed*
- **Fabric Permissions API**: 0.2-SNAPSHOT - *No separate installation needed*

> **Note**: These libraries are bundled for convenience. If you experience conflicts with other mods using these libraries, please report the issue on GitHub.

### Optional
- **LuckPerms**: For permission-based features (planned)

## 📥 Installation

1. Download the latest release: `Jobs-0.1-1.20.1.jar`
2. Place in your server's `mods/` folder
3. Install required dependencies:
   - Fabric API (0.92.6+1.20.1)
   - Impactor Economy (5.3.0+)
   - *(SgUi and Fabric Permissions API are already bundled inside the mod)*
4. Start your server
5. Configure jobs in `config/jobs/`

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
  }
}
```

### Job Definitions (`jobs/*.json`)
Each job has its own JSON configuration file with:
- **Progress Formula**: Custom mathematical expressions (e.g., `100 * (level^2)`)
- **Action Types**: `break`, `harvest`, `place`, etc.
- **Special Progress**: Material-specific multipliers
- **Rewards**: Economy rewards per level
- **GUI Configuration**: Custom icons and descriptions

Example job structure:
```json
{
  "id": "miner",
  "name": "&6&lMiner",
  "progressFormula": "100 * (level^2)",
  "actionType": "break",
  "specialProgress": {
    "_ore": 5.0,
    "stone": 1.0
  }
}
```

## 🎮 Usage

### Player Commands
- `/jobs` - Open the jobs menu (GUI)
- Join jobs by clicking in the GUI
- View progress and statistics
- Leave jobs with progress saved

### Admin Commands
*(Coming in Phase 2)*

## 🚀 Performance Optimizations

This mod includes several performance enhancements:
- **Formula Caching**: 50-80% reduction in mathematical calculations
- **Memory Management**: Automatic cleanup of tracking data every 5 minutes
- **Dirty Tracking**: Only saves modified player data
- **Async Operations**: Economy transactions run asynchronously
- **Time-Based Expiry**: Old entries auto-expire after 10 minutes

See [OPTIMIZATION_REPORT.md](OPTIMIZATION_REPORT.md) for detailed analysis.

## 🔒 Anti-Exploit Features

- **Block Placement Tracking**: Player-placed blocks don't give mining progress
- **Bonemeal Cooldown**: 1-second cooldown prevents bonemeal farming
- **Timestamp Tracking**: Automatic cleanup prevents memory leaks
- **Validation System**: Action-type whitelists prevent invalid progress

## 🧩 Modded Content Support

The mod fully supports modded blocks and items:
- Uses full resource identifiers (e.g., `modname:copper_ore`)
- Wildcard support (e.g., `_ore` matches all ores)
- Automatic compatibility with any mod
- No additional configuration required

See [MODDED_CONTENT_SUPPORT.md](MODDED_CONTENT_SUPPORT.md) for details.

## 🏗️ Development

### Building from Source

```bash
# Clone the repository
git clone https://github.com/Toastie0/Jobs.git
cd Jobs

# Build with Gradle
./gradlew build

# Output: build/libs/Jobs-0.1-1.20.1.jar
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

### Phase 1: Core System ✅
- [x] Job framework
- [x] Progress tracking
- [x] Economy integration
- [x] Basic jobs (Miner, Farmer, Builder)
- [x] Anti-exploit systems
- [x] Performance optimizations

### Phase 2: Expansion 🚧
- [ ] 17+ additional jobs
- [ ] Job-specific achievements
- [ ] Leaderboards
- [ ] Boosters & multipliers
- [ ] Permission integration

### Phase 3: Advanced Features 📋
- [ ] Custom job creation GUI
- [ ] Job rewards (items, commands)
- [ ] Job quests/milestones
- [ ] Statistics dashboard
- [ ] API for developers

## 🤝 Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

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
