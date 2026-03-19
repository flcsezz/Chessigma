# Stockfish Engine Setup (Native)

The Chessigma app requires native Stockfish 16 binaries to perform on-device analysis. Due to size constraints, these are not stored in version control.

## Required Binaries
- `app/src/main/jniLibs/arm64-v8a/libstockfish.so`
- `app/src/main/jniLibs/armeabi-v7a/libstockfish.so`

## Obtaining Binaries

### Option A: Manual Download (Recommended)
1. Download the pre-compiled Android binaries from the official [Stockfish repository](https://stockfishchess.org/download/) or a trusted build source like [Lichess Android](https://github.com/lichess-org/lichobile).
2. Ensure you select the shared library version (`.so`).

### Option B: Building from Source
If you need to compile them manually using the Android NDK:
1. Clone the Stockfish source: `git clone https://github.com/official-stockfish/Stockfish.git`
2. Navigate to `src`.
3. Use the following make command (requires NDK in path):
   ```bash
   make build ARCH=armv8-android
   ```
   *Note: Repeat for other architectures as needed.*

## Verification
Upon launch, check the logs via Timber for `StockfishEngine: Initialized successfully`. If the binary is missing or incompatible, the app will fallback to a stubbed mode with reduced functionality.
