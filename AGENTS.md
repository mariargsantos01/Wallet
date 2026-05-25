# AGENTS.md

## Project Overview

Android "Wallet" app — a digital card/wallet manager built with **Kotlin**, **Jetpack Compose**, and **Room** (local SQLite). Single-module Gradle project (`app/`). Package: `com.example.wallet`.

## Architecture

```
ui/screens/  →  viewmodel/  →  repository/ (interfaces)  →  data/local/ (Room DAOs + entities)
                                                          →  data/remote/ (Retrofit AuthService)
```

- **No DI framework** — uses a manual `ServiceLocator` singleton (`utils/ServiceLocator.kt`). Call `ServiceLocator.init(context)` before accessing any repository. ViewModels default-inject from `ServiceLocator`.
- **Repository pattern**: interfaces in `repository/` (e.g. `CardRepository`), Room implementations prefixed `Room*`, fake/mock implementations prefixed `Fake*`.
- **UI state**: generic `UiState<T>` data class (`state/UiState.kt`) with `isLoading`, `data`, `error`. All ViewModels expose `StateFlow<UiState<…>>`.
- **Navigation**: sealed class `Routes` in `navigation/Routes.kt`; single `AppNavHost` composable wires all routes.

## Key Conventions

- **Language**: Kotlin only; comments and UI strings in Brazilian Portuguese.
- **Serialization**: `kotlinx.serialization` (not Gson/Moshi). DTOs annotated with `@Serializable`.
- **Database migrations**: currently uses `fallbackToDestructiveMigration()`. Increment `AppDatabase` version and add real `Migration` objects before shipping to production.
- **KSP** (not kapt) for Room annotation processing.
- **Version catalog**: all dependency versions in `gradle/libs.versions.toml`; reference via `libs.*` aliases.

## Build & Run

```sh
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires emulator/device)
./gradlew connectedDebugAndroidTest
```

Minimum SDK 24, target/compile SDK 36, Java 11, Kotlin 2.0.21.

## Adding a New Feature (checklist)

1. **Model** → `model/` (data class, `@Serializable` if used in network).
2. **Entity + DAO** → `data/local/entity/` + `data/local/dao/`. Register entity in `AppDatabase`.
3. **Repository interface** → `repository/`. Implementation `Room*Repository`.
4. **Wire in ServiceLocator** → expose via `val` property with `by lazy`.
5. **ViewModel** → `viewmodel/`, use `UiState<T>`, collect from repository flows.
6. **Screen composable** → `ui/screens/`. Accept navigation lambdas, no direct NavController reference.
7. **Route** → add to `Routes` sealed class + `AppNavHost`.

## Important Files

| Purpose | Path |
|---------|------|
| Dependency versions | `gradle/libs.versions.toml` |
| DI / wiring | `app/…/utils/ServiceLocator.kt` |
| Network config | `app/…/utils/NetworkModule.kt` |
| Room database | `app/…/data/local/AppDatabase.kt` |
| Navigation graph | `app/…/navigation/AppNavHost.kt` |
| Route definitions | `app/…/navigation/Routes.kt` |
| Generic UI state | `app/…/state/UiState.kt` |

