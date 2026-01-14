# AdvancedJobs Fabric 1.20.1 Port - Information Requirements

Please fill out this document with the requested information. This will help us create an accurate and efficient port of AdvancedJobs to Fabric 1.20.1.

---

## 1. ECONOMY SYSTEM

### Impactor Economy Details
**Economy Mod Name:** 
Impactor - NickImpact

**Mod Link/Source:** 
https://github.com/NickImpact/Impactor

**Does it have a Fabric 1.20.1 version?**
- [✓] Yes
- [ ] No (we'll need to create a wrapper)

**Economy command structure:**
I'm not sure but I brought the impactor source code in, and the github has all the information
```
Give money command: 

Take money command: 

Check balance command: 
```

**Does it have an API we can use?**
I believe this is the API:

repositories {
    maven("https://maven.impactdev.net/repository/development")
}

dependencies {
    implementation("net.impactdev.impactor.api:economy:5.3.0")
}

---

## 2. CURRENT SERVER SETUP

### Fabric Mods Currently Installed
I brought in all the mods installed on my server. They are all in the folder "server mods"
the main economy mods installed are. GUISHOP, AuctionHouse, Impactor

### Permissions System
**Which permissions mod are you using?**
Im using LuckPerms and Fabric Permissions

### Region Protection
No I do not have any region claim mods

**Do you need job restrictions by region/world?**
not needed

### Server Type
single fabric server

---

## 3. FEATURE PRIORITIES

### Jobs to Implement
**Which jobs do your players actually use?** (Check all that apply)
- [✓] Miner
- [✓] Lumberjack
- [✓] Farmer
- [✓] Builder
- [✓] Fisher
- [✓] Hunter
- [✓] Fighter
- [✓] Breeder
- [✓] Crafter
- [✓] Smelter
- [✓] Enchanter
- [✓] Brewer
- [✓] Beekeeper
- [✓] Tamer
- [✓] Swimmer
- [✓] Runner
- [✓] Glider
- [✓] Explorer
- [✓] Gourmet
- [✓] Custom Enchanter

**Priority Level:**
- **Phase 1 (Must Have First):** 
It doesn't matter to me, Let's do a few at a time but I want to port over all the jobs in the end

- **Phase 2 (Can Add Later):** 
  <!-- List jobs that can be added after core functionality -->

- **Phase 3 (Nice to Have):** 
  <!-- List jobs that are rarely used -->

### External Plugin Hooks
**Do you currently use any of these plugin integrations?**

I am not using Spigot, I want to create this for fabric.

- [ ] PlaceholderAPI - **Usage:** _____________ (e.g., "for scoreboard display")
- [ ] MMOCore
- [ ] MMOItems
- [ ] mcMMO
- [ ] MythicMobs
- [ ] Citizens
- [ ] Factions
- [ ] Lands
- [ ] Clans
- [ ] ASkyBlock / SuperiorSkyblock
- [ ] Other: _______________

**Are these integrations critical?**
- [ ] Yes, absolutely needed
- [ ] Nice to have, but can live without
- [✓] Not using any plugin hooks

### Premium System
**Do you use the Premium/Free job split?**
- [ ] Yes, keep it
- [✓] No, make all jobs free
- [ ] Undecided

---

## 4. CONFIGURATION PREFERENCES

### Config Format
- [ ] Keep YAML (requires library)
- [✓] Switch to JSON (native Fabric support)
- [ ] Switch to HOCON (Configurate library)
- [ ] No preference

### Storage Method
**How do you want to store player data?**
- [ ] MySQL/MariaDB
  - Already have database? [ ] Yes [ ] No
  - Same database as Spigot version? [ ] Yes [ ] No
- [ ] SQLite (single file database)
- [✓] JSON files (current system)
- [ ] No preference

### Current Customizations
**Have you customized the default config files?**
- [ ] Yes, heavily customized
- [ ] Minor changes
- [✓] Using defaults

**If customized, which aspects?**
- [ ] Job progression formulas
- [ ] Reward amounts
- [ ] Level requirements
- [ ] Menu layouts
- [ ] Messages/translations
- [ ] Other: _______________

---

## 5. FEATURES & FUNCTIONALITY

### GUI Menus
**Menu preferences:**
- [✓] Keep all current menus (Portal, Free, Premium, Progress, Leaderboard)
- [ ] Simplified menus
- [ ] Command-based only (no GUIs)

### Leaderboard System
- [✓] Need leaderboards (Points + Per-Job levels)
- [ ] Points leaderboard only
- [ ] Not needed

### Booster System
- [✓] Keep boosters (Progress, Rewards, Points multipliers)
- [ ] Not needed
- [ ] Undecided

### Daily Reset System
**Do you use daily job resets?**
- [ ] Yes, keep it
- [ ] No, jobs should be permanent
- [✓] Undecided

**If yes, current reset time:** _____ (e.g., "00:00 UTC", "midnight EST")

### Anti-Exploit Features
**Which protections do you need?**
- [ ] Block break protection (prevent quest farming placed blocks)
- [ ] Brewing protection (prevent potion reuse)
- [ ] Region restrictions
- [ ] World restrictions
- [✓] All of the above

---

## 6. PLACEHOLDERS & INTEGRATIONS

### Placeholder Usage
**Where do you currently use AdvancedJobs placeholders?**
- [ ] Scoreboard
- [ ] Chat/Tab list
- [ ] Holograms
- [ ] Other plugins
- [✓] Not using placeholders

**If using placeholders, list the specific ones:**
```
Example: %advancedjobs_job_miner_level%

1. 
2. 
3. 
```

### Statistics & Metrics
- [ ] Keep bStats integration (anonymous usage statistics)
- [✓] Remove it

---

## 7. LANGUAGE & LOCALIZATION

**Which language(s) do you need?**
- [ ] English only
- [✓] Multiple languages: All avaliable
  - Available: English, Spanish, French, German, Dutch, Danish, Korean, Chinese

---

## 8. PERFORMANCE & SCALE

### Server Size
**Typical player count:**
- [✓] Small (1-20 players)
- [ ] Medium (20-50 players)
- [ ] Large (50-100 players)
- [ ] Very Large (100+ players)

**Concurrent job workers at peak:**
- Estimate: 5 players actively working jobs at once

### Performance Priorities
- [ ] Optimize for low RAM usage
- [ ] Optimize for low CPU usage
- [ ] Optimize for fast database queries
- [✓] Balanced approach

---

## 9. DEVELOPMENT APPROACH

### Implementation Strategy
**Preferred approach:**
- [✓] **Proof-of-Concept First** - Get one job type working end-to-end (Miner), then expand
- [ ] **Full Architecture First** - Build complete framework, then add job types
- [ ] **Your recommendation** - You decide the best approach

### Testing Availability
**Can you test on your server during development?**
- [✓] Yes, I can test frequently
- [ ] Yes, but only occasionally
- [ ] No, build it complete first

---

## 10. ADDITIONAL INFORMATION

### Custom Requirements
**Any specific features or behaviors unique to your server?**
```
Example: "Players should earn 2x rewards on weekends"


```

### Known Issues with Current System
**Any bugs or limitations in the Spigot version you want fixed?**
```
I am not going to use this for spigot. I want to use this for fabric 1.20.1

```

### Nice-to-Have Features
**Any new features you'd like added that don't exist in Spigot version?**
```


```

### Timeline
**Do you have a deadline or target date?**
- Target date: _______________
- [✓] No rush, quality over speed
- [ ] ASAP

---

## 11. TECHNICAL DETAILS (Optional)

### Development Environment
**If you want to compile/modify the mod yourself:**
- [ ] I have Java development environment set up
- [ ] I'll just use the compiled JAR
- [ ] I want to learn how to modify it

**Java version installed:**
- [ ] Java 17 (required for Minecraft 1.20.1)
- [✓] Other: 21
- [ ] Unknown

---

## SUBMISSION

Once completed, save this file and let me know. I'll use this information to create your Fabric port!

**Any questions or concerns before we start?**
```
CONFIRMED: Ready to start development
```

---
---

# DETAILED IMPLEMENTATION PLAN FOR CLAUDE

## PROJECT OVERVIEW
**Project:** AdvancedJobs Fabric 1.20.1 Port
**Source:** Spigot plugin decompiled code in `c:\Users\Cristian\Documents\Code\VSCODE\JobsMod\`
**Target Location:** `c:\Users\Cristian\Documents\Code\VSCODE\JobsMod\jobsfabricmod-template-1.20.1\`
**Template Status:** ✅ Ready to use - Fabric 1.20.1 template with Fabric API

---

## KEY DECISIONS MADE

### ✅ CONFIRMED FEATURES
1. **NO Daily Reset System** - All jobs permanently available, no rotation
2. **NO Premium/Free Split** - All jobs accessible to everyone
3. **JSON Configuration** - Using Gson (already in Minecraft)
4. **JSON Storage** - Player data saved as JSON files
5. **Server-Side Only** - No client mod required
6. **All 20 Jobs** - Implement gradually (3 in Phase 1, rest in Phase 2)
7. **Anti-Exploit Protections** - Block break + brewing protection
8. **All 8 Languages** - Full localization support
9. **Booster System** - Progress/Rewards/Points multipliers
10. **Leaderboard System** - Points + per-job levels

### ❌ EXCLUDED FEATURES
1. No bStats telemetry
2. No PlaceholderAPI integration
3. No region/world restrictions
4. No external plugin hooks (MMOCore, Factions, etc.)
5. No Bungee/Velocity multi-server support

---

## ECONOMY INTEGRATION - IMPACTOR

### API Details
**Maven Dependency:**
```gradle
repositories {
    maven { url "https://maven.impactdev.net/repository/development" }
}
dependencies {
    modImplementation "net.impactdev.impactor.api:economy:5.3.0"
}
```

### Usage Pattern (from source analysis)
```java
// Get economy service
EconomyService service = Impactor.instance().services().provide(EconomyService.class);

// Get player account
Currency currency = service.currencies().primary(); // Default currency
CompletableFuture<Account> accountFuture = service.account(currency, playerUUID);

// Transactions
Account account = accountFuture.join();
account.deposit(BigDecimal.valueOf(amount));      // Give money
account.withdraw(BigDecimal.valueOf(amount));     // Take money  
BigDecimal balance = account.balance();           // Check balance
```

**Implementation File:** `src/main/java/com/toastie/economy/ImpactorEconomyHandler.java`

---

## ARCHITECTURE DESIGN

### Core Package Structure
```
src/main/java/com/toastie/
├── JobsFabricMod.java (main entry point)
├── config/
│   ├── ConfigManager.java (loads/saves JSON configs)
│   ├── MainConfig.java (main settings)
│   ├── RewardsConfig.java (reward definitions)
│   └── LanguageConfig.java (localization)
├── storage/
│   ├── JsonStorageManager.java (player data I/O)
│   ├── UserDataStorage.java (user CRUD operations)
│   └── LeaderboardStorage.java (top players cache)
├── objects/
│   ├── User.java (player data model)
│   ├── Job.java (job definition)
│   ├── UserJobInfo.java (player's job progress)
│   ├── Reward.java (reward definition)
│   └── Booster.java (multiplier data)
├── economy/
│   └── ImpactorEconomyHandler.java (Impactor integration)
├── events/
│   ├── EventRegistry.java (registers all handlers)
│   ├── BlockBreakHandler.java (mining jobs)
│   ├── BlockPlaceHandler.java (builder, break protection)
│   ├── CropHarvestHandler.java (farmer)
│   ├── EntityKillHandler.java (hunter, fighter)
│   ├── FishingHandler.java (fisherman)
│   ├── CraftingHandler.java (crafter)
│   ├── SmeltingHandler.java (smelter)
│   ├── BrewingHandler.java (brewer + brewing protection)
│   ├── EnchantingHandler.java (enchanter)
│   ├── AnimalBreedHandler.java (breeder)
│   ├── AnimalTameHandler.java (tamer)
│   └── MovementHandlers.java (swimmer, runner, glider)
├── jobs/
│   ├── JobManager.java (load jobs, validate, cache)
│   ├── JobController.java (join/leave/progress logic)
│   ├── ProgressTracker.java (track & calculate progress)
│   └── RewardDistributor.java (give rewards, call economy)
├── gui/
│   ├── MenuManager.java (GUI factory)
│   ├── JobListMenu.java (browse jobs)
│   ├── JobProgressMenu.java (individual job details)
│   ├── LeaderboardMenu.java (top players)
│   └── PortalMenu.java (main hub)
├── commands/
│   ├── JobsCommand.java (player commands)
│   └── JobsAdminCommand.java (admin commands)
├── leaderboard/
│   ├── LeaderboardManager.java (calculate rankings)
│   └── LeaderboardCache.java (cache top players)
├── boosters/
│   ├── BoosterManager.java (manage active boosters)
│   └── BoosterCalculator.java (apply multipliers)
├── protection/
│   ├── BlockTracker.java (track player-placed blocks)
│   └── BrewingTracker.java (NBT tags on potions)
└── util/
    ├── TextFormatter.java (color codes, placeholders)
    ├── ProgressCalculator.java (formula evaluation)
    └── TimeFormatter.java (format durations)
```

### Resource Structure
```
src/main/resources/
├── fabric.mod.json (mod metadata)
├── config/
│   ├── config.json (main settings)
│   ├── rewards.json (reward definitions)
│   └── jobs/
│       ├── miner.json
│       ├── farmer.json
│       ├── builder.json
│       └── ... (17 more)
├── lang/
│   ├── en.json
│   ├── es.json
│   ├── fr.json
│   ├── de.json
│   ├── nl.json
│   ├── da.json
│   ├── ko.json
│   └── zh_cn.json
└── data/
    └── advancedjobs/
        └── users/ (player data saved here at runtime)
```

---

## PHASE 1: PROOF-OF-CONCEPT (3 Jobs Working)

### Goal
Get Miner, Farmer, and Builder jobs fully functional with economy rewards and GUI

### Files to Create/Modify

#### 1. Update `build.gradle`
```gradle
repositories {
    maven { url "https://maven.impactdev.net/repository/development" }
    maven { url "https://maven.fabricmc.net/" }
}

dependencies {
    // ... existing ...
    modImplementation "net.impactdev.impactor.api:economy:5.3.0"
    
    // Fabric Permissions API
    modImplementation include("me.lucko:fabric-permissions-api:0.2-SNAPSHOT")
}
```

#### 2. Update `fabric.mod.json`
```json
{
  "id": "advancedjobs",
  "name": "AdvancedJobs",
  "description": "Jobs system with progression and economy rewards",
  "version": "${version}",
  "environment": "server",
  "entrypoints": {
    "main": ["com.toastie.JobsFabricMod"],
    "server": ["com.toastie.JobsFabricMod"]
  },
  "depends": {
    "fabricloader": ">=0.18.3",
    "minecraft": "~1.20.1",
    "fabric-api": "*",
    "impactor": "*"
  }
}
```

#### 3. Core Object Models

**User.java**
```java
public class User {
    private UUID uuid;
    private Map<String, UserJobInfo> activeJobs;
    private double points;
    
    // Methods: addJob, removeJob, addPoints, getProgress, etc.
}
```

**UserJobInfo.java**
```java
public class UserJobInfo {
    private String jobId;
    private int level;
    private BigDecimal progress;
    private BigDecimal requiredProgress;
    private boolean active;
    
    // Methods: addProgress, levelUp, reset, etc.
}
```

**Job.java**
```java
public class Job {
    private String id;
    private String name;
    private String actionType; // "block-break", "harvest-crops", etc.
    private String progressFormula; // "2 * (%level% ^ 2)"
    private String pointsFormula;
    private Map<String, Double> specialProgress; // material -> multiplier
    private List<String> defaultRewards;
    private Map<Integer, List<String>> levelRewards;
    private GuiItemConfig guiItem;
    
    // Methods: calculateProgress, calculatePoints, getRewards, etc.
}
```

#### 4. Configuration System

**ConfigManager.java** - Load JSON configs from resources
```java
public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    public MainConfig loadMainConfig();
    public RewardsConfig loadRewardsConfig();
    public Map<String, Job> loadJobs();
    public Map<String, JsonObject> loadLanguages();
}
```

**Convert YAML configs to JSON:**
- `config.yml` → `config.json`
- `rewards.yml` → `rewards.json`
- `jobs/*.yml` → `jobs/*.json`
- `lang/*.yml` → `lang/*.json`

#### 5. Storage System

**JsonStorageManager.java**
```java
public class JsonStorageManager {
    private Path dataDirectory; // config/advancedjobs/users/
    
    public void saveUser(User user);
    public Optional<User> loadUser(UUID uuid);
    public void saveLeaderboard(LeaderboardData data);
    
    // Auto-save every 180 seconds (config setting)
}
```

#### 6. Economy Integration

**ImpactorEconomyHandler.java**
```java
public class ImpactorEconomyHandler {
    private EconomyService service;
    private Currency currency;
    
    public void init() {
        this.service = Impactor.instance().services().provide(EconomyService.class);
        this.currency = service.currencies().primary();
    }
    
    public CompletableFuture<Boolean> giveMoney(UUID player, double amount);
    public CompletableFuture<Double> getBalance(UUID player);
}
```

#### 7. Event Handlers (Phase 1 - 3 Jobs)

**BlockBreakHandler.java** (Miner)
```java
public class BlockBreakHandler {
    @EventHandler
    public void onBlockBreak(PlayerBlockBreakEvents.After event) {
        ServerPlayerEntity player = event.getPlayer();
        BlockState state = event.getState();
        
        // Check if block is player-placed (protection)
        if (blockTracker.isPlayerPlaced(pos)) return;
        
        // Check if player has Miner job active
        User user = userCache.get(player.getUuid());
        if (!user.hasActiveJob("miner")) return;
        
        // Check if material matches job requirements
        String material = Registry.BLOCK.getId(state.getBlock()).toString();
        Job minerJob = jobManager.getJob("miner");
        
        // Calculate progress
        double progress = minerJob.calculateProgress(material, user.getJobLevel("miner"));
        
        // Add progress
        jobController.addProgress(user, "miner", progress);
    }
}
```

**CropHarvestHandler.java** (Farmer)
```java
public class CropHarvestHandler {
    // Hook into block break + check if crop is mature
    // Use UseBlockCallback for right-click harvesting
}
```

**BlockPlaceHandler.java** (Builder + Break Protection)
```java
public class BlockPlaceHandler {
    @EventHandler
    public void onBlockPlace(PlayerBlockBreakEvents.Before event) {
        // Track block placement for protection
        blockTracker.markPlayerPlaced(pos, player.getUuid());
        
        // Count toward Builder job progress
        User user = userCache.get(player.getUuid());
        if (user.hasActiveJob("builder")) {
            jobController.addProgress(user, "builder", 1.0);
        }
    }
}
```

#### 8. Job Controller Logic

**JobController.java**
```java
public class JobController {
    public boolean joinJob(User user, String jobId) {
        // Check: max jobs limit (3 by default)
        // Check: required points
        // Check: permissions
        // Add job to user's active jobs
        // Fire event
        // Send message
    }
    
    public void addProgress(User user, String jobId, double amount) {
        UserJobInfo jobInfo = user.getJob(jobId);
        Job job = jobManager.getJob(jobId);
        
        // Apply booster multiplier
        double boosted = boosterManager.applyMultiplier(user, amount);
        
        jobInfo.addProgress(boosted);
        
        // Check if level up
        if (jobInfo.getProgress() >= jobInfo.getRequiredProgress()) {
            levelUp(user, jobId);
        }
        
        // Send progress notification (10%, 25%, 50%, 75%, 100%)
        notifyProgress(user, jobInfo);
    }
    
    private void levelUp(User user, String jobId) {
        UserJobInfo jobInfo = user.getJob(jobId);
        jobInfo.levelUp();
        
        // Reset progress
        jobInfo.setProgress(BigDecimal.ZERO);
        
        // Calculate new required progress
        Job job = jobManager.getJob(jobId);
        jobInfo.setRequiredProgress(job.calculateRequiredProgress(jobInfo.getLevel()));
        
        // Award points
        double points = job.calculatePoints(jobInfo.getLevel());
        user.addPoints(points);
        
        // Give rewards
        rewardDistributor.giveRewards(user, job, jobInfo.getLevel());
        
        // Fire event
        // Send messages/title/sound
    }
}
```

#### 9. Reward System

**RewardDistributor.java**
```java
public class RewardDistributor {
    public void giveRewards(User user, Job job, int level) {
        List<String> rewardIds = job.getRewardsForLevel(level);
        
        for (String rewardId : rewardIds) {
            Reward reward = rewardsConfig.getReward(rewardId);
            
            if (reward.getType().equals("command")) {
                // Parse variables (level, booster, etc.)
                String command = reward.parseCommand(user, level);
                
                // Execute command (e.g., "eco give %player% 500")
                server.getCommandManager().executeWithPrefix(
                    server.getCommandSource(),
                    command
                );
            } else if (reward.getType().equals("item")) {
                // Give items to player
                giveItems(user, reward);
            }
        }
    }
}
```

#### 10. GUI System (Using SgUi by Patbox)

**IMPORTANT:** SgUi source code available at `c:\Users\Cristian\Documents\Code\VSCODE\JobsMod\sgui-1.20\`

**JobListMenu.java** - Proper SgUi Implementation
```java
public class JobListMenu {
    public void open(ServerPlayerEntity player) {
        // Create 5-row chest GUI
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X5, player, false);
        gui.setTitle(Text.literal("§aAvailable Jobs"));
        
        // Add background/border items (glass panes)
        fillBackground(gui);
        
        List<Job> jobs = jobManager.getAllJobs();
        int[] jobSlots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24}; // Center slots
        
        for (int i = 0; i < jobs.size() && i < jobSlots.length; i++) {
            Job job = jobs.get(i);
            int slot = jobSlots[i];
            
            // Create clickable job icon using GuiElementBuilder
            GuiElementBuilder builder = createJobIcon(job, player);
            
            // Set callback for clicks
            builder.setCallback((index, type, action) -> {
                if (type.isLeft) {
                    User user = userCache.get(player.getUuid());
                    jobController.joinJob(user, job.getId());
                    gui.close();
                }
            });
            
            gui.setSlot(slot, builder.build());
        }
        
        // Add navigation buttons
        addNavigationButtons(gui);
        
        gui.open();
    }
    
    private GuiElementBuilder createJobIcon(Job job, ServerPlayerEntity player) {
        User user = userCache.get(player.getUuid());
        UserJobInfo jobInfo = user.getJob(job.getId());
        
        // Parse material from job config (e.g., "diamond_pickaxe")
        Item material = Registries.ITEM.get(new Identifier(job.getGuiItem().getMaterial()));
        
        GuiElementBuilder builder = new GuiElementBuilder(material);
        
        // Set custom name with color codes
        builder.setName(Text.literal(job.getGuiItem().getName())
            .formatted(Formatting.GREEN));
        
        // Add lore
        List<Text> lore = new ArrayList<>();
        for (String line : job.getGuiItem().getLore()) {
            // Replace placeholders
            String parsed = line
                .replace("%level%", jobInfo != null ? String.valueOf(jobInfo.getLevel()) : "1")
                .replace("%progress%", jobInfo != null ? jobInfo.getProgress().toString() : "0")
                .replace("%required_progress%", job.calculateRequiredProgress(1).toString())
                .replace("%active%", jobInfo != null && jobInfo.isActive() ? "§aYes" : "§cNo")
                .replace("&", "§"); // Color codes
            
            lore.add(Text.literal(parsed));
        }
        builder.setLore(lore);
        
        // Add glow effect if active
        if (jobInfo != null && jobInfo.isActive()) {
            builder.glow();
        }
        
        return builder;
    }
    
    private void fillBackground(SimpleGui gui) {
        GuiElementBuilder glass = new GuiElementBuilder(Items.GRAY_STAINED_GLASS_PANE)
            .setName(Text.literal("")); // Empty name
        
        // Fill all slots first
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setSlot(i, glass.build());
        }
        
        // Clear job display slots
        int[] jobSlots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24};
        for (int slot : jobSlots) {
            gui.clearSlot(slot);
        }
    }
    
    private void addNavigationButtons(SimpleGui gui) {
        // Back button
        GuiElementBuilder back = new GuiElementBuilder(Items.ARROW)
            .setName(Text.literal("§e← Back to Portal"))
            .setCallback((index, type, action) -> {
                menuManager.openPortalMenu(gui.getPlayer());
            });
        gui.setSlot(40, back.build());
    }
}
```

**PortalMenu.java** - Main hub menu
```java
public class PortalMenu {
    public void open(ServerPlayerEntity player) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);
        gui.setTitle(Text.literal("§aJobs Portal"));
        
        // Browse Jobs button
        GuiElementBuilder jobsButton = new GuiElementBuilder(Items.CHEST)
            .setName(Text.literal("§eView All Jobs"))
            .addLoreLine(Text.literal("§7Click to browse available jobs"))
            .setCallback((index, type, action) -> {
                jobListMenu.open(player);
            });
        gui.setSlot(11, jobsButton.build());
        
        // Progress button
        GuiElementBuilder progressButton = new GuiElementBuilder(Items.BOOK)
            .setName(Text.literal("§eMy Progress"))
            .addLoreLine(Text.literal("§7View your active jobs"))
            .setCallback((index, type, action) -> {
                progressMenu.open(player);
            });
        gui.setSlot(13, progressButton.build());
        
        // Leaderboard button
        GuiElementBuilder leaderboardButton = new GuiElementBuilder(Items.GOLDEN_HELMET)
            .setName(Text.literal("§eLeaderboards"))
            .addLoreLine(Text.literal("§7See top players"))
            .setCallback((index, type, action) -> {
                leaderboardMenu.open(player);
            });
        gui.setSlot(15, leaderboardButton.build());
        
        gui.open();
    }
}
```

**JobProgressMenu.java** - Individual job details with progress bar
```java
public class JobProgressMenu {
    public void open(ServerPlayerEntity player, String jobId) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);
        
        User user = userCache.get(player.getUuid());
        Job job = jobManager.getJob(jobId);
        UserJobInfo jobInfo = user.getJob(jobId);
        
        gui.setTitle(Text.literal("§a" + job.getName() + " Progress"));
        
        // Create visual progress bar using items
        int progressSlots = 7; // Slots 10-16
        double progressPercent = jobInfo.getProgress().doubleValue() / 
                                  jobInfo.getRequiredProgress().doubleValue();
        int filledSlots = (int) (progressPercent * progressSlots);
        
        for (int i = 0; i < progressSlots; i++) {
            GuiElementBuilder progressItem;
            if (i < filledSlots) {
                progressItem = new GuiElementBuilder(Items.LIME_STAINED_GLASS_PANE)
                    .setName(Text.literal("§aProgress: " + (int)(progressPercent * 100) + "%"));
            } else {
                progressItem = new GuiElementBuilder(Items.RED_STAINED_GLASS_PANE)
                    .setName(Text.literal("§cIncomplete"));
            }
            gui.setSlot(10 + i, progressItem.build());
        }
        
        // Leave job button
        GuiElementBuilder leaveButton = new GuiElementBuilder(Items.BARRIER)
            .setName(Text.literal("§cLeave Job"))
            .setCallback((index, type, action) -> {
                jobController.leaveJob(user, jobId);
                gui.close();
            });
        gui.setSlot(22, leaveButton.build());
        
        gui.open();
    }
}
```

**AnimatedGuiElement Example** - For fancy effects
```java
// Animated pickaxe icon cycling through tiers
AnimatedGuiElementBuilder animatedIcon = new AnimatedGuiElementBuilder()
    .setItem(Items.NETHERITE_PICKAXE).saveItemStack()
    .setItem(Items.DIAMOND_PICKAXE).saveItemStack()
    .setItem(Items.IRON_PICKAXE).saveItemStack()
    .setItem(Items.STONE_PICKAXE).saveItemStack()
    .setInterval(10); // Ticks between frames

gui.setSlot(slot, animatedIcon.build());
```

**Dependency:** Add SgUi library for GUI management
```gradle
repositories {
    maven { url 'https://maven.nucleoid.xyz' }
}

dependencies {
    // Latest 1.20.1 version
    modImplementation include("eu.pb4:sgui:1.2.2+1.20.1")
}
```

**Key SgUi Features to Use:**
- `SimpleGui` - Standard chest GUIs
- `GuiElementBuilder` - Build items with name, lore, callbacks
- `AnimatedGuiElement` - Animated icons
- `ClickType` - Detect left/right/shift clicks
- `gui.setAutoUpdate(true)` - Auto-refresh on tick
- `gui.onTick()` - Override for real-time updates
- `gui.canPlayerClose()` - Prevent closing
- Pagination support built-in

#### 11. Commands

**JobsCommand.java** - Using Brigadier
```java
public class JobsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("jobs")
            .executes(ctx -> {
                // Open jobs GUI
                ServerPlayerEntity player = ctx.getSource().getPlayer();
                menuManager.openPortalMenu(player);
                return 1;
            })
            .then(CommandManager.literal("join")
                .then(CommandManager.argument("job", StringArgumentType.string())
                    .suggests(/* job suggestions */)
                    .executes(ctx -> {
                        // Join job command
                        String jobId = StringArgumentType.getString(ctx, "job");
                        jobController.joinJob(user, jobId);
                        return 1;
                    })
                )
            )
            .then(CommandManager.literal("leave")
                .then(CommandManager.argument("job", StringArgumentType.string())
                    .executes(ctx -> {
                        // Leave job
                    })
                )
            )
        );
    }
}
```

#### 12. Main Mod Class

**JobsFabricMod.java**
```java
public class JobsFabricMod implements DedicatedServerModInitializer {
    private static JobsFabricMod instance;
    private ConfigManager configManager;
    private JobManager jobManager;
    private UserCache userCache;
    private ImpactorEconomyHandler economy;
    private JobController jobController;
    // ... all managers ...
    
