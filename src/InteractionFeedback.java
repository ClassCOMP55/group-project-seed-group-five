public class InteractionFeedback {

    // Highlight tile if merge is valid
    public void showMergeCue(Tile tile) {
        // Example: change tile colo, but will change this to only either add a star or change color of piece or both
        System.out.println("Valid merge target highlighted");
    }

    // Remove highlight
    public void clearCue(Tile tile) {
        System.out.println("Cue removed");
    }

    // Snap unit into tile position
    public void snapUnitToTile(UnitBase unit, Tile tile) {
        System.out.println("Snapping " + unit.getName() +
                           " to tile (" + tile.getRow() +
                           ", " + tile.getCol() + ")");
    }

    // Check merge hover logic
    public boolean shouldShowCue(UnitBase dragged, UnitBase target) {
        return dragged.getName().equals(target.getName()) &&
               dragged.getStarLevel() == target.getStarLevel();
    }
}