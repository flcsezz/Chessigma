package com.chessigma.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import com.chessigma.app.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chessigma.app.domain.model.*
import androidx.compose.ui.tooling.preview.Preview
import com.chessigma.app.ui.theme.ChessigmaTheme
import kotlin.math.roundToInt

@Stable
class ChessboardState(
    initialBoard: ChessBoard = ChessBoard.empty(),
    initialIsFlipped: Boolean = false,
    initialBoardTheme: BoardTheme = BoardTheme.WOOD,
    initialPieceSet: PieceSet = PieceSet.DEFAULT
) {
    var board by mutableStateOf(initialBoard)
    var isFlipped by mutableStateOf(initialIsFlipped)
    var selectedSquare by mutableStateOf<String?>(null)
    var lastMove by mutableStateOf<ChessMove?>(null)
    var legalMoves by mutableStateOf<List<String>>(emptyList())
    var boardTheme by mutableStateOf(initialBoardTheme)
    var pieceSet by mutableStateOf(initialPieceSet)
}

@Composable
fun rememberChessboardState(
    board: ChessBoard = ChessBoard.empty(),
    isFlipped: Boolean = false,
    boardTheme: BoardTheme = BoardTheme.WOOD,
    pieceSet: PieceSet = PieceSet.DEFAULT
) = remember(board, isFlipped, boardTheme, pieceSet) {
    ChessboardState(board, isFlipped, boardTheme, pieceSet)
}

@Composable
fun Chessboard(
    state: ChessboardState,
    onMove: (ChessMove) -> Unit,
    modifier: Modifier = Modifier,
    onSquareClick: (String) -> Unit = {}
) {
    var draggingSquare by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    
    // Track previous pieces to animate captures
    var previousPieces by remember { mutableStateOf<Map<String, ChessPiece>>(emptyMap()) }
    val currentPieces = state.board.pieces
    
    // Update previous pieces when board changes
    LaunchedEffect(state.board) {
        // We only care about pieces that were removed (captured)
        // pieces that moved are handled by animateIntOffsetAsState
        previousPieces = currentPieces
    }

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(16.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(state.boardTheme.darkSquare)
    ) {
        val boardWidth = maxWidth
        val squareSize = boardWidth / 8
        val squareSizePx = with(LocalDensity.current) { squareSize.toPx() }

        // Draw the board grid
        BoardGrid(
            isFlipped = state.isFlipped,
            selectedSquare = state.selectedSquare,
            lastMove = state.lastMove,
            legalMoves = state.legalMoves,
            squareSize = squareSize,
            theme = state.boardTheme,
            onSquareClick = {
                state.selectedSquare = it
                onSquareClick(it)
            }
        )

        // Identify captured pieces to animate them out
        val capturedPieces = remember(state.board) {
            val captured = mutableMapOf<String, ChessPiece>()
            previousPieces.forEach { (sq, piece) ->
                if (!currentPieces.containsKey(sq) || currentPieces[sq] != piece) {
                    // Piece was at sq, but now it's gone or replaced
                    // If it was replaced, it might have been captured or moved
                    // In chess, if a move happened TO this square, the piece was captured.
                    if (state.lastMove?.toSquare == sq) {
                        captured[sq] = piece
                    }
                }
            }
            captured
        }

        // Draw captured pieces (animating out)
        capturedPieces.forEach { (squareName, piece) ->
            val file = squareName[0] - 'a'
            val rank = squareName[1] - '1'
            val displayFile = if (state.isFlipped) 7 - file else file
            val displayRank = if (state.isFlipped) rank else 7 - rank
            val targetOffset = IntOffset(
                (displayFile * squareSizePx).roundToInt(),
                (displayRank * squareSizePx).roundToInt()
            )

            key("captured", piece, squareName) {
                Piece(
                    piece = piece,
                    squareSize = squareSize,
                    pieceSet = state.pieceSet,
                    isVisible = false, // Trigger exit animation
                    modifier = Modifier.offset { targetOffset }
                )
            }
        }

        // Draw current pieces
        currentPieces.forEach { (squareName, piece) ->
            if (squareName != draggingSquare) {
                val file = squareName[0] - 'a'
                val rank = squareName[1] - '1'

                val displayFile = if (state.isFlipped) 7 - file else file
                val displayRank = if (state.isFlipped) rank else 7 - rank

                val targetOffset = IntOffset(
                    (displayFile * squareSizePx).roundToInt(),
                    (displayRank * squareSizePx).roundToInt()
                )

                val animatedOffset by animateIntOffsetAsState(
                    targetValue = targetOffset,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "PieceMoveAnimation"
                )

                key(piece, squareName) {
                    Piece(
                        piece = piece,
                        squareSize = squareSize,
                        pieceSet = state.pieceSet,
                        isVisible = true,
                        modifier = Modifier
                            .offset { animatedOffset }
                            .pointerInput(squareName, state.isFlipped) {
                                detectDragGestures(
                                    onDragStart = {
                                        draggingSquare = squareName
                                        dragOffset = Offset.Zero
                                        state.selectedSquare = squareName
                                        onSquareClick(squareName)
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffset += dragAmount
                                    },
                                    onDragEnd = {
                                        val targetFile = if (state.isFlipped) {
                                            7 - ((displayFile * squareSizePx + dragOffset.x) / squareSizePx).roundToInt()
                                        } else {
                                            ((displayFile * squareSizePx + dragOffset.x) / squareSizePx).roundToInt()
                                        }
                                        val targetRank = if (state.isFlipped) {
                                            ((displayRank * squareSizePx + dragOffset.y) / squareSizePx).roundToInt()
                                        } else {
                                            7 - ((displayRank * squareSizePx + dragOffset.y) / squareSizePx).roundToInt()
                                        }

                                        if (targetFile in 0..7 && targetRank in 0..7) {
                                            val fileChar = ('a' + targetFile)
                                            val rankChar = ('1' + targetRank)
                                            val targetSquare = "$fileChar$rankChar"
                                            if (targetSquare != squareName) {
                                                onMove(ChessMove(squareName, targetSquare))
                                            }
                                        }
                                        draggingSquare = null
                                        dragOffset = Offset.Zero
                                    },
                                    onDragCancel = {
                                        draggingSquare = null
                                        dragOffset = Offset.Zero
                                    }
                                )
                            }
                            .clickable { 
                                state.selectedSquare = squareName
                                onSquareClick(squareName) 
                            }
                    )
                }
            }
        }

        // Draw the piece being dragged on top
        draggingSquare?.let { squareName ->
            val piece = state.board.pieces[squareName] ?: return@let
            val file = squareName[0] - 'a'
            val rank = squareName[1] - '1'

            val displayFile = if (state.isFlipped) 7 - file else file
            val displayRank = if (state.isFlipped) rank else 7 - rank

            Piece(
                piece = piece,
                squareSize = squareSize,
                pieceSet = state.pieceSet,
                isVisible = true,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (displayFile * squareSizePx + dragOffset.x).roundToInt(),
                            (displayRank * squareSizePx + dragOffset.y).roundToInt()
                        )
                    }
            )
        }

        // Coordinates
        BoardCoordinates(state.isFlipped, squareSize)
    }
}

