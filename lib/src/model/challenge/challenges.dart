import 'dart:async';

import 'package:fast_immutable_collections/fast_immutable_collections.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:chessigma_mobile/src/model/challenge/challenge.dart';
import 'package:chessigma_mobile/src/model/challenge/challenge_repository.dart';

final challengesProvider = AsyncNotifierProvider.autoDispose<Challenges, ChallengesList>(
  Challenges.new,
  name: 'ChallengesProvider',
);

class Challenges extends AsyncNotifier<ChallengesList> {
  @override
  Future<ChallengesList> build() {
    return Future.value((
      inward: const IList<Challenge>.empty(),
      outward: const IList<Challenge>.empty(),
    ));
  }
}
