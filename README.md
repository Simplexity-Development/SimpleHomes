# SimpleHomes Plugin

A basic plugin for setting and teleporting to locations. It allows players to manage multiple homes and
provides a straightforward interface for home management commands.

## Commands

| Command        | Permission               | Description         |
|----------------|--------------------------|---------------------|
| `/sethome`     | `homes.commands.sethome` | Sets a home         |
| `/delhome`     | `homes.commands.delhome` | Deletes a home      |
| `/home`        | `homes.commands.home`    | Teleports to a home |
| `/homesreload` | `homes.reload`           | Reloads the config  |
| `/homelist`    | `homes.commands.list`    | Lists your homes    |

## Permissions

| Permission               | Default | Description                                                                      |
|--------------------------|---------|----------------------------------------------------------------------------------|
| `homes`                  | op      | Allows base plugin functionality                                                 |
| `homes.commands`         | op      | Allows player to use commands                                                    |
| `homes.commands.sethome` | op      | Allows player to set home                                                        |
| `homes.commands.delhome` | op      | Allows player to delete home                                                     |
| `homes.commands.home`    | op      | Allows player to teleport to home                                                |
| `homes.commands.list`    | op      | Allows you to list your own homes                                                |
| `homes.bed`              | op      | Allows you to teleport to your bed                                               |
| `homes.count`            | op      | Base for permission on number of homes you can have                              |
| `homes.count.<num>`      | op      | Base for permission on number of homes you can have                              |
| `homes.count.bypass`     | op      | Allows for setting infinite homes regardless of how many you have set as the max |
| `homes.reload`           | op      | Allows reloading the config                                                      |
| `homes.safety.bypass`    | false   | Allows bypassing the safety checks                                               |

## Importing from other plugins

**THIS COMMAND IS IRREVERSIBLE AND WILL DELETE ANY HOMES YOU HAVE ALREADY SET IN SIMPLEHOMES**
it is **HIGHLY** recommended you do not run this command while anyone is online, to reduce the risk of any save
corruption.
**USE AT YOUR OWN RISK**

To import from another plugin, you need to go to console, as the command is console-only. Command syntax is as follows:
`importhomes <plugin> [player]`
The player argument is optional, if you want to import all homes that are saved in the plugin specified, leave the
player argument out.

Currently supported plugins to import from:

- Essentials
