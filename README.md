# LyttleAdmin

LyttleAdmin is a powerful and flexible administration plugin for Minecraft servers. It provides a wide range of features to help server administrators manage their servers more effectively.

## Features

- **Staff Mode**: Allows staff members to switch into a special mode where they can perform administrative tasks without affecting their normal gameplay. Staff mode can be enabled or disabled with a simple command.
- **Inventory Management**: Staff members' inventories are saved when they enter staff mode and restored when they exit, preventing any loss of items.
- **Staff Logs**: All staff mode activations and deactivations are logged, along with the reason provided by the staff member. Logs can be viewed with a simple command.
- **Role Management**: Staff members are automatically given appropriate permissions when they enter staff mode, and these permissions are removed when they exit.
- **Location Management**: Staff members' locations are saved when they enter staff mode and they are teleported back to this location when they exit.

## Dependencies
- **LuckPerms**: LyttleAdmin requires LuckPerms to manage permissions. You can download LuckPerms from their [website](https://luckperms.net/).

## Installation

1. Download the latest version of the LyttleAdmin plugin from the [releases page](https://github.com/Lyttle-Development/LyttleAdmin/releases).
2. Place the downloaded `.jar` file into your server's `plugins` directory.
3. Please make sure you do have LuckPerms installed as it is a dependency for LyttleAdmin
4. Restart your server.

## Usage

- To enable or disable staff mode, use the `/staff` command. This command can only be used by players (not the console).
- To view the staff logs, use the `/staff log` command. This command can be used by anyone with the `lyttleadmin.staff` permission.
- To restore a lost inventory, use the `/staff --restore <date> <time>` command. This command can only be used by players with the `lyttleadmin.staff` permission.

## Permissions

- `lyttleadmin.staff`: Allows the user to use the `/staff` command and view the staff logs.
- `lyttleadmin.staff.admin`: Gives the user admin-level permissions when they enter staff mode.
- `lyttleadmin.staff.moderator`: Gives the user moderator-level permissions when they enter staff mode.

## Support

If you encounter any issues or have any questions, please [open an issue](https://github.com/Lyttle-Development/LyttleAdmin/issues) on GitHub.

## Contributing

Contributions are welcome! Please read the [contributing guidelines](CONTRIBUTING.md) before getting started.

# License

All rights reserved. Before using or distributing this software, you must first obtain permission from the author. Please contact the author (Stualyttle Kirry) at [Discord](https://discord.com/invite/QfqFFPFFQZ) to request permission.