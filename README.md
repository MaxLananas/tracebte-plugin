# TraceBTE

Interactive tracing tutorial plugin for BuildTheEarth France.  
Guides new players through the fundamentals of real-world building placement using GPS coordinates, WorldEdit, and BTE projection tools.

---

## Requirements

| Dependency | Version      | Type     |
|------------|--------------|----------|
| PaperMC    | 1.21.1+      | Required |
| Java       | 21+          | Required |
| WorldEdit  | 7.3.x        | Required |
| BTE Tools  | Any          | Required |

---

## Installation

1. Download the latest JAR from the [Releases](../../releases) page.
2. Place it in your server's `plugins/` directory.
3. Restart the server.

No configuration file is required.

---

## Commands

| Command      | Description                          |
|--------------|--------------------------------------|
| `/tuto`      | Starts the interactive tutorial.     |
| `/tuto stop` | Interrupts the current tutorial.     |

---

## What the tutorial covers

- Retrieving real-world GPS coordinates from Google Maps.
- Using `/tpll` to teleport to geographic coordinates in the BTE world.
- Placing corner markers to define a building footprint.
- Drawing straight lines between corners using WorldEdit's `//line` command.
- Using WorldEdit selection and `//stack` to build upward.

---

## Build

```bash
./gradlew build
