import java.awt.Color;
import acm.graphics.*;

public class InteractionFeedback {
	private GRect mergeHighlight;
    private GOval glowRing;
    private GLabel mergeLabel;
    private Tile currentTile;
    
    public InteractionFeedback() {
    	mergeHighlight = null;
    	glowRing = null;
    	mergeLabel = null;
    	currentTile = null;
    	
    }

    public void showMergeHighlight(MainApplication screen, Tile tile) {
    	if (tile == null) return;
    	
    	//Doesn't redraw the highlight in every frame
    	if (currentTile == tile && mergeHighlight != null) return;
    	
        clear(screen);

        double x = tile.getPixelX();
        double y = tile.getPixelY();

        mergeHighlight = new GRect(x, y, Tile.SIZE, Tile.SIZE);
        mergeHighlight.setColor(Color.RED);
        mergeHighlight.setLineWidth(4);

        glowRing = new GOval(x - 4, y - 4, Tile.SIZE + 8, Tile.SIZE + 8);
        glowRing.setColor(Color.YELLOW);
        glowRing.setLineWidth(2);

        mergeLabel = new GLabel("MERGE!", x + 4, y - 6);
        mergeLabel.setColor(Color.YELLOW);
        mergeLabel.setFont("SansSerif-BOLD-14");

        screen.add(glowRing);
        screen.add(mergeHighlight);
        screen.add(mergeLabel);
        
        currentTile = tile;
    }
    
    public void clear(MainApplication screen) {
    	if (mergeHighlight != null) {
    		screen.remove(mergeHighlight);
    		mergeHighlight = null;
    	}
    	
    	if (glowRing != null) {
    		screen.remove(glowRing);
    		glowRing = null;
    	}
    	
    	if (mergeLabel != null) {
    		screen.remove(mergeLabel);
    		mergeLabel = null;
    	}
    	
    	currentTile = null;
    }
 


    // Remove highlight
    public void clearCue(Tile tile) {
        System.out.println("Cue removed");
    }


    
    public void snapObjectToTile(GObject obj, Tile tile) {
    	if (obj == null || tile == null) return;
    	
        double centeredX = tile.getPixelX() + (Tile.SIZE - obj.getWidth()) / 2;
        double centeredY = tile.getPixelY() + (Tile.SIZE - obj.getHeight()) / 2;
        obj.setLocation(centeredX, centeredY);
    }

    // Check merge hover logic
    public boolean shouldShowCue(ChessPiece dragged, ChessPiece target) {
        return dragged != null &&
        		target != null &&
        		dragged.canMergeWith(target);
    }
    
    public boolean isShowing(Tile tile) {
    	return currentTile == tile && mergeHighlight != null;
    }
}