@Composable
private fun BoardGrid(
    isFlipped: Boolean,
    selectedSquare: String?,
    lastMove: ChessMove?,
    legalMoves: List<String>,
    squareSize: Dp,
    theme: BoardTheme,
    onSquareClick: (String) -> Unit
) {
    Column {
        for (rankIdx in 0..7) {
            Row {
                for (fileIdx in 0..7) {
                    val visualRank = if (isFlipped) rankIdx else 7 - rankIdx
                    val visualFile = if (isFlipped) 7 - fileIdx else fileIdx
                    val squareName = "${'a' + visualFile}${visualRank + 1}"

                    val isLight = (visualFile + visualRank) % 2 != 0
                    val bgColor = when {
                        squareName == selectedSquare -> theme.selectedSquare
                        squareName == lastMove?.fromSquare || squareName == lastMove?.toSquare -> theme.lastMoveSquare
                        isLight -> theme.lightSquare
                        else -> theme.darkSquare
                    }

                    Box(
                        modifier = Modifier
                            .size(squareSize)
                            .background(bgColor)
                            .clickable { onSquareClick(squareName) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (legalMoves.contains(squareName)) {
                            Canvas(modifier = Modifier.size(squareSize / 3.5f)) {
                                drawCircle(color = theme.lastMoveSquare.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun Piece(
    piece: ChessPiece,
    squareSize: Dp,
    pieceSet: PieceSet,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val drawableRes = when (piece.color) {
        PieceColor.WHITE -> when (piece.type) {
            PieceType.PAWN -> R.drawable.ic_white_pawn
            PieceType.KNIGHT -> R.drawable.ic_white_knight
            PieceType.BISHOP -> R.drawable.ic_white_bishop
            PieceType.ROOK -> R.drawable.ic_white_rook
            PieceType.QUEEN -> R.drawable.ic_white_queen
            PieceType.KING -> R.drawable.ic_white_king
        }
        PieceColor.BLACK -> when (piece.type) {
            PieceType.PAWN -> R.drawable.ic_black_pawn
            PieceType.KNIGHT -> R.drawable.ic_black_knight
            PieceType.BISHOP -> R.drawable.ic_black_bishop
            PieceType.ROOK -> R.drawable.ic_black_rook
            PieceType.QUEEN -> R.drawable.ic_black_queen
            PieceType.KING -> R.drawable.ic_black_king
        }
    }

    val colorFilter = when (pieceSet) {
        PieceSet.NEON -> {
            val tint = if (piece.color == PieceColor.WHITE) Color(0xFF00FFFF) else Color(0xFFFF00FF)
            ColorFilter.tint(tint)
        }
        PieceSet.STARK -> {
            val tint = if (piece.color == PieceColor.WHITE) Color.White else Color.Black
            ColorFilter.tint(tint)
        }
        else -> null
    }

    Box(
        modifier = modifier.size(squareSize),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn(initialScale = 0.8f) + fadeIn(),
            exit = scaleOut(targetScale = 0.5f) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = "${piece.color} ${piece.type}",
                colorFilter = colorFilter,
                modifier = Modifier.fillMaxSize(0.9f)
            )
        }
    }
}

@Composable
private fun BoardCoordinates(isFlipped: Boolean, squareSize: Dp) {
    val textColor = Color(0xFFFAFAF9).copy(alpha = 0.6f) // Subtle text color
    val fontSize = 10.sp
    val fontWeight = FontWeight.SemiBold

    // Files (a-h)
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Bottom
    ) {
        for (i in 0..7) {
            val file = if (isFlipped) 'h' - i else 'a' + i
            Box(
                modifier = Modifier.size(squareSize),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = file.toString(),
                    color = textColor,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }

    // Ranks (1-8)
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        for (i in 0..7) {
            val rank = if (isFlipped) i + 1 else 8 - i
            Box(
                modifier = Modifier.size(squareSize),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = rank.toString(),
                    color = textColor,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}
