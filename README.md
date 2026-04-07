# SimpleHomes Plugin

A basic plugin for setting and teleporting to locations. It allows players to manage multiple homes and
provides a straightforward interface for home management commands.

## Commands

| Command               | Permission         | Description                        |
|-----------------------|--------------------|------------------------------------|
| `/sethome`            | `homes.sethome`    | Sets a home                        |
| `/delhome`            | `homes.delhome`    | Deletes a home                     |
| `/home`               | `homes.home`       | Teleports to a home                |
| `/homes`              | `homes.list`       | Lists your homes                   |
| `/homes admin reload` | `homes.reload`     | Reloads the config                 |
| `/homes admin tp`     | `homes.admin.tp`   | Teleports to another player's home |
| `/homes admin list`   | `homes.admin.list` | Lists another player's homes       |

## Permissions

| Permission            | Default | Description                                                                      |
|-----------------------|---------|----------------------------------------------------------------------------------|
| `homes.sethome`       | true    | Allows player to set home                                                        |
| `homes.delhome`       | true    | Allows player to delete home                                                     |
| `homes.home`          | true    | Allows player to teleport to home                                                |
| `homes.list`          | true    | Allows you to list your own homes                                                |
| `homes.bed`           | true    | Allows you to teleport to your bed, requires configuration setting               |
| `homes.reload`        | op      | Allows reloading the config                                                      |
| `homes.bypass.safety` | false   | Allows bypassing the safety checks                                               |
| `homes.bypass.delay`  | op      | Allows bypassing the teleport delay                                              |
| `homes.bypass.count`  | op      | Allows for setting infinite homes regardless of how many you have set as the max |

## Configuring Number of Homes

The number of homes is determined by permission nodes you create within the `config.yml`.

```yml
# Configuration for number of homes based on permission
counts:
  player:
    permission: "homes.count.player"
    home-count: 3
  moderator:
    permission: "homes.count.mod"
    home-count: 5
```

In the above example, we can break down the counts part of the configuration into a few parts:

```yml
counts:
  name:
    permission: "string"
    home-count: int
```

`name` can be anything, just has to be unique.

`permission` can be any string, just has to be unique across all plugins.

- We recommend using `homes.count.<something>`.
- This is the permission you will give to a player or group/rank.

`home-count` should be an integer, the number of homes you want it to be for the player.

- The highest home-count value given to a player will be applied. This means in the above example,
  if a player has both then it will apply a maximum of 5 homes.

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
