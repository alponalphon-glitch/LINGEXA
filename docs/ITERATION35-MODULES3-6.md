# LINGEXA Iteration 35 — Modules 3–6 curriculum repair

## Changes made
- Module 3 now uses ten different topic-aware example sentence patterns in each of its ten units, instead of repeating generic placeholder frames.
- Module 4 word lookup was repaired: each of its 100 curriculum rows contains ten words, and the lesson now selects the correct word by level and question index. The previous indexing could return a whole row and then run out of array bounds.
- Modules 4, 5 and 6 now use three separate 1,000-word English vocabulary tiers. The banks contain exactly 1,000 unique entries each, so all 100 levels × 10 questions can be addressed without an array overrun.
- Modules 4 and 5 now request real English dictionary definitions, like Module 6. The previous generic placeholder meanings in Module 5 were removed.
- Dictionary requests run on a background thread. If the device is offline or a word has no entry, the UI says the definition is unavailable instead of inventing one.
- Progression, saved question steps, level locks, XP/Gems rewards, heart handling and Premium heart behavior were preserved.
- A pre-edit MainActivity backup is stored in `backups/MainActivity.java.before-iteration35-modules3-6`.

## Verification
- Static checks confirm Module 4 has 100 rows × 10 words.
- Static checks confirm Module 5 has 1,000 unique entries and Module 6 has 1,000 unique entries.
- Java syntax scan found no syntax diagnostics; a full Android compile was not possible in this environment because the Android SDK/Gradle wrapper is not present.

## Known limitation
This iteration repairs and expands the current English curriculum. The Iteration 34 source did not contain a working English/German/Russian course selector or localized curriculum data, despite the earlier UI description. German and Russian target-language lessons still need a separate proper curriculum implementation; they are not represented as completed here.
