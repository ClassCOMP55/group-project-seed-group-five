import java.awt.Color;
import java.util.List;
import acm.graphics.GLabel;
import acm.graphics.GRect;

public class UnitBase extends GraphicsPane {
    private String name;
    private int health;
    private int maxHealth;
    private int damage;
    private int starLevel;

    private GLabel label;
    private GRect hpBarBg;
    private GRect hpBarFill;
    private static final int HP_BAR_W = 40;
    private static final int HP_BAR_H = 5;

    private int pathIndex;       // tile we are currently on
    private int targetIndex;     // tile we are moving toward
    private double pixelX;
    private double pixelY;

    private Color color;

    public UnitBase(String name, int health, int damage, int starLevel) {
        this(name, health, damage, starLevel, new Color(220, 60, 60)); // default red
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

    public void spawnAt(Tile t, MainApplication screen) {
        pathIndex   = 0;
        targetIndex = 1;
        pixelX = t.getPixelX() + Tile.SIZE / 2.0;
        pixelY = t.getPixelY() + Tile.SIZE / 2.0;
        label = new GLabel("\u2620 " + name, pixelX - 20, pixelY + 5);
        label.setFont("DialogInput-BOLD-13");
        label.setColor(color);
        screen.add(label);

        hpBarBg = new GRect(pixelX - HP_BAR_W / 2.0, pixelY - 16, HP_BAR_W, HP_BAR_H);
        hpBarBg.setFilled(true);
        hpBarBg.setFillColor(new Color(60, 60, 60));
        hpBarBg.setColor(new Color(60, 60, 60));
        screen.add(hpBarBg);

        hpBarFill = new GRect(pixelX - HP_BAR_W / 2.0, pixelY - 16, HP_BAR_W, HP_BAR_H);
        hpBarFill.setFilled(true);
        hpBarFill.setFillColor(new Color(50, 200, 80));
        hpBarFill.setColor(new Color(50, 200, 80));
        screen.add(hpBarFill);
    }

    /**
     * Smoothly advances the enemy toward the next path tile by `speed` pixels.
     * Returns true when the enemy has arrived at the last tile.
     */
    public boolean step(List<Tile> path, double speed) {
        if (targetIndex >= path.size()) return true;

        Tile target = path.get(targetIndex);
        double tx = target.getPixelX() + Tile.SIZE / 2.0;
        double ty = target.getPixelY() + Tile.SIZE / 2.0;
        double dx = tx - pixelX;
        double dy = ty - pixelY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist <= speed) {
            pixelX = tx;
            pixelY = ty;
            pathIndex = targetIndex;
            targetIndex++;
        } else {
            pixelX += speed * dx / dist;
            pixelY += speed * dy / dist;
        }

        if (label != null) label.setLocation(pixelX - 20, pixelY + 5);
        if (hpBarBg   != null) hpBarBg.setLocation(pixelX - HP_BAR_W / 2.0, pixelY - 16);
        if (hpBarFill != null) hpBarFill.setLocation(pixelX - HP_BAR_W / 2.0, pixelY - 16);
        return targetIndex >= path.size();
    }

    public void removeFrom(MainApplication screen) {
        if (label     != null) { screen.remove(label);     label     = null; }
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

    public String getName()       { return name; }
    public int    getHealth()     { return health; }
    public int    getMaxHealth()  { return maxHealth; }
    public int    getDamage()     { return damage; }
    public int    getStarLevel()  { return starLevel; }
    public int    getPathIndex()  { return pathIndex; }
    public int    getTargetIndex(){ return targetIndex; }
    public double getPixelX()     { return pixelX; }
    public double getPixelY()     { return pixelY; }
    public GLabel getLabel()      { return label; }
}
