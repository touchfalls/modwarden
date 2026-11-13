## 1.0.1
- Fixed players being kicked in whitelist mode because of `mixinextras`, which is bundled with Fabric Loader. It is now always allowed.

## 1.0.0
- First release of ModWarden, a fork of ModChecker by AVCD.
- Supports Minecraft 1.21 – 26.3 (Fabric / Quilt).
- Fabric API modules (`fabric-*`) are always allowed, so the whitelist no longer breaks between game versions.
- Kick messages in English, Russian, French, Italian and Spanish (picked from the client language).
