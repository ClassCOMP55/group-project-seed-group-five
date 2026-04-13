import java.awt.Color;
import java.util.List;
import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GRect;

public class UnitBase extends GraphicsPane {
    private String name;
    private int health;
    private int maxHealth;
    private int damage;
    private int starLevel;

    protected GLabel label;
    protected GImage sprite;
    private GRect hpBarBg;
    private GRect hpBarFill;
    private static final int HP_BAR_W = 40;
    private static final int HP_BAR_H = 5;

    protected double speed     = 2.0;
    protected int    goldValue = 5;

    private int pathIndex;
    private int targetIndex;
    private double pixelX;
    private double pixelY;

    private Color color;

    public UnitBase(String name, int health, int damage, int starLevel) {
        this(name, health, damage, starLevel, new Color(220, 60, 60));
    }

    public UnitBase(String name, int health, int damage, int starLevel, Color color) {
        this.name      = name;
        this.health    = health;
        this.maxHealth = health;
        this.damage    = damage;
        this.starLevel = starLevel;
        this.color     = color;
    }

    // ---- Visual / path ----

    /**
     * Override in subclasses to render a sprite instead of the default label.
     * Set either `label` or `sprite` and add it to screen.
     */
    protected void createVisual(double px, double py, MainApplication screen) {
        label = new GLabel("\u2620 " + name, px - 20, py + 5);
        label.setFont("DialogInput-BOLD-13");
        label.setColor(color);
        screen.add(label);
    }

    public void spawnAt(Tile t, MainApplication screen) {
        pathIndex   = 0;
        targetIndex = 1;
        pixelX = t.getPixelX() + Tile.SIZE / 2.0;
        pixelY = t.getPixelY() + Tile.SIZE / 2.0;

        createVisual(pixelX, pixelY, screen);

        double barY = sprite != null ? pixelY - 55 : pixelY - 16;
        hpBarBg = new GRect(pixelX - HP_BAR_W / 2.0, barY, HP_BAR_W, HP_BAR_H);
        hpBarBg.setFilled(true);
        hpBarBg.setFillColor(new Color(60, 60, 60));
        hpBarBg.setColor(new Color(60, 60, 60));
        screen.add(hpBarBg);

        hpBarFill = new GRect(pixelX - HP_BAR_W / 2.0, barY, HP_BAR_W, HP_BAR_H);
        hpBarFill.setFilled(true);
        hpBarFill.setFillColor(new Color(50, 200, 80));
        hpBarFill.setColor(new Color(50, 200, 80));
        screen.add(hpBarFill);
    }

    /** Smoothly advances toward the next path tile. Returns true when end is reached. */
    public boolean step(List<Tile> path, double spd) {
        if (targetIndex >= path.size()) return true;

        Tile target = path.get(targetIndex);
        double tx = target.getPixelX() + Tile.SIZE / 2.0;
        double ty = target.getPixelY() + Tile.SIZE / 2.0;
        double dx = tx - pixelX;
        double dy = ty - pixelY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist <= spd) {
            pixelX = tx;
            pixelY = ty;
            pathIndex = targetIndex;
            targetIndex++;
        } else {
            pixelX += spd * dx / dist;
            pixelY += spd * dy / dist;
        }

        if (label  != null) label.setLocation(pixelX - 20, pixelY + 5);
        if (sprite != null) sprite.setLocation(pixelX - sprite.getWidth()  / 2.0,
                                               pixelY - sprite.getHeight() / 2.0);
        double barY = sprite != null ? pixelY - 55 : pixelY - 16;
        if (hpBarBg   != null) hpBarBg.setLocation(pixelX - HP_BAR_W / 2.0, barY);
        if (hpBarFill != null) hpBarFill.setLocation(pixelX - HP_BAR_W / 2.0, barY);
        return targetIndex >= path.size();
    }

    public void removeFrom(MainApplication screen) {
        if (label     != null) { screen.remove(label);     label     = null; }
        if (sprite    != null) { screen.remove(sprite);    sprite    = null; }
        if (hpBarBg   != null) { screen.remove(hpBarBg);   hpBarBg   = null; }
        if (hpBarFill != null) { screen.remove(hpBarFill); hpBarFill = null; }
    }

    // ---- Combat ----

    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
        if (hpBarFill != null) {
            double pct = (double) health / maxHealth;
            hpBarFill.setSize(HP_BAR_W * pct, HP_BAR_H);
            Color fill = pct > 0.4 ? new Color(50, 200, 80) : new Color(200, 50, 50);
            hpBarFill.setFillColor(fill);
            hpBarFill.setColor(fill);
        }
    }

    public boolean isAlive() { return health > 0; }

    public void attackDamage(UnitBase target) {
        if (target != null && isAlive()) target.takeDamage(damage);
    }

    public void upgrade() {
        starLevel++;
        maxHealth += 20;
        health = maxHealth;
        damage += 10;
    }

    public void heal(int amount) {
        health = Math.min(health + amount, maxHealth);
    }

    // ---- Getters ----

    public String getName()        { return name; }
    public int    getHealth()      { return health; }
    public int    getMaxHealth()   { return maxHealth; }
    public int    getDamage()      { return damage; }
    public int    getStarLevel()   { return starLevel; }
    public int    getPathIndex()   { return pathIndex; }
    public int    getTargetIndex() { return targetIndex; }
    public double getPixelX()      { return pixelX; }
    public double getPixelY()      { return pixelY; }
    public double getSpeed()       { return speed; }
    public int    getGoldValue()   { return goldValue; }
    public GLabel getLabel()       { return label; }
}
