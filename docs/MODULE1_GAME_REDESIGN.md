# Module 1 — First Steps: Game Redesign

- Reworked the lesson flow into one-question-at-a-time level gameplay.
- Added a blue bird companion (`🐦`) that introduces each question and gives immediate Russian conversational feedback for correct and incorrect answers.
- Kept all Module 1 interface labels, buttons, questions, hints, and learning content in English. Russian is used only for the bird's conversational messages and feedback. No Tajik text is used in Module 1.
- Added a five-heart energy system. A wrong answer removes one heart; at zero hearts the learner is paused from answering.
- Hearts regenerate automatically at one heart per 30 minutes, up to five. Recovery is calculated from a saved timestamp and continues while the app is closed.
- Correct answers award XP and Gems. Completing all ten questions unlocks the next level.
- Existing Module 1 progress keys are preserved.

Build status: source/archive checks only; Android Gradle build was not run in this environment.
