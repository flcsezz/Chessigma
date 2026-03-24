# Chessigma Mobile

Chessigma mobile app. Focused on local play, puzzles, and learning.

## How to contribute

Contributions to this project are welcome!

If you want to contribute, please read the [contributing guide](./CONTRIBUTING.md).

## Setup

tl;dr: Install Flutter, clone the repo, run in order:
- `flutter pub get`
- `dart run build_runner watch`
- `flutter analyze --watch`,

and you're ready to code!

See [the dev environment docs](./docs/setting_dev_env.md) for detailed instructions.

## Running the app

To run the app, you can use the following command:

```bash
# if not already done, run the code generation
dart run build_runner build

# run the app on all available devices
flutter run -d all
```

## Running tests

To run the tests, you can use the following command:

```bash
# if not already done, run the code generation
dart run build_runner build

flutter test
```

## Internationalisation

Do not edit the `app_en.arb` file by hand, this file is generated.
For more information, see [Internationalisation](./docs/internationalisation.md).
