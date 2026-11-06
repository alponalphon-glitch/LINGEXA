# LINGEXA Android

LINGEXA is an offline-first Android language-learning prototype built with native Java views.

## Current visual iteration
Iteration 36 introduces a cinematic navy interface, layered 2.5D cards, subtle touch parallax, short screen transitions, refined five-tab navigation and a locally drawn animated owl mascot. The rendering uses Android Canvas, gradients and property animations without adding a heavyweight 3D engine or external runtime assets.

## Build
Open this folder as an Android Studio project and build the `:app` module. The project uses Android Gradle Plugin 8.9.2, compileSdk 35 and minSdk 26.

## Notes
Progress is stored locally with SharedPreferences. Some integrations described in the UI remain prototypes and require their own backend or billing setup. See `docs/ITERATION36-VISUAL-UPGRADE.md` for this iteration's exact scope and verification status.
