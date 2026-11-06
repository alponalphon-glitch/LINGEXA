# Iteration 24 — Strict sequential level unlocking

- Each of Modules 1–6 starts with Level 1 unlocked.
- A level's next level is unlocked only in the completion handler after all 10 questions have been answered correctly and the level-complete action is confirmed.
- Level grids show locked levels with a lock icon and reject taps on them.
- Each game entry point independently checks the unlock boundary, so a level cannot be opened by bypassing the level grid.
- Completing all 100 levels of a module unlocks Level 1 of the next module, following the existing module progression.
- Existing saved progress is preserved; the stored unlock boundary is clamped to Levels 1–100.
