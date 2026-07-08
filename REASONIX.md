# QR_Swing — Java Swing widget toolkit

## Stack

- **Language:** Java 17 (`.iml` `LANGUAGE_LEVEL="JDK_17"`)
- **UI:** Swing — wrappers in `swing.qr.kiarelemb.*`
- **Keyboard:** jnativehook (`lib/jnativehook.jar`)
- **Utils lib:** `QR_Method.jar` (`method.qr.kiarelemb.utils.*`) — source NOT in this repo
- **Collections:** Apache Commons Collections 4.4 (`lib/commons-collections4-4.4.jar`)

## Layout

`src/swing/qr/kiarelemb/` — root package

| Subpackage | Contents |
|------------|----------|
| `basic/` | Swing wrappers: `QRButton`, `QRFrame`, `QRComboBox`, `QRPanel`, `QRTable` |
| `assembly/` | UI delegates: `QRScrollBarUI`, `QRBasicComboBoxUI`, `QRUndoManager` |
| `combination/` | Composite widgets: `QRTabbedPane`, `QRPopupMenu`, `QRMenuButton` |
| `utils/` | Utility controls: `QRFontComboBox`, `QRFileSelectButton` |
| `window/` | `QRFrame`/`QRDialog` + file/picture/progress dialogs |
| `theme/` | `QRColorsAndFonts`, `QRSwingThemeDesigner` |
| `task/` | Background runner: `QRTaskRunner`, `QRTaskWorker` |
| `test/` | Manual `main()` tests (no JUnit) |
| `inter/` | Interfaces: `QRComponentUpdate`, `QRActionRegister` |
| `lib/` | Bundled JAR deps |
| `out/` | Compiled `.class` files (gitignored) |

## Commands

No Maven/Gradle. Build via IntelliJ artifact (`QR_Swing.jar`, "build on make" in `.idea/`). No test/lint/format scripts.

## Conventions

- **Prefix:** All public classes named `QR*`
- **Theme refresh:** Controls implement `componentFresh()` — called on theme switch
- **Entry:** `QRSwing.start()` before any UI, then `QRSwing.registerGlobalKeyEvents()`
- **Tests:** `main()` methods under `test/` package, no test framework
- **Imports:** Explicit single-class (no `*`)
- **Javadoc:** `@author Kiarelemb QR`, `@program: QR_Swing`, `@create yyyy-MM-dd HH:mm`
- **Listeners:** `QRActionRegister<T>` functional interface for callback registration

## Watch out for

- `QRSwing.start()` is mandatory before any UI — missing it causes silent failures
- `QR_Method.jar` is external — its source is in a **separate project**, not editable here
- `lib/libJNativeHook.x86_64.so` is Linux x86_64 only — keyboard hooks fail on other platforms
- Theme changes call `componentFresh()` on every control — keep it lightweight
