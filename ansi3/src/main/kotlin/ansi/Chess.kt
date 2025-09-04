package ansi


enum class ChessFigure(val white: String, val black: String) {
    NONE(" ", " "),
    KING("♔", "♚"),
    QUEEN("♕", "♛"),
    ROOK("♖", "♜"),
    BISHOP("♗", "♝"),
    KNIGHT("♘", "♞"),
    PAWN("♙", "♟");
}

enum class ChessRank {
    RANK_1, RANK_2, RANK_3, RANK_4, RANK_5, RANK_6, RANK_7, RANK_8
}

enum class ChessFile {
    FILE_A, FILE_B, FILE_C, FILE_D, FILE_E, FILE_F, FILE_G, FILE_H
}

enum class ChessColor {
    WHITE, BLACK
}

data class ChessPoint(val rank: ChessRank, val file: ChessFile) {
    val field: ChessColor = if ((rank.ordinal + file.ordinal) % 2 == 0) ChessColor.WHITE else ChessColor.BLACK
    val figure: ChessFigure = when (rank) {
        ChessRank.RANK_1 -> when (file) {
            ChessFile.FILE_A, ChessFile.FILE_H -> ChessFigure.ROOK
            ChessFile.FILE_B, ChessFile.FILE_G -> ChessFigure.KNIGHT
            ChessFile.FILE_C, ChessFile.FILE_F -> ChessFigure.BISHOP
            ChessFile.FILE_D -> ChessFigure.QUEEN
            ChessFile.FILE_E -> ChessFigure.KING
        }

        ChessRank.RANK_2 -> ChessFigure.PAWN
        ChessRank.RANK_7 -> ChessFigure.PAWN
        ChessRank.RANK_8 -> when (file) {
            ChessFile.FILE_A, ChessFile.FILE_H -> ChessFigure.ROOK
            ChessFile.FILE_B, ChessFile.FILE_G -> ChessFigure.KNIGHT
            ChessFile.FILE_C, ChessFile.FILE_F -> ChessFigure.BISHOP
            ChessFile.FILE_D -> ChessFigure.QUEEN
            ChessFile.FILE_E -> ChessFigure.KING
        }

        else -> ChessFigure.NONE
    }

    fun toLine() = Line(
        listOf(
            Symbol(" ", Attributes(WHITE, if (field == ChessColor.WHITE) WHITE else BLACK)),
            Symbol(
                if (rank in ChessRank.RANK_1..ChessRank.RANK_2) figure.white else figure.black,
                Attributes(WHITE.light, if (field == ChessColor.WHITE) WHITE else BLACK)
            ),
            Symbol(" ", Attributes(WHITE, if (field == ChessColor.WHITE) WHITE else BLACK))
        )
    )
}

fun main() {
    ChessRank.entries.forEach { rank ->
        val line = ChessFile.entries.map { file -> ChessPoint(rank, file).toLine() }.reduce { acc, line -> acc + line }
        println(line)
    }
}