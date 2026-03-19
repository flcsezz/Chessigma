# Current Active Task

**Current Agent**: OpenCode (CLI Assistant)
**Task**: Build Environment Setup & Verification

## Objectives
- [x] Install Java 17 (OpenJDK 17.0.10 Temurin).
- [x] Configure project to use Java 17 (.java-version file).
- [x] Verify Java 17 installation and compiler.
- [ ] Install Android SDK (requires `yay -S android-studio`).
- [ ] Create `local.properties` with SDK path.
- [ ] Run successful Gradle build.

## Context
Java 17 has been installed and verified. The Gradle build attempt revealed that the Android SDK is missing. The project build configuration is already set to use Java 17 (lines 39-47 in `app/build.gradle.kts`). Next step is to install the Android SDK and complete the build.

## Next Step
1. Install Android Studio via `yay -S android-studio`.
2. Set `ANDROID_HOME` environment variable or create `local.properties` with SDK path.
3. Run `./gradlew build --no-daemon` to verify successful build.
