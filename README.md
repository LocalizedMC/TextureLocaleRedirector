<div align="center"> 
   <img height="128px" width="128px" alt="logo" src="./icon/icon.png"/> 
   <h1>Texture Locale Redirector</h1>

<a href="README_CN.md">中文</a> | English

<a href="https://modrinth.com/project/texture-locale-redirector">
<img alt="modrinth" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg">
</a>
<a href="https://www.curseforge.com/minecraft/mc-mods/texture-locale-redirector">
<img alt="curseforge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg">
</a>

<img alt="forge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/forge_vector.svg">
<img alt="neoforge" height="56" src="https://raw.githubusercontent.com/KessokuTeaTime/Badges-Extra/main/assets/cozy/supported/neoforge_vector.svg">
<img alt="fabric" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_vector.svg">
</div>

**Texture Locale Redirector** adds native multi-language texture support to Minecraft resource packs.  

By extending vanilla resource loading mechanism, this mod allows you to provide language-specific textures within a single resource pack.  
It solves the problem where localized textures (with text or symbols) for one language may negatively affect players in other languages.

## Usage

Follow these three simple steps to enable multi-language textures:

1. **Install the mod**: Place this mod in your `mods` folder.  
2. **Create a resource pack**: Organize your localized textures according to the required folder structure.  
3. **Start the game**: Enable the resource pack and switch to the corresponding language in Minecraft.  

## Resource Pack Structure Example

Inside your resource pack, store localized textures under `assets/<namespace>/textures/<language>/`.

- `<namespace>` is the namespace — `minecraft` for vanilla, or the modid for mods.  
- `<language>` is the language code, such as `zh_cn` (Simplified Chinese) or `ja_jp` (Japanese).  

**Note:** The relative path of the texture to be replaced must match the original texture path.

```
ResourcePackName/
└── assets/
    └── minecraft (namespace)/
        └── textures/
            ├── zh_cn/          # Simplified Chinese textures
            │   ├── block/
            │   │   └── dirt.png  # Replaced dirt texture
            │   └── item/
            │       └── diamond_sword.png  # Replaced diamond sword texture
            └── ja_jp/          # Japanese textures
                └── item/
                    └── diamond_sword.png  # Replaced diamond sword texture
```

## Performance

This mod is heavily optimized and should have negligible performance impact:

* Works **only** when the current language is not `en_us`, meaning no effect for English players.  
* Implements an **advanced caching system** to drastically reduce unnecessary disk I/O.  
* Clears cache upon language change or resource reload, ensuring textures are updated immediately.  
* Automatically releases cache memory when system memory is low to prevent lag.  
* If a resource pack has no language-specific texture folder, no replacement scan is performed — avoiding unnecessary work.  
* Does not interfere with the normal operation of other resource packs.

## License

This project is open-sourced under the [MIT License](LICENSE).  

Contributions and PRs are welcome! Feel free to submit issues for bug reports or feature requests.
