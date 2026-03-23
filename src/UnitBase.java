
public class UnitBase {
    private String name;
    private int health;
    private int maxHealth;
    private int damage;
    private int starLevel;

	
	public UnitBase(String name, int health, int damage, int starLevel) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.damage = damage;
        this.starLevel = starLevel;

	}

}
