# Nodex Project Tree
This is currently what the project tree looks like. Expect some changes to come.

```tree
Nodex
├─── boostrap                                       # Entry point for initialization
│    ├─── ApplicationBootstrap.kt                   # Controls Initialization flow
│    ├─── PlatformBootstrap.kt                      # Initializes JavaFX
│    ├─── ServiceBootstrap.kt                       # Initializes Services
│    ├─── MinecraftBootstrap.kt                     # Initializes Minecraft and loader wrapper and features
│    ├─── LegacyBootstrap.kt                        # Initializes Legacy systems //TODO: Remove
│    └─── UiBootstrap.kt                            # Starts UI on the JavaFX Thread
├─── domain                                         # Pure data models and contracts for the application
│    ├─── entity                                    # Core business objects with identity
│    │    ├─── EditorDocument.kt                    # Represents an open file buffer
│    │    ├─── Project.kt                           # Represents a Datapack project
│    │    └─── Workspace.kt                         # Root aggregate of open projects
│    ├─── config                                    # Serializable data for persistence
│    │    ├─── Layout.kt                            # Window geometry and panel sizes
│    │    ├─── ProjectState.kt                      # Per-project UI state (open tabs, expanded tree)
│    │    └─── Session.kt                           # Global history (recent projects)
│    └─── uicontract                                # Data contracts for the UI layer
│         ├─── Dialogs.kt                           # Dialog types and action definitions
│         ├─── DraggableWindowView.kt               # Interface for window drag behavior
│         ├─── AppScreen.kt                         # Screen enums (IDE, Project Manager, Settings)
│         ├─── PanelPosition.kt                     # IDE Screens panel positions
│         └─── Styling.kt                           # Tab styling rules and context
├─── events                                         # Event system
├─── ingame                                         # Ingame Feature system (Legacy planned for rewrite)
├─── loader                                         # Minecraft and loader wrappers
│    ├─── fabric                                    # Fabric Wrappers
│    └─── minecraft                                 # Minecraft Wrappers
│         └─── MCInterface.kt                       # Facade for Minecraft interaction
├─── service                                        # Services
│    ├─── core                                      # Low level services
│    │    ├─── ConcurrencyService.kt                # Centralizes thread management and coroutine scopes
│    │    ├─── ConfigService                        # Saving & loading configs, setting universal path
│    │    ├─── FileService.kt                       # Low-level I/O wrapper with watcher support
│    │    ├─── LayoutService.kt                     # Persists window geometry and panel state
│    │    ├─── ModInfoService.kt                    # Provies mod's metadata
│    │    └─── SessionService.kt                    # Persists recent projects and session state
│    ├─── files                                     # File services
│    │    └─── FileWatcherService.kt                # Manages file system watchers
│    ├─── ui                                        # UI services
│    │    ├─── DialogService.kt                     # Creates dialogs and notifications by publishing events
│    │    └─── NavigationService.kt                 # Manages the global navigation state (active screen)
│    └─── workspace                                 # Workspace services
│         ├─── EditorService.kt                     # Manages the lifecycle and state of open editor documents
│         └─── WorkspaceService.kt                  # High-level management of the workspace, projects, and session state
├─── settings                                       # Legacy Settings system
├─── styling                                        # Holds styling system extra info at: io/github/frostzie/nodex/styling/StylingGuide.md
│    ├─── css                                       # CSS based styling
│    │    ├─── SceneStyler.kt                       # 
│    │    ├─── StyleModule.kt                       # 
│    │    └─── StyleRegistry.kt                     # 
│    └─── theme                                     # AtlantaFX Theme based styling
│         ├─── ThemeDescriptor.kt                   # 
│         ├─── ThemeInstaller.kt                    # 
│         └─── ThemeRegistry.kt                     # 
├─── ui                                                         # UI packages
│    ├─── util                                                  # UI utilities (Can be used in viewmodel)
│    ├─── view                                                  # JavaFX Views and Layouts
│    │    ├─── layout                                           # Holds all app window layouts views
│    │    │    └─── IdeLayoutView.kt                            # IDE Views layout
│    │    ├─── bottombar                                        # Holds bottombar view
│    │    │    └─── BottomBarView.kt                            # Bottom Bar View
│    │    ├─── leftbar                                          # Holds leftbar view
│    │    │    └─── LeftBarView.kt                              # Left Bar View
│    │    ├─── rightbar                                         # Holds rightbar view
│    │    │    └─── RightBarView.kt                             # Right Bar View
│    │    ├─── overlay                                          # 
│    │    │    └─── FileTreeDropOverlayView.kt                  # 
│    │    ├─── workbench                                        # Holds workbench area (middle area) views
│    │    │    ├─── editor                                      # 
│    │    │    │    ├─── pane                                   # Holds different editor area's panes
│    │    │    │    │    ├─── CodeEditorView.kt                 # Active code editor view (Has a file opened)
│    │    │    │    │    └─── EmptyCodeEditorView.kt            # Empty, non active code editor view (No file opened)
│    │    │    │    └─── EditorAreaView.kt                      #
│    │    │    ├─── tree                                        # Holds file tree area view
│    │    │    │    └─── FileTreePlaceholderView.kt             # Placeholder for FileTree
│    │    │    └─── WorkbenchView.kt                            # 
│    │    ├─── MainWindow.kt                                    # Sets OS level window properties, loads css files and drag/resizing
│    │    └─── AppShell.kt                                      # The main screen container view
│    ├─── viewmodel                                             # MVVM ViewModels
│    │    ├─── bottombar                                        # Holds bottombar viewmodels
│    │    │    └─── BottomBarViewModel.kt                       # Bottom Bar ViewModel
│    │    ├─── leftbar                                          # Holds leftbar viewmodels
│    │    │    └─── LeftBarViewModel.kt                         # Left Bar ViewModel
│    │    ├─── workbench                                        # 
│    │    │    ├─── editor                                      # 
│    │    │    │    ├─── pane                                   # 
│    │    │    │    │    ├─── CodeEditorView.kt                 # 
│    │    │    │    │    └─── EmptyCodeEditorView.kt            # 
│    │    │    │    └─── EditorAreaView.kt                      #
│    │    │    └─── WorkbenchViewModel.kt                       #
│    │    ├─── MainWindow.kt                                    # Saves window geometry
│    │    └─── AppShell.kt                                      # The main screen container view model
│    └─── ViewFactory.kt                                        # Factory for UI components
└─── utils                                          # Core utilities (Can only be used by services) //TODO: reorgenize
```