<div align="center">
  
# LyttleAdmin

[![Paper](https://img.shields.io/badge/Paper-1.21.x-blue)](https://papermc.io)
[![Hangar](https://img.shields.io/badge/Hangar-download-success)](https://hangar.papermc.io/Lyttle-Development)
[![Discord](https://img.shields.io/discord/941334383216967690?color=7289DA&label=Discord&logo=discord&logoColor=ffffff)](https://discord.gg/QfqFFPFFQZ)

> ✨ **A powerful, lightweight staff management plugin with advanced moderation features!** ✨

[📚 Features](#--features) • [⌨️ Commands](#-%EF%B8%8F-commands) • [🔑 Permissions](#--permissions) • [📥 Installation](#--installation) • [⚙️ Configuration](#%EF%B8%8F-configuration) • [📱 Support](#--support)

</div>

![Divider](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

## 🌟 Features

### 🎯 Core Plugin Features
- Advanced Staff Mode System with reason tracking
- Inventory and location restoration capabilities
- Comprehensive staff action logging
- Player-focused moderation tools
- MiniMessage format support for all messages

---

### 🤌 Lyttle Certified
- Clean and efficient codebase
- No unnecessary features
- Full flexibility and configurability
- Open source and free to use (MIT License)

---

## ⌨️ Commands

> 💡 `<required>` `[optional]`

| Command                        | Permission         | Description                          |
|:------------------------------|:-------------------|:-------------------------------------|
| `/staff`                      | `admin.staff`      | Toggle staff mode                    |
| `/staff --restore <date> <time>` | `admin.restore`    | Restore player data from backup      |

---

## 🔑 Permissions

| Permission Node     | Description                   | Default |
|:-------------------|:------------------------------|:--------|
| `admin.*`          | Grants all plugin permissions | `❌`     |
| `admin.staff`      | Access to staff mode          | `❌`     |
| `admin.restore`    | Access to restore commands    | `❌`     |

---

## 📥 Installation

### Quick Start
1. Download the latest version from Hangar
2. Place the `.jar` file in your server's `plugins` folder
3. Restart your server
4. Edit the configuration files to customize messages and settings
5. Use `/staff` to begin using the plugin

---

### 📋 Requirements
- Java 21 or newer
- Paper 1.21.x+
- Minimum 20MB free disk space

---

### 📝 Configuration Files
#### 🔧 `config.yml`
The main configuration file controlling plugin behavior and features.

#### 💬 `messages.yml`
Customize all plugin messages. Supports MiniMessage formatting for advanced styling.

Example message customization:
```yaml
prefix: "<dark_aqua><bold>Lyttle<aqua><bold>Admin<reset> <dark_gray>- <gray>"
staff_enabled: "<USER> <dark_red>enabled<gray> staff mode.\n   Reason: <italic><blue><REASON>"
```

### 🔄 The #defaults Folder
The folder serves several important purposes: `#defaults`
1. **Backup Reference**: Contains original copies of all configuration files
2. **Reset Option**: Use these to restore default settings
3. **Update Safety**: Preserved during plugin updates
4. **Documentation**: Shows all available options with comments


> 💡 **Never modify files in the #defaults folder!** They are automatically overwritten during server restarts.
>

---

## 💬 Support

<div align="center">

### 🤝 Need Help?

[![Discord](https://img.shields.io/discord/941334383216967690?color=7289DA&label=Join%20Our%20Discord&logo=discord&logoColor=ffffff&style=for-the-badge)](https://discord.gg/QfqFFPFFQZ)

🐛 Found a bug? [Open an Issue](https://github.com/your-username/your-plugin/issues)  
💡 Have a suggestion? [Share your idea](https://github.com/your-username/your-plugin/issues)

</div>

---

## 📜 License

<div align="center">

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

### 🌟 Made with the lyttlest details in mind by [Lyttle Development](https://www.lyttledevelopment.com)

If you enjoy this plugin, please consider:

⭐ Giving it a star on GitHub <br>
💬 Sharing it with other server owners<br>
🎁 Supporting development through [Donations](https://github.com/LyttleDevelopment)

![Divider](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png)

</div>