    @Override
    public void onInitializeServer() {
        instance = this;
        
        // 1. Load configs
        configManager = new ConfigManager();
        configManager.load();
        
        // 2. Initialize economy
        economy = new ImpactorEconomyHandler();
        economy.init();
        
        // 3. Load jobs
        jobManager = new JobManager(configManager);
        jobManager.loadJobs();
        
        // 4. Initialize storage
        storageManager = new JsonStorageManager();
        
        // 5. Initialize caches
        userCache = new UserCache(storageManager);
        
        // 6. Register event handlers
        EventRegistry.registerAll();
        
        // 7. Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            JobsCommand.register(dispatcher);
            JobsAdminCommand.register(dispatcher);
        });
        
        // 8. Start auto-save task
        startAutoSave();
        
        LOGGER.info("AdvancedJobs loaded successfully!");
    }
    
    private void startAutoSave() {
        // Every 180 seconds (config setting)
        // Save all cached user data
    }
}
```

---

## PHASE 2: EXPAND TO ALL JOBS

### Additional Event Handlers Needed

1. **EntityKillHandler** - Hunter (passive mobs), Fighter (hostile mobs)
2. **FishingHandler** - Fisherman (catch fish)
3. **CraftingHandler** - Crafter (craft items)
4. **SmeltingHandler** - Smelter (smelt items in furnace)
5. **BrewingHandler** - Brewer (brew potions) + brewing protection
6. **EnchantingHandler** - Enchanter, Custom Enchanter (enchant items)
7. **AnimalBreedHandler** - Breeder (breed animals)
8. **AnimalTameHandler** - Tamer (tame animals)
9. **BeehiveHandler** - Beekeeper (harvest honey/honeycomb)
10. **MovementHandlers**:
    - Swimmer: Track distance in water
    - Runner: Track distance on ground
    - Glider: Track distance with elytra
11. **ExplorerHandler** - Track unique biomes/structures visited
12. **GourmetHandler** - Eat different foods

### Fabric Event Hooks Reference
```java
// Block events
PlayerBlockBreakEvents.AFTER
PlayerBlockBreakEvents.BEFORE
UseBlockCallback

