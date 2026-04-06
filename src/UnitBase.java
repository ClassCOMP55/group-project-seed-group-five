import java.awt.Color;
import java.util.List;
import acm.graphics.GLabel;

public class UnitBase extends GraphicsPane {
    private String name;
    private int health;
    private int maxHealth;
    private int damage;
    private int starLevel;

    private GLabel label;
    private int pathIndex;       // tile we are currently on
    private int targetIndex;     // tile we are moving toward
    private double pixelX;
    private double pixelY;

    public UnitBase(String name, int health, int damage, int starLevel) {
        this.name      = name;
        this.health    = health;
        this.maxHealth = health;
        this.damage    = damage;
        this.starLevel = starLevel;
    }

    // ---- Visual / path ----

    public void spawnAt(Tile t, MainApplication screen) {
        pathIndex   = 0;
        targetIndex = 1;
        pixelX = t.getPixelX() + Tile.SIZE / 2.0;
        pixelY = t.getPixelY() + Tile.SIZE / 2.0;
        label = new GLabel("☠ " + name, pixelX - 20, pixelY + 5);
        label.setFont("DialogInput-BOLD-13");
        label.setColor(new Color(220, 60, 60));
        screen.add(label);
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
        return targetIndex >= path.size();
    }

    public void removeFrom(MainApplication screen) {
        if (label != null) { screen.remove(label); label = null; }
    }

    // ---- Combat ----

    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
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

    public String getName()      { return name; }
    public int    getHealth()    { return health; }
    public int    getMaxHealth() { return maxHealth; }
    public int    getDamage()    { return damage; }
    public int    getStarLevel() { return starLevel; }
    public int    getPathIndex() { return pathIndex; }
    public GLabel getLabel()     { return label; }
}
