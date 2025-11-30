<div align="center"> 
   <img height="128px" width="128px" alt="logo" src="./icon/icon.png"/> 
   <h1>Texture Locale Redirector</h1>

中文 | <a href="README.md">English</a>

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

Texture Locale Redirector 为 Minecraft 资源包提供了原生的多语言纹理支持。

它通过扩展原版的资源加载机制，让你可以在一个资源包中为不同语言提供专门的纹理。
这解决了本地化过程中，因纹理上的文字或图标需要替换而影响其他语言玩家体验的问题。

## 使用说明

只需简单三步，即可让你的资源包支持多语言纹理：

1.  **安装模组**：将本模组放入你的 `mods` 文件夹。
2.  **创建资源包**：在资源包中，按照特定的文件夹结构组织你的本地化纹理。
3.  **开始游戏**：在游戏中启用资源包并切换到对应语言即可。

## 资源包结构示例

在你的资源包中，按照 `assets/<namespace>/textures/<language>/` 的结构来存放本地化纹理。

`<namespace>` 是命名空间，原版为 `minecraft`，模组一般为它们的 modid。

`<language>` 是语言代码，如 `zh_cn`（简体中文）或 `ja_jp`（日文）。

注意：需要替换的纹理的相对路径需与原纹理保持一致。

```
资源包名称/
└── assets/
    └── minecraft（命名空间）/
        └── textures/
            ├── zh_cn/          # 简体中文纹理
            │   ├── block/
            │   │   └── dirt.png  # 替换的泥土纹理
            │   └── item/
            │       └── diamond_sword.png  # 替换的钻石剑纹理
            └── ja_jp/          # 日文纹理
                └── item/
                    └── diamond_sword.png  # 替换的钻石剑纹理
```

## 性能

本模组进行了大量的优化，对性能不应造成明显影响

* 模组只在非 `en_us` 环境下工作，对英语玩家没有任何影响。
* 本模组为需要替换的纹理建立了**先进的缓存机制**，极大地减少了不必要的磁盘读写操作。
* 在语言切换和游戏资源重载时会清空缓存，立即更新纹理。内存不足时会智能释放缓存，保证游戏正常运行。
* 如果资源包没有定义特定语言纹理文件夹，则不会扫描替换以减少性能影响。且不会影响其他任何资源包的正常工作。

## 许可证

本项目采用 [MIT 许可证](LICENSE) 开源。

欢迎提交 Issue 反馈问题或提出建议，欢迎贡献 PR 。