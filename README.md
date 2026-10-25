# 🔢 Sudoku Multiplatform (v1.1.0)

A modern, offline-first Sudoku game built using **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**.

Targeted primarily for **Android** and **iOS**, with secondary adaptation for **Desktop / Steam**.

---

## 🏛 Architecture Overview

The codebase strictly decouples platform-independent domain logic from platform-specific presentation:

```text
                     ┌────────────────────────────────┐
                     │          shared/core           │
                     │  SudokuBoard • CellPosition    │
                     │  Candidates (9-bit Bitmask)    │
                     │  Solver (MRV + Backtracking)   │
                     │  Generator (Symmetric & Seeded)│
                     │  Difficulty Analyzer           │
                     │  Game Engine & Undo/Redo       │
                     │  Highlighting Engine           │
                     │  Tutorials & Demo Board Matrix │
                     │  Storage & Statistics Drivers  │
                     └───────────────┬────────────────┘
                                     │
                     ┌───────────────┴────────────────┐
                     │          shared/ui             │
                     │  Compose Multiplatform UI      │
                     │  Canvas Board & 3x3 Notes      │
                     │  Mini Board Pattern Diagrams   │
                     │  NumberPad & Ripple Animations │
                     │  Screens: Home, Game, Stats,   │
                     │     Badges, Settings, Tips,    │
                     │     Strategy Interactive Demos │
                     └───────────────┬────────────────┘
                                     │
                 ┌───────────────────┼───────────────────┐
                 ▼                   ▼                   ▼
           androidApp             iosApp             desktopApp
        (Google Play)          (App Store)            (Steam)
```

---

## ✨ Features

### 1. Mathematics & Core Domain
* **Memory-Efficient Grid**: Backed by a compact contiguous `IntArray(81)` with immutable functional copies for gameplay and in-place mutation for solver throughput.
* **Pre-Pooled Coordinate System (`CellPosition`)**: Pre-computes and caches all 81 positions (`row`, `column`, `box`, linear `index`), eliminating runtime garbage collection overhead.
* **Bitmask Candidate Engine (`Candidates`)**: Zero-allocation `@JvmInline value class` backed by a 9-bit bitmask. Executes constraint elimination in $\mathcal{O}(1)$ time.

### 2. Solver, Generator & Difficulty
* **MRV Backtracking Solver**: Uses the *Minimum Remaining Values* (Most Constrained Variable) heuristic to solve standard and tournament-grade puzzles (e.g., Arto Inkala’s AI Sudoku) in single-digit milliseconds.
* **Bounded Solution Counter**: Proves unique solvability (`countSolutions(limit = 2) == 1`) without unbounded search tree expansion.
* **180° Rotational Symmetry**: Generates classic tournament-style puzzles by carving clues in symmetric pairs.
* **Human-Technique Difficulty Classifier**: Models how humans solve puzzles without brute-force guessing:
  - *Naked Single* (Weight: 1)
  - *Hidden Single* (Weight: 2)
  - *Naked Pair* (Weight: 4)
  - *Pointing Pair / Triple* (Weight: 5)
  - *X-Wing* (Weight: 10)
  - Calibrated into **Easy**, **Medium**, **Hard**, and **Expert**.

### 3. Interactive Strategy Guide & Multi-Demo Engine
* **Visual Mini Board Previews**: Every technique card in **Tips & Tricks** features a static mini 9×9 visual grid preview illustrating the exact cell pattern (e.g., lines, boxes, or 2×2 rectangles).
* **Multi-Example Interactive Demos**: Tap **"🎮 Try Demos"** on any strategy card to launch an isolated interactive board with multiple real-game scenarios. Test and apply deductions with live visual feedback without polluting personal statistics or ongoing game saves.
* **Pedagogical Hint Engine**: Step-by-step reasoning identifying the exact numbers in a cell's Row, Column, and 3×3 Box that rule out all other digits.

