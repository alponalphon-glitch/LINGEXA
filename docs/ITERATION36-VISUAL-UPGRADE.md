# LINGEXA — Iteration 36: Cinematic 2.5D visual upgrade

## Visual work applied
- Reworked the global app shell with a darker cinematic navy gradient, refined header and layered wallet badge.
- Rebuilt the five-item bottom navigation with raised gradient icon tiles, clearer active state and tap feedback.
- Added short native view entrance transitions to screen roots, cards, headings, buttons and home tiles.
- Added lightweight touch parallax to major cards and home tiles using small X/Y rotations and elevation changes.
- Refined the home hero, progress strip, continue-learning card, skill tiles and Premium card.
- Replaced the owl drawing with a layered, locally rendered 2.5D mascot: gradient feathers, glossy highlights, eye movement, blinking, gentle wing motion and idle bobbing.
- Kept visuals native to Android Canvas/View/Drawable; no 3D engine, network images, external libraries or runtime downloads were added.
- Updated theme chrome and incremented app version to 1.36.0 / versionCode 36.

## Performance choices
- The owl is drawn with native Canvas gradients and a ~29 FPS idle redraw rather than a continuous 60 FPS render loop.
- Screen transitions are short property animations; no blur, particle field, video background, shader pipeline or heavyweight 3D renderer is used.
- Scrollbars/overscroll visuals are reduced to keep the learning screens clean.

## Functional preservation
- Existing learning flows, modules, progress preferences, profile, Premium demo, Text-to-Speech and speaking code were not intentionally rewritten.
- This iteration is a visual-layer update, not a claim that unfinished services (such as a live AI backend or real billing) have been connected.

## Verification status
- Source-level edits and project structure were inspected.
- APK build and device performance testing were not run in this environment because Android SDK / Gradle are unavailable here.
