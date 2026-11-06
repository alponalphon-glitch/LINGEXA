# Module 3 — Sentence Explorer (Iteration 17)

- 100 unlockable levels, 10 questions per level.
- Uses the existing custom vector-drawn blue owl mascot (`OwlMascotView`).
- All lesson content, buttons, prompts, and navigation are in English.
- The owl's conversational feedback and encouragement are in Russian.
- Five-heart energy system shared with the app; an incorrect answer removes one heart.
- One heart regenerates every 30 minutes, including while the app is closed.
- At zero hearts the lesson is paused and the refill screen shows the remaining wait.
- Correct answers award 7 XP and 1 Gem; finishing a level awards 20 XP and 4 Gems.
- Level progress is stored using the existing `m3_step_<level>` and `module3_unlocked` preferences.
- Completing all 100 levels unlocks Module 4.

The Android build/device runtime has not been verified in this iteration.
