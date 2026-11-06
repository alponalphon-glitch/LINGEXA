# Speaking Journey

The fourth bottom navigation tab is now Speaking. It contains a guided six-stage CEFR speaking course:

- A1 Beginner
- A2 Elementary
- B1 Intermediate
- B2 Upper-Intermediate
- C1 Advanced
- C2 Proficient

Each stage contains ten spoken sentence missions. The learner listens to the blue owl's English model, can replay it slowly, then uses Android speech recognition to repeat the sentence. The recognized text is compared with the target sentence using a simple word-overlap score. At 65% or higher the lesson is marked complete, 8 XP is awarded, and progress is stored in SharedPreferences. A stage unlocks only after all ten missions in the previous stage are completed.

The owl's coaching feedback is spoken in Russian; lesson prompts and speech recognition use English (en-US).

## Important limitations

This is guided speech practice, not a live generative AI conversation. Recognition depends on an installed Android speech-recognition service and network/device support. The similarity score is a basic text comparison; it is not phoneme-level pronunciation assessment and may misjudge accents or speech-recognition errors. TTS requires installed English and Russian voices.
