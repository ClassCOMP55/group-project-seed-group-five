import java.awt.Color;
import acm.graphics.*;

public abstract class ChessPiece {

    //Piece identity
    private String name;
    private int    cost;       // gold cost to purchase from shop
    private int    damage;
    private int    tier;       // 1 = base, 2 = upgraded, 3 = max
    private String symbol;     // Unicode chess glyph for placeholder rendering

    private Tile   tile;       // the tile this piece currently occupies (null if in shop)

    //Visual
    private GLabel label;      // placeholder renderer; swap for GImage once sprites exist

    public ChessPiece(String name, String symbol, int cost, int damage) {
        this.name   = name;
        this.symbol = symbol;
        this.cost   = cost;
        this.damage = damage;
        this.tier   = 1;
    }

    public abstract boolean canAttack(int fromRow, int fromCol, int toRow, int toCol);

    //Returns the display color for this piece type.
    public abstract Color getPieceColor();

    public GLabel createLabel(double x, double y) {
        String display = symbol + " " + name + " T" + tier;
        label = new GLabel(display, x, y);
        label.setFont("DialogInput-BOLD-14");
        label.setColor(getPieceColor());
        return label;
    }

    //Moves the stored label to a new pixel position.
    public void moveLabelTo(double x, double y) {
        if (label != null) {
            label.setLocation(x, y);
        }
    }

    public GLabel getLabel() { return label; }

    public boolean canMergeWith(ChessPiece other) {
        return other != null
            && other.getClass() == this.getClass()
            && other.getTier() == this.tier
            && this.tier < getMaxTier();
    }

    public void promote() {
        if (tier < getMaxTier()) {
            tier++;
            damage = (int)(damage * 1.5); // 50% damage increase per tier
            cost   = cost * 2;            // reflects upgraded value
        }
    }

    public int getMaxTier() { return 3; }


    public void placedOnTile(Tile t) {
        if (tile != null) tile.setOccupied(false);
        tile = t;
        if (tile != null) tile.setOccupied(true);
    }

    public void removeFromTile() {
        if (tile != null) tile.setOccupied(false);
        tile = null;
    }

    public String getName()   { return name; }
    public int    getCost()   { return cost; }
    public int    getDamage() { return damage; }
    public int    getTier()   { return tier; }
    public String getSymbol() { return symbol; }
    public Tile   getTile()   { return tile; }

    public String getUpgradeDescription() {
        if (tier >= getMaxTier()) return name + " (max tier)";
        int nextDmg = (int)(damage * 1.5);
        return name + " T" + (tier + 1)
            + " — damage " + damage + " → " + nextDmg
            + "  (cost: " + cost + "g)";
    }

    @Override
    public String toString() {
        return name + " [T" + tier + ", dmg=" + damage + ", cost=" + cost + "]";
    }
}