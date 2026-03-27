import java.awt.event.MouseEvent;
import acm.graphics.GObject;

public class DragDropController extends GraphicsPane {
    private GObject selectedUnit;
    private GamePane pane;

    public DragDropController(GamePane pane) {
        this.pane = pane;
    }

    public void handlePressed(MouseEvent e) {
        // Mouse-pick logic goes here
    }

    public void handleDragged(MouseEvent e) {
        if (selectedUnit != null) {
            selectedUnit.setLocation(e.getX(), e.getY());
        }
    }
}