// Entity events
ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY
AnimalTameCallback (custom - use mixin)
ServerLivingEntityEvents.ALLOW_DEATH

// Item events
UseItemCallback (fishing)
CraftItemCallback (custom - use mixin)

// Movement tracking
ServerTickEvents.END_SERVER_TICK (check player positions)
```

---

## PHASE 3: ADVANCED FEATURES

### 1. Leaderboard System
- Track top 10 players by points
- Track top 10 players per job by level
- Cache system with refresh every 5 minutes
- GUI display with pagination

### 2. Booster System
- Booster types: Progress, Rewards, Points
- Time-based expiration
- Global vs personal boosters
- Apply multipliers in calculations

### 3. Admin Commands
```
/jobsadmin reload - Reload configs
/jobsadmin reset <player> - Reset player data
/jobsadmin setlevel <player> <job> <level>
/jobsadmin addpoints <player> <amount>
/jobsadmin booster <type> <multiplier> <duration>
```

---

## CONVERSION CHECKLIST: YAML → JSON

### Config Files to Convert
1. **config.yml** → **config.json**
   - Keep same structure
   - Convert YAML maps to JSON objects
   - Convert YAML lists to JSON arrays

2. **rewards.yml** → **rewards.json**
   - Reward definitions with variables
   - Command execution strings
   - Item specifications

3. **jobs/*.yml** → **jobs/*.json** (20 files)
   - Miner, Farmer, Builder, Fisher, Hunter, Fighter
   - Breeder, Crafter, Smelter, Enchanter, Brewer
   - Beekeeper, Tamer, Swimmer, Runner, Glider
   - Explorer, Gourmet, Lumberjack, Custom Enchanter

4. **lang/*.yml** → **lang/*.json** (8 files)
   - en, es, fr, de, nl, da, ko, zh-cn
   - All message strings
   - Time formats
   - Color codes

### Conversion Script Example
```java
// Use SnakeYAML to read, convert to Map, write with Gson
Yaml yaml = new Yaml();
Map<String, Object> data = yaml.load(yamlFile);
String json = new GsonBuilder().setPrettyPrinting().create().toJson(data);
```

---

## TESTING STRATEGY

### Phase 1 Testing Checklist
- [ ] Mod loads without errors
- [ ] Impactor economy integration works
- [ ] Config files load correctly
- [ ] User data saves/loads
- [ ] Player can join Miner job
- [ ] Breaking ores gives progress
- [ ] Player-placed blocks don't give progress
- [ ] Level up works
- [ ] Rewards are given (economy command executes)
- [ ] GUI menu opens and displays jobs
- [ ] Commands work (/jobs, /jobs join miner)
- [ ] Messages display correctly
- [ ] Progress notifications at milestones

### Phase 2 Testing
- [ ] All 20 jobs work correctly
- [ ] Each job type triggers properly
- [ ] Brewing protection works
- [ ] Progress formulas calculate correctly

### Phase 3 Testing
- [ ] Leaderboards display correctly
- [ ] Boosters apply multipliers
- [ ] Admin commands work
- [ ] Data persists across server restarts

---

## CRITICAL IMPLEMENTATION NOTES

### 1. Block Break Protection
```java
// Use a Map<BlockPos, UUID> cache
// Mark blocks when placed
// Check cache before giving progress
// Clear old entries after 1 hour (config)
```

### 2. Brewing Protection
```java
// Use NBT tags on potion items
// PotionItem.setCustomData(stack, nbt.putBoolean("ajobs_counted", true))
// Check NBT before giving progress
```

### 3. Progress Formula Evaluation
```java
// Use JavaScript engine or custom parser
// Variables: %level%, %booster%
// Example: "2 * (%level% ^ 2)" with level=3 → 18
ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
String formula = progressFormula.replace("%level%", String.valueOf(level));
return ((Number) engine.eval(formula)).doubleValue();
```

### 4. Placeholder Replacement
```java
public String replacePlaceholders(String text, ServerPlayerEntity player, Job job, UserJobInfo info) {
    return text
        .replace("%player%", player.getName().getString())
        .replace("%level%", String.valueOf(info.getLevel()))
        .replace("%progress%", info.getProgress().toString())
        .replace("%required_progress%", info.getRequiredProgress().toString())
        .replace("%points%", String.valueOf(user.getPoints()))
        .replace("%active%", info.isActive() ? "§aYes" : "§cNo")
        .replace("%job_name%", job.getName());
}
```

### 5. Permission Checks
```java
// Use Fabric Permissions API
boolean hasPermission = Permissions.check(player, "advancedjobs.job.miner", 0);
int maxJobs = Permissions.getPermissionValue(player, "advancedjobs.slots", 3);
```

---

## DEPENDENCIES TO ADD

### build.gradle additions
```gradle
repositories {
    maven { url "https://maven.impactdev.net/repository/development" }
    maven { url "https://maven.nucleoid.xyz/" } // For SgUi
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
}

