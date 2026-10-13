# Sudoku Engine & Cross-Platform Game

A modern, mathematically rigorous, offline-first Sudoku game built using **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**.

Targeted primarily for **Android** and **iOS**, with secondary adaptation for **Desktop / Steam**.

---

## 🏛 Architecture Overview

The codebase strictly decouples platform-independent game logic from platform-specific presentation:

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
                     │  Storage & Statistics Drivers  │
                     └───────────────┬────────────────┘
                                     │
                     ┌───────────────┴────────────────┐
                     │          shared/ui             │
                     │  Compose Multiplatform UI      │
                     │  Canvas Board & 3x3 Notes      │
                     │  NumberPad & Ripple Animations │
                     │  Screens: Home, Game, Stats,   │
                     │           Badges, Settings     │
                     └───────────────┬────────────────┘
                                     │
                 ┌───────────────────┼───────────────────┐
                 ▼                   ▼                   ▼
           androidApp             iosApp             desktopApp
        (Google Play)          (App Store)            (Steam)
```

---

## ✨ Features Implemented

### 1. Mathematics & Core Domain
* **Memory-Efficient Grid**: Backed by a compact contiguous `IntArray(81)` with functional immutable copies for gameplay state and in-place mutation for solver throughput.
* **Pre-Pooled Coordinate System (`CellPosition`)**: Pre-computes and pools all 81 positions (`row`, `column`, `box`, linear `index`), eliminating runtime garbage-collection allocations.
* **Bitmask Candidate Engine (`Candidates`)**: Zero-allocation `@JvmInline value class` backed by a 9-bit bitmask. Executes constraint elimination in $\mathcal{O}(1)$ time.

### 2. Solver & Uniqueness
* **MRV Backtracking Solver**: Uses the *Minimum Remaining Values* (Most Constrained Variable) heuristic to solve standard and tournament-grade hard puzzles (e.g., Arto Inkala’s AI Sudoku) in single-digit milliseconds.
* **Bounded Solution Counter**: Proof of unique solvability (`countSolutions(limit = 2) == 1`) without unbounded search expansion on ambiguous puzzles.

### 3. Generator & Difficulty Grading
* **180° Rotational Symmetry**: Generates elegant, classic tournament-style puzzles by carving clues in symmetric pairs.
* **Deterministic Seeding**: Supports passing a `seed: Long` so any two devices in the world generate the identical puzzle bit-for-bit (powers the **Daily Challenge**).
* **Human-Technique Difficulty Classifier**: Models how humans solve puzzles without brute-force guessing:
  - *Naked Single* (Weight: 1)
  - *Hidden Single* (Weight: 2)
  - *Naked Pair* (Weight: 4)
  - *Pointing Pair / Triple* (Weight: 5)
  - *X-Wing* (Weight: 10)
  - Grades puzzles into **Easy**, **Medium**, **Hard**, and **Expert**.

### 4. Gameplay Engine
* **Undo / Redo Stack**: Deterministic, reversible move tracking.
* **Auto-Cleaning Notes**: Placing a digit automatically removes that candidate note from peer cells across its row, column, and 3×3 box (and cleanly restores them on Undo).
* **Clue Protection & Mistake Tracking**: Immutable given clues; configurable 3-strikes mode or relaxed unlimited practice mode.
* **Exhausted Number Disabling**: Automatically dims and disables keypad buttons once all 9 instances of a digit are correctly placed.

### 5. Board Interaction & Highlighting (Phase 8 Specification)
* **Strict Separation of Highlighting Targets**:
  - Tapping cell $(r, c)$ activates the **local focus crosshair** (highlights that cell's row, column, and box).
  - Placed digit inside that cell activates the **global digit highlight** (highlights all matching numbers and pencil notes).
  - **Does NOT** highlight rows or columns of matching digits in other boxes.

### 6. Visual Polish & Themes
* **Deep Charcoal Dark Theme**: Optimized dark palette (`#0F1117`) with seamless dark status/navigation bar integration.
* **Staggered Ripple Wave**: Completing any Row, Column, or 3×3 Box triggers a gentle 55ms-staggered horizontal wave highlight gliding from left to right.
* **Interactive Hint Engine**: Displays deduction explanations (e.g., *"Hidden Single: 4 can only go here in row 3"*), highlights focal cells in gold/amber, and applies moves in one tap.

