import 'dart:math' as math;
import 'package:flutter/material.dart';

/// Animated splash screen shown while the app initializes.
///
/// Plays a staggered animation:
/// 1. Background fades in (0–300 ms)
/// 2. Logo scales + fades in (300–800 ms)
/// 3. Wordmark slides up + fades in (600–1000 ms)
/// 4. Tagline fades in (900–1200 ms)
/// 5. Sigma particle burst (800–1400 ms)
///
/// The widget stays visible until [isReady] becomes true, then fades out.
class ChessigmaSplashScreen extends StatefulWidget {
  const ChessigmaSplashScreen({
    super.key,
    required this.isReady,
    required this.child,
  });

  /// When this becomes true the splash fades out and [child] is shown.
  final bool isReady;

  /// The real app content to show once the splash is done.
  final Widget child;

  @override
  State<ChessigmaSplashScreen> createState() => _ChessigmaSplashScreenState();
}

class _ChessigmaSplashScreenState extends State<ChessigmaSplashScreen>
    with TickerProviderStateMixin {
  // ── Intro controllers ────────────────────────────────────────────────────
  late final AnimationController _bgCtrl;
  late final AnimationController _logoCtrl;
  late final AnimationController _wordmarkCtrl;
  late final AnimationController _taglineCtrl;
  late final AnimationController _particleCtrl;

  // ── Outro controller ─────────────────────────────────────────────────────
  late final AnimationController _exitCtrl;

  // ── Derived animations ────────────────────────────────────────────────────
  late final Animation<double> _bgOpacity;
  late final Animation<double> _logoScale;
  late final Animation<double> _logoOpacity;
  late final Animation<double> _wordmarkOpacity;
  late final Animation<Offset> _wordmarkSlide;
  late final Animation<double> _taglineOpacity;
  late final Animation<double> _particleProgress;
  late final Animation<double> _exitOpacity;

  bool _showSplash = true;

  @override
  void initState() {
    super.initState();

    // ── Background ──────────────────────────────────────────────────────────
    _bgCtrl = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 400),
    );
    _bgOpacity = CurvedAnimation(parent: _bgCtrl, curve: Curves.easeIn);

    // ── Logo ────────────────────────────────────────────────────────────────
    _logoCtrl = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 700),
    );
    _logoScale = Tween<double>(begin: 0.5, end: 1.0).animate(
      CurvedAnimation(parent: _logoCtrl, curve: Curves.elasticOut),
    );
    _logoOpacity = CurvedAnimation(parent: _logoCtrl, curve: const Interval(0, 0.5));

    // ── Wordmark ────────────────────────────────────────────────────────────
    _wordmarkCtrl = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 500),
    );
    _wordmarkOpacity = CurvedAnimation(parent: _wordmarkCtrl, curve: Curves.easeIn);
    _wordmarkSlide = Tween<Offset>(
      begin: const Offset(0, 0.4),
      end: Offset.zero,
    ).animate(CurvedAnimation(parent: _wordmarkCtrl, curve: Curves.easeOut));

    // ── Tagline ─────────────────────────────────────────────────────────────
    _taglineCtrl = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 400),
    );
    _taglineOpacity = CurvedAnimation(parent: _taglineCtrl, curve: Curves.easeIn);

    // ── Particles ───────────────────────────────────────────────────────────
    _particleCtrl = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 800),
    );
    _particleProgress = CurvedAnimation(parent: _particleCtrl, curve: Curves.easeOut);

    // ── Exit ─────────────────────────────────────────────────────────────────
    _exitCtrl = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 400),
    );
    _exitOpacity = Tween<double>(begin: 1.0, end: 0.0).animate(
      CurvedAnimation(parent: _exitCtrl, curve: Curves.easeIn),
    );

    _runIntroSequence();
  }

  Future<void> _runIntroSequence() async {
    await Future<void>.delayed(const Duration(milliseconds: 100));
    await _bgCtrl.forward();

    // Logo and particles start together
    _particleCtrl.forward();
    await _logoCtrl.forward();

    await Future<void>.delayed(const Duration(milliseconds: 50));
    await _wordmarkCtrl.forward();

    await Future<void>.delayed(const Duration(milliseconds: 100));
    await _taglineCtrl.forward();
  }

  @override
  void didUpdateWidget(ChessigmaSplashScreen oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (widget.isReady && !oldWidget.isReady) {
      _runExit();
    }
  }

  Future<void> _runExit() async {
    // Minimum display time so the animation is always seen
    await Future<void>.delayed(const Duration(milliseconds: 600));
    await _exitCtrl.forward();
    if (mounted) {
      setState(() => _showSplash = false);
    }
  }

  @override
  void dispose() {
    _bgCtrl.dispose();
    _logoCtrl.dispose();
    _wordmarkCtrl.dispose();
    _taglineCtrl.dispose();
    _particleCtrl.dispose();
    _exitCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (!_showSplash) return widget.child;

    return Stack(
      children: [
        widget.child,
        FadeTransition(
          opacity: _exitOpacity,
          child: _SplashContent(
            bgOpacity: _bgOpacity,
            logoScale: _logoScale,
            logoOpacity: _logoOpacity,
            wordmarkOpacity: _wordmarkOpacity,
            wordmarkSlide: _wordmarkSlide,
            taglineOpacity: _taglineOpacity,
            particleProgress: _particleProgress,
          ),
        ),
      ],
    );
  }
}

// ── Inner stateless content ──────────────────────────────────────────────────

class _SplashContent extends StatelessWidget {
  const _SplashContent({
    required this.bgOpacity,
    required this.logoScale,
    required this.logoOpacity,
    required this.wordmarkOpacity,
    required this.wordmarkSlide,
    required this.taglineOpacity,
    required this.particleProgress,
  });

