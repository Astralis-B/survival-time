![#Survival Time](https://cdn.modrinth.com/data/cached_images/b862cd2ec885648e3d249285df751740d16788b4.png)

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-green)
![Fabric](https://img.shields.io/badge/Modloader-Fabric-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

Keep track of your survival progress! Survival Time displays exactly how long you've survived since your last death — always in sync with Minecraft's built-in statistics.

## ✨ Features

### ⏱️ Dual Time Formats
- **Real Time** — Decimal real-world units that scale automatically (Seconds → Minutes → Hours → Days → Months → Years)
- **Minecraft Days** — Decimal in-game units that scale automatically (Hours → Days → Months → Years)
- All values are shown with two decimal places (e.g. `1.50 Hours`, `2.75 Days`)

### 💀 Death Animation
- When you die, the time you survived counts down to zero on screen
- The display fades out automatically — no need to dismiss it

### 🎨 Customizable Display
- **6 Screen Positions** — Top Left, Top Right, Bottom Left, Bottom Right, Action Bar, Center Top
- **Fine Position Adjustments** — X and Y offset controls for pixel-perfect placement
- **Clean HUD Integration** — Minimal design that blends with your game

### ⚙️ Easy Configuration
- **In-Game Settings** — Full ModMenu integration with Cloth Config
- **Instant Updates** — Changes apply immediately
- **Auto-Save** — Your preferences are remembered between sessions

## 🚀 Installation

1. Install [Fabric Loader](https://fabricmc.net/use/)
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download Survival Time
4. Place the `.jar` file in your `.minecraft/mods/` folder

## 🎮 How to Use

1. Access all settings in-game via **ModMenu**
2. Click on **Survival Time**
3. Adjust:
    - **Enabled** — Toggle the HUD on or off
    - **Death Animation** — Toggle the countdown animation on death
    - **Time Format** — Switch between Real Time and Minecraft Days
    - **Position** — Choose from 6 preset screen locations
    - **X / Y Offsets** — Fine-tune the exact position
4. Changes apply instantly and save automatically

### 💡 Tips
- The timer only displays while you are alive
- The timer pauses when the game is paused
- Works seamlessly alongside other HUD mods

## 🔧 Technical Details

- **Client-side only** — No server installation required
- **Requirements:**
    - Minecraft 1.21.11
    - Fabric Loader 0.18.2+
    - Fabric API
- **Optional:** ModMenu + Cloth Config for in-game configuration
- **Lightweight:** Minimal performance impact — a single HUD callback registered at startup

## 🤝 Compatibility

- **No server installation required** — client-side only
- Works with most HUD and interface mods
- Compatible with death counter and statistics mods
- No conflicts with resource packs

## 🐛 Reporting Issues

Found a bug or have a suggestion?
[Open an issue](https://github.com/Astralis-B/survival-time/issues)

Please include:
- Minecraft version
- Mod version
- Other installed mods
- Steps to reproduce the issue

## 🙏 Credits

- **Astralis** — Development
- **FabricMC** — Modloader foundation

## 📜 License

Licensed under the MIT License. See [LICENSE](LICENSE) for details.

---

*"Every second counts in survival mode."*
