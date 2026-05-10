<h1 align="center">
  <br>
  <img width="100" alt="datapack_color" src="docs/README_Pictures/Nodex_IconSVG.svg" />
  <br>
  Nodex
</h1>
<h4 align="center">A work in progress minecraft in-game IDE mod for datapack development.</h4>
<div align="center">  

  [![Discord](https://img.shields.io/discord/1163847082080211025?label=discord&color=9089DA&logo=discord&style=for-the-badge)](https://discord.com/invite/qZ885qTvkx)
  [![Downloads](https://img.shields.io/modrinth/dt/XlilVGvF)](https://modrinth.com/mod/nodex/versions)

  [![Kotlin](https://img.shields.io/badge/Kotlin-2.3.20-orange?logo=kotlin&logocolor=white)](https://kotlinlang.org/)
  [![Java](https://img.shields.io/badge/Java-jdk%2025-red?logo=openjdk&logocolor=white)](https://jdk.java.net/25/)

  <code style="color: red"> This mod is currently in alpha and is in active development</code> <br>
  <code style="color: red"> many features of this mod have not been fully implemented</code>
</div>

## 📝 Overview

Nodex is a minecraft mod that allows in game data pack editing with a built-in text editor.<br>
**These screenshots are OLD**

|                                     GUI                                     |                                     Menu                                      |
|:---------------------------------------------------------------------------:|:-----------------------------------------------------------------------------:|
| ![preview_1](docs/README_Pictures/Datapack-IDE-0.0.1-FullScreen-README.png) | ![preview_2](docs/README_Pictures/Datapack-IDE-0.0.1-SettingsMenu-README.png) |

The goal of this mod aside from basic coding functionality expected from vscode is:
- To provide in-game tools for debugging or fast implementation to speed up datapack development.
- To provide a collaborative datapack programming environment (multiplayer support).

## ✅ What's implemented?

* Saving, loading, editing datapack files
* Code Editor with JSON Syntax highlighting
* [AtlantaFX themes](https://mkpaz.github.io/atlantafx/) support 
* File Tree
* Project manager
* Universal folders for datapacks and configs
* Datapack Mirroring to a world

## 👷 Future plans
* Multiplayer support
* Plugin system
* [SpyglassMC](https://github.com/SpyglassMC/Spyglass) plugin
* [Beet](https://github.com/mcbeet/beet) plugin
* [Datapack Icons](https://github.com/FuncFusion/mc-dp-icons) plugin


## 🧪 Want to test out the mod?
<details>
<summary><b>click to view process to get experimental build for play testing</b></summary>
<hr>
go to Actions -> go to the latest workflow shown on top
<img width="667" height="343" alt="image" src="https://github.com/user-attachments/assets/cec35fa7-c6ec-46b4-8ac1-407a5b29733e" />

Then download Artifacts.
Unzip Artifact folder upon download and drag the jar file into your mod folder make sure to install the appropriate dependencies, such as [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin) and [Fabric API](https://modrinth.com/mod/fabric-api). Then you should be good to go!

Warning that this version isn't a stable build as it is a dev build, if the current artifact build is having issues, feel free to file a issue report.<br>
If you are only interested in running a build for your own use, try older artifacts if the lastest isn't working.
</details>
<hr>

## ❓FAQ

#### Will we support other loaders?
We plan on supporting all loaders as well as any server software. Support for other loaders will come while beta is being released and fabric will be the default until then and will be focused on first! 

#### When will it be FULLY released?
The beta release isn't expected any time soon as there isn't any exact set date. If you are looking for updates, check up on our discord where we have regular updates on our progress posted up on there.

#### What versions of minecraft will you be supporting?
Currently supporting versions from 1.20.5 to newest!<br>
Check the modrinth versions page for more info [here](https://modrinth.com/project/XlilVGvF).

## 🖐️ Want to support the project?
Here's a donation link:<br>
<a href="https://ko-fi.com/frostzie" target="_blank">
  <img src="https://storage.ko-fi.com/cdn/brandasset/v2/support_me_on_kofi_blue.png" width="200" alt="Ko-fi" />
</a>
<br>
All proceeds goes to frostzie

## ☝️ Want to give feedback or request features?
Any input is appreciated here
<h4 align="left">
  <a href="https://github.com/Frostzie/DataPack-IDE/issues/new?title=Feedback%3A+&labels=feedback%2C&assignees=Frostzie%2C">🗒️ Open Feedback Issue</a>
<br>
<br>
  <a href="https://github.com/Frostzie/DataPack-IDE/issues/new?title=Feature%20Request%3A+&labels=featurerequest%2C&assignees=Frostzie%2C">💡 Open Feature Issue</a>
</h4>

## License & Dependencies

Nodex is free software; you can redistribute it and/or modify it
under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation; **either version 2.1 of the License, or
(at your option) any later version**.

A copy of the GNU LGPL is provided in the [LICENSE](LICENSE) file.<br>

Dependencies used: [List](docs/SOFTWARE_USED.md)