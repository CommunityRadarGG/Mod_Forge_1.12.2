CommunityRadar - Official - Forge Mod 1.12.2
==========================
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4.svg)](#code-of-conduct)
<br>
[Website](https://community-radar.de/) |
[Discord](https://discord.community-radar.de/)

CommunityRadar is a free and open-source collection of mods and addons for Minecraft for managing ingame scammers and trusted players on GrieferGames.

## Issues
If you notice any bugs or missing features, you can let us know by opening a ticket or by creating a feedback or bug-report post on our Discord.

## License
This project is subject to the [Apache License v2.0](https://www.apache.org/licenses/LICENSE-2.0).
This does only apply for source code located directly in this repository.
Dependencies and used tools may have other licenses, which is not covered by this license.
Dependency licenses can be seen in the `LICENSES` folder if present.

# General Information
## Code of Conduct
Please view our Code of Conduct [here](https://github.com/CommunityRadarGG/.github/blob/main/CODE_OF_CONDUCT.md).

## Contributing
We appreciate contributions. So if you want to support us,
feel free to make changes to the source code and submit a pull request.
Please follow the [guidelines](https://github.com/CommunityRadarGG/.github/blob/main/CONTRIBUTING.md).

## Security
If you find a security issue please don't report it in a public issue.
Please use our form [here](https://github.com/CommunityRadarGG/.github/security/policy/).

# Documentation
## Commands
- `/radar [help]` --> Shows all available commands and subcommands.
- `/radar lists` --> Shows all available lists.
- `/radar list add <list_namespace> <list_prefix>` --> Creates a list with the given namespace and prefix.
- `/radar list prefix <list_namespace> <new_list_prefix>` --> Changes the prefix of the given list.
- `/radar list delete <list_namespace>` --> Deletes a list by the given name.
- `/radar list show <list_namespace>` --> Shows all players on the given list.
- `/radar check <player_name>` --> Checks if the given player is on a list.
- `/radar check *` --> Checks, which players who are on a list are online.
- `/radar player add <list_namespace> <player_name> <add_cause>` --> Adds a player to a private list.
- `/radar player remove <list_namespace> <player_name>` --> Removes a given player from a private list.
