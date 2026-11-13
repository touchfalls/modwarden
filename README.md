# ModWarden

Checks which mods a player has installed when they join a Fabric server, and kicks them if the list breaks the server's rules.

**Install it on both the server and the client.** Players without ModWarden are kicked after 5 seconds.

## Config

`config/modwarden.json` is created on the first server start:

```json
{
  "mode": "whitelist",
  "mods": []
}
```

- `whitelist`: players may only have the mods listed in `mods`.
- `banlist`: players may have anything except the mods listed in `mods`.

Use mod **ids** (for example `sodium`, not "Sodium"). These are always allowed: `minecraft`, `java`, `fabricloader`, `modwarden`, and every Fabric API module (`fabric-*`).

The config is read when the server starts.

## Languages

Kick messages follow the player's game language: English, Русский, Français, Italiano, Español. Any other language falls back to English.

## Limitations

The client sends its own mod list, so a modified client can lie about it. ModWarden stops accidental and casual use of forbidden mods. It is not an anti-cheat.

## Credits

A fork of [ModChecker](https://github.com/VladKagaykin/mod_checker) by AVCD, under the MIT license.