### 7. Persistence & Game Systems
* **Crash & Swipe-Kill Survival**: Persists board state, notes, timer, and moves directly to disk via Android `SharedPreferences`. Survives process death and App Switcher swipe-out.
* **Home Screen Navigation**: "Continue Game" banner, difficulty selection, and stats access.
* **Daily Challenge**: Date-seeded worldwide puzzle.
* **Player Statistics**: Tracks win rate, total games, best time, and average time per difficulty.
* **Tactile Haptic Feedback**: Subtle ticks on keypad taps, double warning vibration on errors, and success pulses on unit completion.
* **Achievements System**: Local badges engine (First Victory, Flawless Mind, Speed Demon, Pure Logic, Grandmaster, etc.).
* **Production Android Polish**: Adaptive vector launcher icon with Android 13+ Material You monochrome dynamic theming, and ProGuard / R8 optimization rules.

---

## 📁 Project Directory Structure

```text
sudoku-engine/
├── androidApp/
│   ├── src/main/
│   │   ├── AndroidManifest.xml          # Haptics permission, theme, launcher icon
│   │   ├── kotlin/com/sudoku/android/   # MainActivity, AndroidStorageDriver, AndroidHapticDriver
│   │   └── res/                         # Adaptive vector icons & themes
│   ├── build.gradle.kts
│   └── proguard-rules.pro               # R8 shrinking optimization rules
│
├── shared/
│   ├── src/commonMain/kotlin/com/sudoku/
│   │   ├── core/
│   │   │   ├── achievements/            # Badges model
│   │   │   ├── candidates/              # Bitmask candidate calculator
│   │   │   ├── daily/                   # Calendar-seeded daily puzzle generator
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
│   │   │   └── validator/               # Rule enforcement & conflict detector
│   │   └── ui/
│   │       ├── board/                   # SudokuBoardView, SudokuCellView, NotesGrid
│   │       ├── controls/                # NumberPad, HintCard
│   │       ├── screens/                 # HomeScreen, GameScreen, Stats, Badges, Settings
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
* **Android Studio** (or Android SDK command-line tools installed)
* Connected Android device (with USB debugging enabled) or emulator

### 1. Run Automated Domain Tests
Executes the cross-platform test corpus across mathematical rules, candidate calculations, solver edge cases, uniqueness counting, and selection logic:

```bash
./gradlew desktopTest
```

### 2. Build and Install on Android
Deploy directly to a connected phone or emulator:

```bash
./gradlew :androidApp:installDebug
```

Assemble debug APK manually:
```bash
./gradlew :androidApp:assembleDebug
# Output APK location:
# androidApp/build/outputs/apk/debug/androidApp-debug.apk
```

---

## 🗺 Roadmap & Next Phases

* [x] **Phases 0–1**: Board, Position, Validator
* [x] **Phase 2**: Candidate Bitmask Engine
* [x] **Phase 3**: Solver & Solution Counter
* [x] **Phase 4**: Puzzle Generator
* [x] **Phase 5**: Solving Techniques & Difficulty Engine
* [x] **Phases 6–8**: Game State, Undo/Redo & Selection Highlighting
* [x] **Phases 9–14**: Compose UI, Number Pad, Notes, Timer & Hints
* [x] **Phase 15**: Disk Persistence & Process Death Survival
* [x] **Phases 16–20**: Home Screen, Daily Challenge, Stats & Animations
* [x] **Phases 21–23**: Settings, Haptics, Badges & Android Adaptive Icons
* [ ] **Phase 24**: iOS / iPadOS Target Configuration (Xcode wrapper, Safe Areas, `NSUserDefaults`)
* [ ] **Phase 25**: Cross-Platform Matrix Parity Tests
* [ ] **Phase 26**: Desktop Prototype & Physical Keyboard Shortcuts (`1–9`, `N`, `Z`, `H`, `Esc`)
* [ ] **Phase 27**: Steamworks Evaluation (Cloud saves, Steam achievements)
* [ ] **Phase 28**: Production Google Play Store Release (`.aab` bundle)
