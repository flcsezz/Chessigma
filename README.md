# Chessigma

Chessigma is a premium, chess improvement app that marries local Stockfish engine analysis with AI-driven coaching (Gemini/Groq/NVIDIA) to give you targeted, personal puzzles and hard-hitting feedback.

## Features

- **Local Stockfish Analysis**: Full offline game evaluation running Stockfish 16 NNUE on-device.
- **AI Coach Cascade**: Honest post-game insights using Gemini, Groq, or NVIDIA NIM APIs.
- **Personal Puzzles**: Mistakes from your actual games automatically become puzzles "My Mistakes".
- **Bot Play**: Practice against pre-configured Stockfish bots (Novice to Master).
- **Premium Design System**: Dark-mode first with gold and green accents, utilizing Material 3.

## Screenshots

_(Coming soon: placeholder for app screenshots)_

## Setup Instructions

### 1. API Keys

The app uses an AI Cascade. You will need API keys to enable the AI coach feature:

- [Google AI Studio (Gemini 1.5 Flash)](https://aistudio.google.com/)
- [Groq Console (Llama 3.1 8B)](https://console.groq.com/)
- [NVIDIA NIM (Mistral Instruct)](https://build.nvidia.com/)
  Enter these keys on the Settings screen upon first launch.

### 2. Stockfish Binary

The binary is too large for version control.

1. Download `libstockfish.so` for your architectures (arm64-v8a, armeabi-v7a).
2. Place them in `app/src/main/jniLibs/<arch>/libstockfish.so`.

### 3. Lichess Puzzle Database

1. Download `lichess_db_puzzle.csv` from [database.lichess.org](https://database.lichess.org).
2. Extract the first 50,000 rows.
3. Place the file in `app/src/main/assets/puzzles/lichess_db_puzzle.csv`.

## Build Instructions

```bash
./gradlew assembleDebug
```

## Documentation

- [Project Roadmap](docs/PLAN.md)
- [Progress Tracker](docs/PROGRESS.md)
- [Architectural Decisions](docs/DECISIONS.md)
- [Agent Handbook](GEMINI.md)