dependencies {
    // Impactor Economy
    modImplementation "net.impactdev.impactor.api:economy:5.3.0"
    
    // Server GUI library
    include(modImplementation("eu.pb4:sgui:1.2.2+1.20.1"))
    
    // Permissions API
    include(modImplementation("me.lucko:fabric-permissions-api:0.2-SNAPSHOT"))
    
    // YAML parser (for config conversion utility)
    implementation "org.yaml:snakeyaml:2.0"
}
```

---

## FILE SIZE ESTIMATES

- Core objects: ~800 lines
- Config system: ~600 lines
- Storage system: ~500 lines
- Economy handler: ~200 lines
- Event handlers (Phase 1): ~600 lines
- Job controller: ~800 lines
- GUI system: ~1000 lines
- Commands: ~400 lines
- Utilities: ~400 lines

**Phase 1 Total: ~5,300 lines of Java code**

---

## IMPORTANT URLS & REFERENCES

- Impactor Source: `c:\Users\Cristian\Documents\Code\VSCODE\JobsMod\Impactor-1.20.1\`
- Original Plugin: `c:\Users\Cristian\Documents\Code\VSCODE\JobsMod\net\advancedplugins\jobs\`
- Template Location: `c:\Users\Cristian\Documents\Code\VSCODE\JobsMod\jobsfabricmod-template-1.20.1\`
- Server Mods: `c:\Users\Cristian\Documents\Code\VSCODE\JobsMod\server mods\`
- Fabric API Docs: https://fabricmc.net/wiki/tutorial:start
- SgUi Docs: https://pb4.eu/docs/sgui/latest/
- Impactor API: https://github.com/NickImpact/Impactor

---

## COMMON PITFALLS TO AVOID

1. **Don't forget server-side only** - No client entrypoint needed
2. **Use CompletableFuture for economy** - Impactor uses async operations
3. **Cache users properly** - Load on join, save on quit + auto-save
4. **Thread safety** - Use ConcurrentHashMap for caches
5. **NBT data** - Use proper Minecraft NBT methods, not custom serialization
6. **Permissions** - Check permissions before operations
7. **Event registration** - Register in onInitializeServer, not static
8. **Resource cleanup** - Properly close GUI handlers
9. **Error handling** - Try-catch for economy operations, file I/O
10. **Testing** - Test with actual Impactor economy mod installed

---

## PHASE COMPLETION CRITERIA

### Phase 1 Complete When:
✅ Miner, Farmer, Builder jobs work end-to-end
✅ Economy rewards given correctly
✅ GUI menus functional
✅ Data saves/loads properly
✅ Block break protection works
✅ Commands functional
✅ User can test on server

### Phase 2 Complete When:
✅ All 20 jobs implemented
✅ All event types working
✅ Brewing protection implemented
✅ All special progress rules work

### Phase 3 Complete When:
✅ Leaderboards functional
✅ Booster system works
✅ All admin commands implemented
✅ Full testing passed

---

## NEXT STEPS WHEN RESUMING

1. Read this plan document
2. Verify template location: `jobsfabricmod-template-1.20.1/`
3. Start with Phase 1, Step 1: Update build.gradle
4. Follow the file creation order listed in Phase 1
5. Test after each major component
6. User can test after Phase 1 completion

---

**PLAN CREATED:** December 24, 2025
**STATUS:** Ready to implement - awaiting user confirmation to start
