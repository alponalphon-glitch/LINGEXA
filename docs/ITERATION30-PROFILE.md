# Iteration 30 — Learner profile and five-tab navigation

- Replaced the six-item bottom navigation with five tabs: Home, Learn, Sounds, Speaking, Profile.
- The fifth tab opens the learner profile; the former sixth Menu tab is removed.
- Added profile photo selection through Android's document picker and persistent URI permission.
- Added a chooser with 20 built-in emoji avatars; selecting an avatar clears the custom photo.
- Added editable learner name, CEFR level, gems, XP, streak, hearts, daily lesson count, daily goal indicator, detailed progress and achievements shortcuts.
- Profile image/avatar and learner name are stored locally in SharedPreferences.
- Existing learning/progress data and curriculum were left intact.

Build verification was not run in this environment because Gradle is not installed and the project has no Gradle wrapper.
