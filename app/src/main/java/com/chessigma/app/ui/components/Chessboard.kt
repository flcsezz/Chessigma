package com.chessigma.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import com.chessigma.app.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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

@Preview(showBackground = true)
@Composable
fun ChessboardPreview() {
    ChessigmaTheme {
        val board = ChessBoard(
            pieces = mapOf(
                "e2" to ChessPiece(PieceType.PAWN, PieceColor.WHITE),
                "e4" to ChessPiece(PieceType.PAWN, PieceColor.WHITE),
                "e7" to ChessPiece(PieceType.PAWN, PieceColor.BLACK),
                "e5" to ChessPiece(PieceType.PAWN, PieceColor.BLACK),
                "g1" to ChessPiece(PieceType.KNIGHT, PieceColor.WHITE),
                "b8" to ChessPiece(PieceType.KNIGHT, PieceColor.BLACK)
            ),
            sideToMove = PieceColor.WHITE,
            isCheck = false,
            isCheckmate = false,
            isDraw = false,
            fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        )
        val state = rememberChessboardState(board)
        Chessboard(state = state, onMove = {})
    }
}

@Preview(showBackground = true)
@Composable
fun ChessboardFlippedPreview() {
    ChessigmaTheme {
        val board = ChessBoard(
            pieces = mapOf(
                "e2" to ChessPiece(PieceType.PAWN, PieceColor.WHITE),
                "e4" to ChessPiece(PieceType.PAWN, PieceColor.WHITE),
                "e7" to ChessPiece(PieceType.PAWN, PieceColor.BLACK),
                "e5" to ChessPiece(PieceType.PAWN, PieceColor.BLACK),
                "g1" to ChessPiece(PieceType.KNIGHT, PieceColor.WHITE),
                "b8" to ChessPiece(PieceType.KNIGHT, PieceColor.BLACK)
            ),
            sideToMove = PieceColor.WHITE,
            isCheck = false,
            isCheckmate = false,
            isDraw = false,
            fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        )
        val state = rememberChessboardState(board, isFlipped = true)
        Chessboard(state = state, onMove = {})
    }
}

@Stable
class ChessboardState(
    initialBoard: ChessBoard = ChessBoard.empty(),
    initialIsFlipped: Boolean = false
) {
    var board by mutableStateOf(initialBoard)
    var isFlipped by mutableStateOf(initialIsFlipped)
    var selectedSquare by mutableStateOf<String?>(null)
    var lastMove by mutableStateOf<ChessMove?>(null)
    var legalMoves by mutableStateOf<List<String>>(emptyList())
}

@Composable
fun rememberChessboardState(
    board: ChessBoard = ChessBoard.empty(),
    isFlipped: Boolean = false
) = remember {
    ChessboardState(board, isFlipped)
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

    BoxWithConstraints(modifier = modifier.aspectRatio(1f)) {
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
            onSquareClick = {
                state.selectedSquare = it
                onSquareClick(it)
            }
        )

        // Draw pieces (except the one being dragged)
        state.board.pieces.forEach { (squareName, piece) ->
            if (squareName != draggingSquare) {
                val file = squareName[0] - 'a'
                val rank = squareName[1] - '1'

                val displayFile = if (state.isFlipped) 7 - file else file
                val displayRank = if (state.isFlipped) rank else 7 - rank

                Piece(
                    piece = piece,
                    squareSize = squareSize,
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (displayFile * squareSizePx).roundToInt(),
                                (displayRank * squareSizePx).roundToInt()
                            )
                        }
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
    onSquareClick: (String) -> Unit
) {
    // Premium wood/dark theme colors
    val lightSquareColor = Color(0xFFF0EBE0) // TextPrimary style light
    val darkSquareColor = Color(0xFFC8A45A).copy(alpha = 0.2f) // PrimaryAccent muted
    val selectedColor = Color(0xFFC8A45A).copy(alpha = 0.5f)
    val lastMoveColor = Color(0xFF4A9B6F).copy(alpha = 0.3f) // SecondaryAccent muted
    val legalMoveIndicatorColor = Color(0xFF4A9B6F).copy(alpha = 0.4f)

    Column {
        for (rankIdx in 0..7) {
            Row {
                for (fileIdx in 0..7) {
                    val visualRank = if (isFlipped) rankIdx else 7 - rankIdx
                    val visualFile = if (isFlipped) 7 - fileIdx else fileIdx
                    val squareName = "${'a' + visualFile}${visualRank + 1}"

                    val isLight = (visualFile + visualRank) % 2 != 0
                    val bgColor = when {
                        squareName == selectedSquare -> selectedColor
                        squareName == lastMove?.fromSquare || squareName == lastMove?.toSquare -> lastMoveColor
                        isLight -> lightSquareColor
                        else -> Color(0xFF1A1A1A) // SurfaceDark
                    }

                    Box(
                        modifier = Modifier
                            .size(squareSize)
                            .background(bgColor)
                            .clickable { onSquareClick(squareName) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (legalMoves.contains(squareName)) {
                            Canvas(modifier = Modifier.size(squareSize / 3)) {
                                drawCircle(color = legalMoveIndicatorColor)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Piece(
    piece: ChessPiece,
    squareSize: Dp,
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

    Box(
        modifier = modifier.size(squareSize),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = "${piece.color} ${piece.type}",
            modifier = Modifier.fillMaxSize(0.85f)
        )
    }
}

@Composable
private fun BoardCoordinates(isFlipped: Boolean, squareSize: Dp) {
    val textColor = Color.Gray
    val fontSize = 10.sp

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
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}
