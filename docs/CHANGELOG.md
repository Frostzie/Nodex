# Nodex Changelog

## [0.0.1]

### Added
- Initial release of DataPack IDE.
- In-game IDE for editing datapack files.
- UI built with JavaFX and AtlantaFX theme library.
- Features include:
    - File tree view with directory selection and file moving.
    - Tabbed text editor with RichTextFX for code editing.
    - Dirty file indicators (suffix and color).
    - Customizable themes and font sizes.
- Minecraft integration:
    - `/datapack-ide` command to open the IDE.
    - Keybinding to toggle the IDE window.
    - "Reload Datapack" button to execute `/reload`.

## [0.0.2]

**Rebrand from "Datapack IDE" to "Nodex"**

### Added
- Updated to Minecraft version 1.21.11
- Project Manager Screen with a recent project list.
- Datapack validation and previews.
- Universal Folder System for config and project sync between instances.
- Datapack Mirroring System for universal folders.
- JSON and mcmeta syntax highlighting (Non IntelliSense).
- Custom Theme CSS editing and live reloading.
- Toggle to show a button on the start and pause screen.
- Added Notification when reloading datapack.
- The Folder Button in LeftBar now collapses the FileTree.

### Fixes
- TopBar's buttons adding white background.
- Fixed crash with too much text in the settings search field.
- Line & Column count always stays at 0.
- Curser should be more visible by default on light themes.
- Disabled non-functional buttons from being pressed.
- Made TopBar buttons actually work.
- Fixed Text Area and Fields not having limits causing crashes.
- TextEditor not gaining focus on file open.
- Curser not saving location.
- Text no longer pushes buttons out in settings.

### Technical Changes
- Migrated to mojang mappings.
- Added Stonecutter for version control.
- Removed ModMenu dependency.
- Added support for SVG icons.
- Added styles to Notification, Message, and Tab controls.
- Massively improved file system watcher.
- Removed multiple unused UI Screens.
- Removed Assets folder from mods configs.


## [0.0.3]

### Overview
Remade the whole mod from the ground up to be more maintainable. 
The functionality is almost identical to alpha 2 but code wise nothing was kept. 
Many features are missing from alpha 2. (Sorry) Ui Wise it looks about the same.

### Added
- Updated to Minecraft 26.1
- Intro Screen on first start.
- Native Topbar support
- Auto for text editor.
- New config system with backups. Creates .nodex folder under project similar to other IDEs.
- OS Specific JavaFX loading. Downloads only your OS required JavaFX libraries. (Jar size decrease)
- FPS and Memory usage display.

### Removed (Temporary)
- Universal Configs.
- Datapack Mirroring to worlds.
- Custom Themes, CSS, Theme Options.

### Technical Changes
- Updated to Java 25 and JavaFX 26.
- Koin DI for services.
- Interface for most services.
- Settings, ToolWindow registration.
- Split all managers into separate services.