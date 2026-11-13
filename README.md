# ModWarden

Control which mods players can join your Fabric server with.

When a player joins, ModWarden compares their installed mods with your rules and kicks them with a clear message if they break them. No more guessing who is running what.

## Features

- **Whitelist or banlist mode.** Allow only the mods you list, or allow everything except the mods you list.
- **Fabric API is always allowed.** Every `fabric-*` module is trusted, so the check keeps working across game versions.
- **Kick messages in 5 languages.** English, Русский, Français, Italiano, Español, picked from the player's game language. Other languages fall back to English.
- **One jar per version range, 1.21 to 26.3.**

## Setup

Install ModWarden on **both the server and the client**. Players without it are kicked after 5 seconds.

On the first server start, `config/modwarden.json` is created:

```json
{
  "mode": "whitelist",
  "mods": []
}
```

- `whitelist`: players may only have the mods listed in `mods`.
- `banlist`: players may have anything except the mods listed in `mods`.

Use mod **ids** (for example `sodium`, not "Sodium"). These are always allowed: `minecraft`, `java`, `fabricloader`, `modwarden` and every Fabric API module. The config is read when the server starts.

## Limitations

The client sends its own mod list, so a modified client can lie about it. ModWarden stops accidental and casual use of forbidden mods. It is not an anti-cheat.

## Credits

A fork of [ModChecker](https://github.com/VladKagaykin/mod_checker) by AVCD, under the MIT license.
