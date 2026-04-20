import acm.graphics.GImage;

// OriginalOgre — balanced, medium speed
class OriginalOgre extends UnitBase {
    public OriginalOgre() { super("Ogre", 40, 10, 1); speed = 2.0; goldValue = 5; }

    @Override
    protected void createVisual(double px, double py, MainApplication screen) {
        sprite = new GImage("Media/original ogre.png");
        sprite.scale(0.15, 0.15);
        sprite.setLocation(px - sprite.getWidth() / 2.0, py - sprite.getHeight() / 2.0);
        screen.add(sprite);
    }
}

// RedOgre — fast, low HP
class RedOgre extends UnitBase {
    public RedOgre() { super("Red Ogre", 20, 8, 1); speed = 3.5; goldValue = 3; }

    @Override
    protected void createVisual(double px, double py, MainApplication screen) {
        sprite = new GImage("Media/red ogre.png");
        sprite.scale(0.15, 0.15);
        sprite.setLocation(px - sprite.getWidth() / 2.0, py - sprite.getHeight() / 2.0);
        screen.add(sprite);
    }
}

// LightBlueOgre — medium speed, medium HP
class LightBlueOgre extends UnitBase {
    public LightBlueOgre() { super("Blue Ogre", 35, 8, 1); speed = 2.5; goldValue = 4; }

    @Override
    protected void createVisual(double px, double py, MainApplication screen) {
        sprite = new GImage("Media/light blue ogre.png");
        sprite.scale(0.15, 0.15);
        sprite.setLocation(px - sprite.getWidth() / 2.0, py - sprite.getHeight() / 2.0);
        screen.add(sprite);
    }
}

// DarkBlueOgre — slow tank, very high HP
class DarkBlueOgre extends UnitBase {
    public DarkBlueOgre() { super("Dark Ogre", 100, 15, 1); speed = 1.0; goldValue = 20; }

    @Override
    protected void createVisual(double px, double py, MainApplication screen) {
        sprite = new GImage("Media/dark blue ogre.png");
        sprite.scale(0.15, 0.15);
        sprite.setLocation(px - sprite.getWidth() / 2.0, py - sprite.getHeight() / 2.0);
        screen.add(sprite);
    }
}

// PinkOgre — fragile, low damage
class PinkOgre extends UnitBase {
    public PinkOgre() { super("Pink Ogre", 18, 5, 1); speed = 2.5; goldValue = 2; }

    @Override
    protected void createVisual(double px, double py, MainApplication screen) {
        sprite = new GImage("Media/pink ogre.png");
        sprite.scale(0.15, 0.15);
        sprite.setLocation(px - sprite.getWidth() / 2.0, py - sprite.getHeight() / 2.0);
        screen.add(sprite);
    }
}

// PurpleOgre — heavy hitter, slow
class PurpleOgre extends UnitBase {
    public PurpleOgre() { super("Purple Ogre", 75, 20, 1); speed = 1.5; goldValue = 15; }

    @Override
    protected void createVisual(double px, double py, MainApplication screen) {
        sprite = new GImage("Media/purple ogre.png");
        sprite.scale(0.15, 0.15);
        sprite.setLocation(px - sprite.getWidth() / 2.0, py - sprite.getHeight() / 2.0);
        screen.add(sprite);
    }
}

// YellowOgre — very fast, light HP
class YellowOgre extends UnitBase {
    public YellowOgre() { super("Yellow Ogre", 23, 8, 1); speed = 4.0; goldValue = 4; }

    @Override
    protected void createVisual(double px, double py, MainApplication screen) {
        sprite = new GImage("Media/yellow ogre.png");
        sprite.scale(0.15, 0.15);
        sprite.setLocation(px - sprite.getWidth() / 2.0, py - sprite.getHeight() / 2.0);
        screen.add(sprite);
    }
}
