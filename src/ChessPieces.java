import java.awt.Color;


//  Pawn  — attacks one square diagonally forward (cheap, short range)

class Pawn extends ChessPiece {
    public Pawn() { super("Pawn", "\u265F", 10, 8); }

    @Override
    public boolean canAttack(int fromRow, int fromCol, int toRow, int toCol) {
        int dr = toRow - fromRow;
        int dc = Math.abs(toCol - fromCol);
        return dr == -1 && dc == 1; // attacks diagonally forward (enemy rows decrease)
    }

    @Override public Color getPieceColor() { return new Color(0x3B8BD4); } // blue
}


//  Rook  — attacks entire row or column (long range, cardinal only)

class Rook extends ChessPiece {
    public Rook() { super("Rook", "\u265C", 40, 25); }

    @Override
    public boolean canAttack(int fromRow, int fromCol, int toRow, int toCol) {
        return fromRow == toRow || fromCol == toCol;
    }

    @Override public Color getPieceColor() { return new Color(0x639922); } // green
}


//  Bishop  — attacks diagonals, unlimited range

class Bishop extends ChessPiece {
    public Bishop() { super("Bishop", "\u265D", 30, 20); }

    @Override
    public boolean canAttack(int fromRow, int fromCol, int toRow, int toCol) {
        return Math.abs(toRow - fromRow) == Math.abs(toCol - fromCol);
    }

    @Override public Color getPieceColor() { return new Color(0xBA7517); } // amber
}


//  Knight  — classic L-shape jump
class Knight extends ChessPiece {
    public Knight() { super("Knight", "\u265E", 35, 22); }

    @Override
    public boolean canAttack(int fromRow, int fromCol, int toRow, int toCol) {
        int dr = Math.abs(toRow - fromRow);
        int dc = Math.abs(toCol - fromCol);
        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    }

    @Override public Color getPieceColor() { return new Color(0x993556); } // pink
}

//  Queen  — attacks any direction, unlimited range
class Queen extends ChessPiece {
    public Queen() { super("Queen", "\u265B", 80, 45); }

    @Override
    public boolean canAttack(int fromRow, int fromCol, int toRow, int toCol) {
        boolean straight  = fromRow == toRow || fromCol == toCol;
        boolean diagonal  = Math.abs(toRow - fromRow) == Math.abs(toCol - fromCol);
        return straight || diagonal;
    }

    @Override public int    getMaxTier()     { return 2; }              // already powerful
    @Override public Color  getPieceColor()  { return new Color(0x7F77DD); } // purple
}

//  King  — the objective. Not purchasable. Placed randomly on the bottom row.
class King extends ChessPiece {
    public King() { super("King", "\u265A", 0, 0); }

    @Override
    public boolean canAttack(int fromRow, int fromCol, int toRow, int toCol) {
        return false; // King does not attack
    }

    @Override public int   getMaxTier()    { return 1; }
    @Override public Color getPieceColor() { return new Color(0xFFD700); } // gold
}