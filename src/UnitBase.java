
public class UnitBase extends GraphicsPane{
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
	
	//Making the Getters
	
	String getName() {
		return name;
	}
	
	int getHealth() {
		return health;
	}
	
	int getDamage() {
		return damage;
	}
	
	int getStarLevel() {
		return starLevel;
	}
	
	// Setters if we need setters
	
	/*void setHealth(int health) {
		this.health = Math.max(0, health);
	}
	
	void setDamage(int damage) {
		this.damage = damage;
	}
	
	void setStarLevel(int starLevel) {
		this.starLevel = starLevel;
	} */
	
	//--------Core game logic--------
	
	//
	public void takeDamage(int amount) {
		health -= amount;
		if (health < 0) {
			health = 0;
		}
		
	}
	
	//Checks if the unit is alive 
	public boolean isAlive() {
		return health > 0;
	}
	
	public void attackDamage(UnitBase target ) {
		if (target != null &&  this.isAlive()) {
			target.takeDamage(this.damage);
		}
		
	}
	
	public void upgrade() {
		starLevel++;
		
		//Base numbers, we can change this later
		maxHealth += 20;
		
		health = maxHealth;
		
		damage += 10;
	}
	
	//Heal funct incase we want to add in a healing option in the off rounds
	public void heal(int amount) {
		health += amount;
		
		if (health > maxHealth) {
			health = maxHealth;
		}
		
	}
	
	// ----- Displays Maybe -------
	/*@Override
	public String toString() {
		return name + " | HP: " + health + "/" + maxHealth + 
				" | DMG: " + damage +
				" | STAR: " + starLevel;
	}*/
	
	

}