  final Animation<double> bgOpacity;
  final Animation<double> logoScale;
  final Animation<double> logoOpacity;
  final Animation<double> wordmarkOpacity;
  final Animation<Offset> wordmarkSlide;
  final Animation<double> taglineOpacity;
  final Animation<double> particleProgress;

  static const _bgDark = Color(0xFF0A0E1A);
  static const _gold = Color(0xFFE8B84B);
  static const _goldLight = Color(0xFFF5CC72);

  @override
  Widget build(BuildContext context) {
    return FadeTransition(
      opacity: bgOpacity,
      child: ColoredBox(
        color: _bgDark,
        child: Stack(
          alignment: Alignment.center,
          children: [
            // ── Chessboard pattern (subtle) ────────────────────────────────
            const _ChessboardPattern(),

            // ── Spotlight glow ─────────────────────────────────────────────
            AnimatedBuilder(
              animation: logoOpacity,
              builder: (context, _) => Opacity(
                opacity: logoOpacity.value * 0.6,
                child: Container(
                  width: 300,
                  height: 300,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    gradient: RadialGradient(
                      colors: [
                        _gold.withAlpha(60),
                        Colors.transparent,
                      ],
                    ),
                  ),
                ),
              ),
            ),

            // ── Sigma particles ────────────────────────────────────────────
            AnimatedBuilder(
              animation: particleProgress,
              builder: (_, _) => CustomPaint(
                painter: _SigmaParticlePainter(progress: particleProgress.value),
                size: const Size(300, 300),
              ),
            ),

            // ── Main column ────────────────────────────────────────────────
            Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                // Logo image
                ScaleTransition(
                  scale: logoScale,
                  child: FadeTransition(
                    opacity: logoOpacity,
                    child: Image.asset(
                      'assets/images/chessigma-logo.png',
                      width: 140,
                      height: 140,
                    ),
                  ),
                ),

                const SizedBox(height: 24),

                // Wordmark
                SlideTransition(
                  position: wordmarkSlide,
                  child: FadeTransition(
                    opacity: wordmarkOpacity,
                    child: ShaderMask(
                      shaderCallback: (bounds) => const LinearGradient(
                        colors: [_goldLight, _gold],
                        begin: Alignment.topCenter,
                        end: Alignment.bottomCenter,
                      ).createShader(bounds),
                      child: const Text(
                        'Chessigma',
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 38,
                          fontWeight: FontWeight.w700,
                          letterSpacing: 1.5,
                        ),
                      ),
                    ),
                  ),
                ),

                const SizedBox(height: 8),

                // Sigma divider
                SlideTransition(
                  position: wordmarkSlide,
                  child: FadeTransition(
                    opacity: wordmarkOpacity,
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Container(width: 40, height: 1, color: _gold.withAlpha(128)),
                        const Padding(
                          padding: EdgeInsets.symmetric(horizontal: 10),
                          child: Text(
                            'Σ',
                            style: TextStyle(color: _gold, fontSize: 18, height: 1),
                          ),
                        ),
                        Container(width: 40, height: 1, color: _gold.withAlpha(128)),
                      ],
                    ),
                  ),
                ),

                const SizedBox(height: 14),

                // Tagline
                FadeTransition(
                  opacity: taglineOpacity,
                  child: Text(
                    'Play. Analyze. Master.',
                    style: TextStyle(
                      color: Colors.white.withAlpha(178),
                      fontSize: 13,
                      letterSpacing: 2.0,
                      fontWeight: FontWeight.w300,
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

// ── Subtle chessboard background ─────────────────────────────────────────────

class _ChessboardPattern extends StatelessWidget {
  const _ChessboardPattern();

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        return CustomPaint(
          painter: _ChessboardPainter(),
          size: Size(constraints.maxWidth, constraints.maxHeight),
        );
      },
    );
  }
}

class _ChessboardPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    const cellSize = 48.0;
    final paint = Paint()..color = const Color(0xFF121828);

    final cols = (size.width / cellSize).ceil() + 1;
    final rows = (size.height / cellSize).ceil() + 1;

    for (var row = 0; row < rows; row++) {
      for (var col = 0; col < cols; col++) {
        if ((row + col).isEven) {
          canvas.drawRect(
            Rect.fromLTWH(col * cellSize, row * cellSize, cellSize, cellSize),
            paint,
          );
        }
      }
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}

// ── Sigma particle burst painter ──────────────────────────────────────────────

class _SigmaParticlePainter extends CustomPainter {
  const _SigmaParticlePainter({required this.progress});

  final double progress;

  static const _gold = Color(0xFFE8B84B);
  static const _particleCount = 12;

  @override
  void paint(Canvas canvas, Size size) {
    if (progress <= 0) return;

    final center = Offset(size.width / 2, size.height / 2);
    final maxRadius = size.width * 0.55;
    final paint = Paint()..style = PaintingStyle.fill;

    for (var i = 0; i < _particleCount; i++) {
      final angle = (i / _particleCount) * math.pi * 2;
      final radius = maxRadius * progress;
      final particleOpacity = (1.0 - progress).clamp(0.0, 1.0);
      final particleSize = (3.0 + (i % 3) * 1.5) * (1 - progress * 0.5);

      final x = center.dx + math.cos(angle) * radius;
      final y = center.dy + math.sin(angle) * radius;

      paint.color = _gold.withAlpha((particleOpacity * 200).round());
      canvas.drawCircle(Offset(x, y), particleSize, paint);
    }
  }

  @override
  bool shouldRepaint(covariant _SigmaParticlePainter oldDelegate) =>
      oldDelegate.progress != progress;
}
