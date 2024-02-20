package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class ChessMatch {  // Regras do jogo
	private final Board board;
	private final List<Piece> piecesontheBoard = new ArrayList<>();
	private final List<Piece> capturedPieces = new ArrayList<>();
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;


	public ChessMatch() {
		board = new Board(8, 8); // O tabuleiro vai ter 8x8.
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}

	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean getCheck() {
		return check;
	}

	public boolean getCheckMate() {
		return getCheck();
	}

	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}

	public ChessPiece getPromoted() {
		return promoted;
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}

	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);

		return board.piece(position).possibleMoves();
	}

	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturePiece = makeMove(source, target);

		if (testCheck(currentPlayer)) {
			undoMove(source, target, capturePiece);
			throw new ChessException("You can't put yourself in check");
		}
		ChessPiece movedPiece = (ChessPiece) board.piece(target);

		// SpecialMove - PROMOTION
		promoted = null;
		if (movedPiece instanceof Pawn) {
			if ((movedPiece.getColor() == Color.WHITE && target.getRow() == 0 || movedPiece.getColor() == Color.BLACK && target.getRow() == 7)) {
				promoted = (ChessPiece) board.piece(target);
				promoted = replacePromotedPiece("Q");
			}

		}

		check = testCheck(opponent(currentPlayer));

		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		} else {
			nextTurn();
		}

		// #SpecialMove - En Passant
		if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		} else {
			enPassantVulnerable = null;
		}

		return (ChessPiece) capturePiece;
	}

	public ChessPiece replacePromotedPiece(String typePiece) {
		if (promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}

		if (!typePiece.equals("B") && !typePiece.equals("N") && !typePiece.equals("R") && !typePiece.equals("Q")) {
			throw new InvalidParameterException("Invalid Type for Promotion");
		}

		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesontheBoard.remove(p);

		ChessPiece newPiece = newPiece(typePiece, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesontheBoard.add(newPiece);

		return newPiece;
	}

	private ChessPiece newPiece(String typePiece, Color color) {
		if (typePiece.equals("B")) return new Bishop(board, color);
		if (typePiece.equals("N")) return new Knight(board, color);
		if (typePiece.equals("Q")) return new Queen(board, color);
		return new Rook(board, color);
	}

	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece) board.removePiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);

		if (capturedPiece != null) {
			piecesontheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		// #SpecialMove - Castling King Side rook
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceTower = new Position(source.getRow(), source.getColumn() + 3);
			Position targetTower = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(sourceTower);
			board.placePiece(rook, targetTower);
			rook.increaseMoveCount();
		}

		// #SpecialMove - Queen King Side rook
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceTower = new Position(source.getRow(), source.getColumn() - 4);
			Position targetTower = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(sourceTower);
			board.placePiece(rook, targetTower);
			rook.increaseMoveCount();
		}

		return capturedPiece;
	}

	public void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece) board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);

		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesontheBoard.add(capturedPiece);
		}

		// #SpecialMove - Castling King Side rook
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceTower = new Position(source.getRow(), source.getColumn() + 3);
			Position targetTower = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetTower);
			board.placePiece(rook, sourceTower);
			rook.decreaseMoveCount();
		}

		// #SpecialMove - Queen King Side rook
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceTower = new Position(source.getRow(), source.getColumn() - 4);
			Position targetTower = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetTower);
			board.placePiece(rook, sourceTower);
			rook.decreaseMoveCount();
		}
	}

	private void validateSourcePosition(Position position) {
		if (!board.thereisaPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}
		if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece is not yours.");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There is no possible moves for the chosen piece");
		}
	}

	private void validateTargetPosition(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece can't move to target position.");
		}
	}

	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private ChessPiece king(Color color) {
		List<Piece> list = piecesontheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color).toList();
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("There is no " + color + " king on the Board");
	}

	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesontheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == opponent(color)).toList();

		for (Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesontheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color).toList();
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i = 0; i < board.getRows(); i++) {
				for (int j = 0; j < board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = ((ChessPiece) p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);

						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);

						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void placenewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesontheBoard.add(piece);
	}

	private void initialSetup() {
		placenewPiece('a', 1, new Rook(board, Color.WHITE));
		placenewPiece('b', 1, new Knight(board, Color.WHITE));
		placenewPiece('c', 1, new Bishop(board, Color.WHITE));
		placenewPiece('d', 1, new Queen(board, Color.WHITE));
		placenewPiece('e', 1, new King(board, Color.WHITE, this));
		placenewPiece('f', 1, new Bishop(board, Color.WHITE));
		placenewPiece('g', 1, new Knight(board, Color.WHITE));
		placenewPiece('h', 1, new Rook(board, Color.WHITE));
		placenewPiece('a', 2, new Pawn(board, Color.WHITE, this));
		placenewPiece('b', 2, new Pawn(board, Color.WHITE, this));
		placenewPiece('c', 2, new Pawn(board, Color.WHITE, this));
		placenewPiece('d', 2, new Pawn(board, Color.WHITE, this));
		placenewPiece('e', 2, new Pawn(board, Color.WHITE, this));
		placenewPiece('f', 2, new Pawn(board, Color.WHITE, this));
		placenewPiece('g', 2, new Pawn(board, Color.WHITE, this));
		placenewPiece('h', 2, new Pawn(board, Color.WHITE, this));

		placenewPiece('a', 8, new Rook(board, Color.BLACK));
		placenewPiece('b', 8, new Knight(board, Color.BLACK));
		placenewPiece('c', 8, new Bishop(board, Color.BLACK));
		placenewPiece('d', 8, new Queen(board, Color.BLACK));
		placenewPiece('e', 8, new King(board, Color.BLACK, this));
		placenewPiece('f', 8, new Bishop(board, Color.BLACK));
		placenewPiece('g', 8, new Knight(board, Color.BLACK));
		placenewPiece('h', 8, new Rook(board, Color.BLACK));
		placenewPiece('a', 7, new Pawn(board, Color.BLACK, this));
		placenewPiece('b', 7, new Pawn(board, Color.BLACK, this));
		placenewPiece('c', 7, new Pawn(board, Color.BLACK, this));
		placenewPiece('d', 7, new Pawn(board, Color.BLACK, this));
		placenewPiece('e', 7, new Pawn(board, Color.BLACK, this));
		placenewPiece('f', 7, new Pawn(board, Color.BLACK, this));
		placenewPiece('g', 7, new Pawn(board, Color.BLACK, this));
		placenewPiece('h', 7, new Pawn(board, Color.BLACK, this));
	}
}