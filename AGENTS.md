# Repository Guidelines

## Project Structure & Module Organization
- Android app module lives in `app/`; Kotlin sources in `app/src/main/java`, Compose UI in the same tree, and resources in `app/src/main/res`.
- Unit tests reside in `app/src/test/java`; instrumented tests in `app/src/androidTest/java`.
- Repository-wide configs: `build.gradle.kts` (root) for plugin versions, `gradle/libs.versions.toml` for dependency catalog, and `gradle.properties` for JVM/Gradle tuning.
- Product spec and asset rules are documented in `docs/spec.md`; keep widget imagery naming aligned to the spec (e.g., `morning_clear.png`).

## Build, Test, and Development Commands
- `./gradlew :app:assembleDebug` – build a debuggable APK (fastest local iterate path).
- `./gradlew :app:installDebug` – install the debug build on a connected device/emulator.
- `./gradlew :app:lint` – run Android Lint; fix or suppress with justification before merging.
- `./gradlew :app:testDebugUnitTest` – run JVM unit tests.
- `./gradlew :app:connectedAndroidTest` – run instrumented tests on an emulator/device; ensure one is booted.
- `./gradlew :app:clean` – clear build artifacts if Gradle cache issues appear.

## Coding Style & Naming Conventions
- Language: Kotlin; prefer idiomatic, null-safe code and Jetpack Compose for UI.
- Indentation: 4 spaces; keep imports optimized and avoid wildcard imports.
- Compose: favor `@Composable` functions that are small, stateless, and receive data via parameters; preview classes end with `Preview`.
- Naming: classes UpperCamelCase, functions/members lowerCamelCase, constants UPPER_SNAKE_CASE. Widget image assets follow `{time_of_day}_{weather}.png` per `docs/spec.md`.
- Keep Android namespace/applicationId as `com.example.dioramaweather` unless explicitly changing package ownership.

## Testing Guidelines
- Frameworks: JUnit4 for unit tests, Espresso + Compose UI Test for instrumented coverage.
- Name unit test classes with `*Test` and instrumented tests with `*AndroidTest`; mirror package of the code under test.
- Aim to cover Compose UI state changes and data parsing; add regression tests alongside fixes.
- Run `:app:testDebugUnitTest` before pushing; add `:app:connectedAndroidTest` when UI or platform APIs change.

## Commit & Pull Request Guidelines
- Follow Conventional Commits (e.g., `feat: add hourly forecast widget`), mirroring existing history (`chore: init project`).
- One logical change per commit; include brief context in the body if behavior changes or trade-offs were made.
- PRs should include: summary of changes, testing performed (commands + outcomes), linked issue or ticket, and screenshots/recordings for UI-facing updates.
- Keep branches rebased on main; resolve lint/test failures locally before requesting review.

## Configuration & Security Notes
- Do not commit API keys or secrets; keep them in `local.properties` or environment variables and reference via build config fields.
- If adding network integrations (Retrofit/OkHttp per spec), prefer HTTPS endpoints and enable timeouts/retries thoughtfully.
- Before publishing assets, verify filenames and dimensions align with `docs/spec.md` to avoid widget lookup mismatches.
