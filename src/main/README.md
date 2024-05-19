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
| `homes.count`            | op      | Base for permission on number of homes you can have                              |
| `homes.count.<num>`      | op      | Base for permission on number of homes you can have                              |
| `homes.count.bypass`     | op      | Allows for setting infinite homes regardless of how many you have set as the max |
| `homes.reload`           | op      | Allows reloading the config                                                      |
| `homes.safety.bypass`    | false   | Allows bypassing the safety checks                                               |