### 4. Gameplay & Board Interaction
* **Selection & Highlighting Engine (Phase 8 Specification)**:
  - Selecting cell $(r, c)$ activates the **local crosshair** (highlights that cell's row, column, and box).
  - Placed numbers trigger **global digit emphasis** (highlights all matching digits and notes).
  - Explicitly avoids highlighting rows/columns of matching digits across other boxes.
* **Staggered Ripple Wave**: Completing a Row, Column, or 3×3 Box triggers a gentle 55ms-staggered horizontal wave highlight gliding smoothly from left to right.
* **Exhausted Digit Disabling**: Keypad numbers automatically dim and disable once all 9 instances are correctly placed.
* **Auto-Cleaning Pencil Notes**: Placing a digit automatically removes that candidate note from peer cells across its row, column, and 3×3 box (cleanly restored on Undo).

### 5. Persistence, Systems & Polish
* **Crash & Swipe-Kill Survival**: Persists active board, notes, timer, and moves directly to disk via Android `SharedPreferences`. Resumes seamlessly after app termination or reboot.
* **Daily Challenge Lockout**: Globally seeded daily puzzles with automatic completion tracking, locking out duplicate daily entries.
* **Accurate Statistics & Badges**: Comprehensive stats tracking (win rate, games played, best & average times, consecutive daily streaks) alongside local achievement milestones.
* **System Gesture Navigation**: Android back button / edge swipe gesture returns to the previous menu and auto-saves games without accidental app closure.
* **Data Management**: Dedicated **"🗑 Delete All User Data"** option in Settings with a confirmation dialog.
* **Tactile Haptic Feedback**: Native vibration effects for keypad taps, error buzzes, and unit completion pulses.
* **Production Packaging**: Adaptive vector launcher icon with Android 13+ Material You themed icon support and R8 optimization.

---

## 📁 Project Directory Structure

```text
Sudoku/
├── androidApp/
│   ├── src/main/
│   │   ├── AndroidManifest.xml          # Haptics, theme & adaptive icon
│   │   ├── kotlin/com/sudoku/android/   # MainActivity, AndroidStorageDriver, AndroidHapticDriver
│   │   └── res/                         # Vector adaptive launcher icons & themes
│   ├── build.gradle.kts                 # Signed release configuration (v1.1.0)
│   └── proguard-rules.pro               # R8 shrinking optimization rules
│
├── shared/
│   ├── src/commonMain/kotlin/com/sudoku/
│   │   ├── core/
│   │   │   ├── achievements/            # Badges model
│   │   │   ├── candidates/              # Bitmask candidate calculator
│   │   │   ├── daily/                   # Daily challenge engine & lockout
│   │   │   ├── difficulty/              # Human solving techniques & analyzer
│   │   │   ├── game/                    # Game session, moves & UndoManager
│   │   │   ├── generator/               # Symmetric puzzle generator
│   │   │   ├── haptics/                 # Multiplatform haptic abstraction
│   │   │   ├── interaction/             # Board crosshair & number selection engine
│   │   │   ├── model/                   # SudokuBoard, CellPosition, Candidates
│   │   │   ├── settings/                # Player preferences model
│   │   │   ├── solver/                  # Deterministic MRV backtracking solver
│   │   │   ├── statistics/              # Player stats model
│   │   │   ├── storage/                 # Serialization & repository drivers
│   │   │   ├── tutorial/                # Demo board matrix & multi-scenario setups
│   │   │   └── validator/               # Rule enforcement & conflict detector
│   │   └── ui/
│   │       ├── board/                   # SudokuBoardView, SudokuCellView, MiniBoardPreview
│   │       ├── controls/                # NumberPad, HintCard
│   │       ├── screens/                 # HomeScreen, GameScreen, Stats, Badges,
│   │       │                            # SettingsScreen, TipsScreen, StrategyDemoScreen
│   │       └── theme/                   # Dark theme color palette & styles
│   │
│   ├── src/commonTest/kotlin/com/sudoku/
│   │   └── core/                        # Automated unit test suite (100% passing)
│   └── build.gradle.kts
│
├── settings.gradle.kts
├── build.gradle.kts
└── gradle.properties
```

---

## 🚀 Building & Running

### Prerequisites
* **JDK 17 or 21**
* **Android SDK** (API 34+)
* Connected Android device (USB Debugging enabled) or Android Emulator

### 1. Run Automated Domain Tests
Executes the cross-platform test corpus across mathematical rules, candidate calculations, solver edge cases, uniqueness counting, and selection logic:

```bash
./gradlew desktopTest
```

### 2. Install Debug Build on Device
Deploy directly to your connected device:

```bash
./gradlew :androidApp:installDebug
```

### 3. Assemble Signed Release APK
Generate the production release APK for direct installation:

```bash
./gradlew :androidApp:assembleRelease
```

The output APK will be located at:
```text
androidApp/build/outputs/apk/release/androidApp-release.apk
```

---

## 🗺 Roadmap & Next Phases

* [x] **Phases 0–1**: Board, Position, Bitwise Validator
* [x] **Phase 2**: Candidate Bitmask Engine
* [x] **Phase 3**: Solver & Solution Counter
* [x] **Phase 4**: Puzzle Generator (Symmetric & Seeded)
* [x] **Phase 5**: Solving Techniques & Difficulty Engine
* [x] **Phases 6–8**: Game State, Undo/Redo & Selection Highlighting
* [x] **Phases 9–14**: Compose UI, Number Pad, Notes, Timer & Hints
* [x] **Phase 15**: Disk Persistence & Process Death Survival
* [x] **Phases 16–20**: Home Screen, Daily Challenge, Stats & Wave Animation
* [x] **Phases 21–23**: Settings, Haptics, Badges & Android Adaptive Icons
* [x] **v1.1.0 Polish**: Interactive Strategy Demo boards, Detailed Hints, Data Wipe & Daily Lockout
* [ ] **Phase 24**: iOS / iPadOS Target Configuration (Xcode wrapper, Safe Areas, `NSUserDefaults`)
* [ ] **Phase 25**: Cross-Platform Matrix Parity Tests
* [ ] **Phase 26**: Desktop Prototype & Physical Keyboard Shortcuts (`1–9`, `N`, `Z`, `H`, `Esc`)
* [ ] **Phase 27**: Steamworks Evaluation (Cloud saves, Steam achievements)
* [ ] **Phase 28**: Production Google Play Store Release (`.aab` bundle)
