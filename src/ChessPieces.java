import java.awt.Color;


//  Pawn  — attacks one square diagonally forward (cheap, short range)

class Pawn extends ChessPiece {
    private int attackMode = 0; // 0=diagonal(default), 1=sniper, 2=flanker

    public Pawn() { super("Pawn", "\u265F", 10, 8); }

    @Override
    public boolean canAttack(int fromRow, int fromCol, int toRow, int toCol) {
        int dr = fromRow - toRow;
        int dc = Math.abs(toCol - fromCol);
        if (attackMode == 1) return dc == 0 && dr >= 1 && dr <= 3; // Sniper: straight up, 3-tile range
        return dc == 1 && dr == 1;                                  // Default/Flanker: both diagonals
    }

    @Override public String[] getUpgradePathNames() { return new String[]{"Sniper", "Flanker"}; }
    @Override public String[] getUpgradePathDescs() {
        return new String[]{
            "Straight up · 3-tile range",
            "Both diagonals · faster attacks"
        };
    }
    @Override public void applyUpgradePath(int path) {
        markUpgraded();
        if (path == 0) { attackMode = 1; setAttackCooldownOverride(120); } // Sniper: slower but longer
        else           { attackMode = 2; setAttackCooldownOverride(55);  } // Flanker: faster
    }

    @Override public Color getPieceColor() { return new Color(0x3B8BD4); } // blue
}


//  Rook  — attacks entire row or column (long range, cardinal only)

class Rook extends ChessPiece {
    public Rook() { super("Rook", "\u265C", 40, 25); }

    @Override
    public boolean canAttack(int fromRow, int fromCol, int toRow, int toCol) {
        int dist = Math.max(Math.abs(toRow - fromRow), Math.abs(toCol - fromCol));
        return (fromRow == toRow || fromCol == toCol) && (getTier() >= 3 || dist <= range);
    }

    @Override public Color getPieceColor() { return new Color(0x639922); } // green
}


//  Bishop  — attacks diagonals, unlimited range

class Bishop extends ChessPiece {
    public Bishop() { super("Bishop", "\u265D", 30, 20); }

    @Override
    public boolean canAttack(int fromRow, int fromCol, int toRow, int toCol) {
        int dr = Math.abs(toRow - fromRow), dc = Math.abs(toCol - fromCol);
        return dr == dc && (getTier() >= 3 || dr <= range);
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

    @Override
    public void promote() {
        super.promote();
        if (getTier() == 3) setAttackCooldownOverride(30); // very fast at tier 3
    }

    @Override public Color getPieceColor() { return new Color(0x993556); } // pink
}

//  Queen  — attacks any direction, unlimited range
class Queen extends ChessPiece {
    public Queen() { super("Queen", "\u265B", 80, 45); }

    @Override
    public boolean canAttack(int fromRow, int fromCol, int toRow, int toCol) {
        int dr = Math.abs(toRow - fromRow), dc = Math.abs(toCol - fromCol);
        boolean straight = fromRow == toRow || fromCol == toCol;
        boolean diagonal = dr == dc;
        return (straight || diagonal) && (getTier() >= 3 || Math.max(dr, dc) <= range);
    }

    @Override
    public void promote() {
        super.promote();
        if (getTier() == 3) {
            setAttackCooldownOverride(25); // very fast at tier 3
        }
    }

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