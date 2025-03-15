package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.AttackMove;
import com.chess.engine.board.Move.MajorMove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Pawn extends Piece {

    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATE = {8, 16, 7, 9};

    public Pawn(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset : CANDIDATE_MOVE_VECTOR_COORDINATE) {
            final int candidateDestinationCoordinate = this.piecePosition + (currentCandidateOffset * this.getPieceAlliance().getDirection());

            if(!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }

            switch(currentCandidateOffset) {
                case 8:
                    processNormalMove(board, legalMoves, candidateDestinationCoordinate);
                    break;
                case 16:
                    processTwoStepMove(board, legalMoves, candidateDestinationCoordinate);
                    break;
                case 7:
                    processLeftDiagonalAttack(board, legalMoves, candidateDestinationCoordinate);
                    break;
                case 9:
                    processRightDiagonalAttack(board, legalMoves, candidateDestinationCoordinate);
                    break;
            }
        }

        return Collections.unmodifiableList(legalMoves);
    }

    private void processNormalMove(Board board, List<Move> legalMoves, int candidateDestinationCoordinate) {
        if (!board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
            legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
        }
    }

    private void processTwoStepMove(Board board, List<Move> legalMoves, int candidateDestinationCoordinate) {
        if(this.isFirstMove() &&
                (BoardUtils.SECOND_ROW[this.piecePosition] && this.getPieceAlliance().isBlack()) ||
                (BoardUtils.SEVENTH_ROW[this.piecePosition] && this.getPieceAlliance().isWhite())) {
            final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
            if(!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                    !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
            }
        }
    }

    private void processLeftDiagonalAttack(Board board, List<Move> legalMoves, int candidateDestinationCoordinate) {
        if(!((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() ||
            (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())))) {
            if(board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                    legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                }
            }
        }
    }

    private void processRightDiagonalAttack(Board board, List<Move> legalMoves, int candidateDestinationCoordinate) {
        if(!((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack() ||
            (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite())))) {
            if(board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                    legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                }
            }
        }
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

}
