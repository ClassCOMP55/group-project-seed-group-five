import java.awt.Color;
import acm.graphics.*;

public abstract class ChessPiece extends GraphicsPane{

    //Piece identity
    private String name;
    private int    cost;       // gold cost to purchase from shop
    private int    damage;
    private int    tier;       // 1 = base, 2 = upgraded, 3 = max
    private String symbol;     // Unicode chess glyph for placeholder rendering

    private Tile   tile;       // the tile this piece currently occupies (null if in shop)

    // Upgrade
    private boolean upgraded              = false;
    private boolean halfStar              = false;
    private int     attackCooldownOverride = -1;  // -1 = use GamePane default
    protected int   range                 = 3;    // max tile distance for ranged pieces
    private Color   colorOverride         = null;
    private boolean pawnTransform         = false;
    private String  nextTransformSymbol   = null;

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
        label.setColor(getEffectiveColor());
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
        // Incoming piece (this) must be tier 1; occupant (other) must not be at max tier
        return other != null
            && other.getClass() == this.getClass()
            && this.tier == 1
            && other.getTier() < other.getMaxTier();
    }

    public void promote() {
        if (tier == 1) {
            tier     = 2;
            halfStar = false;
            damage   = (int)(damage * 1.5);
            cost     = cost * 2;
        } else if (tier == 2 && !halfStar) {
            halfStar = true;
            damage   = (int)(damage * 1.2);
        } else if (tier == 2 && halfStar) {
            tier     = 3;
            halfStar = false;
            damage   = (int)(damage * 1.5);
            cost     = cost * 2;
        }
    }

    public int getMaxTier() { return 3; }

    // ---- Upgrade paths ----
    public boolean isUpgraded()                   { return upgraded; }
    public boolean isHalfStar()                   { return halfStar; }
    public int  getAttackCooldownOverride()        { return attackCooldownOverride; }
    protected void setAttackCooldownOverride(int v){ attackCooldownOverride = v; }
    protected void markUpgraded()                  { upgraded = true; }
    protected void forceTier(int t)                { this.tier = t; }
    public void setColorOverride(Color c)          { colorOverride = c; }
    public Color getEffectiveColor()               { return colorOverride != null ? colorOverride : getPieceColor(); }
    public void markPawnTransform()                { pawnTransform = true; }
    public boolean isPawnTransform()               { return pawnTransform; }
    public void setNextTransformSymbol(String s)   { nextTransformSymbol = s; }
    public String getNextTransformSymbol()         { return nextTransformSymbol; }

    public String get3StarDescription() { return "Reaches maximum power!"; }

    public String[] getUpgradePathNames() { return new String[]{"Swift", "Ranged"}; }
    public String[] getUpgradePathDescs() { return new String[]{"Faster attack speed", "Range: 3 → 5 tiles"}; }
    public void applyUpgradePath(int path) {
        markUpgraded();
        if (path == 0) setAttackCooldownOverride(45); // ~2x faster than default 90
        else           range = 5;
    }

    public int getRange() { return range; }


    public void placedOnTile(Tile t) {
        if (tile != null) tile.setOccupant(null);
        tile = t;
        if (tile != null) tile.setOccupant(this);
    }

    public void removeFromTile() {
        if (tile != null) tile.setOccupant(null);
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