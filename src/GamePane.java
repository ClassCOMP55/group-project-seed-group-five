import java.awt.Color;
import java.awt.event.MouseEvent;
import acm.graphics.*;

public class GamePane extends GraphicsPane {
	private static final int GRID_SIZE = 8;
	private static final int TILE_SIZE = Tile.SIZE;
	private static final int BOARD_X = (800 - GRID_SIZE * TILE_SIZE) / 2;
	private static final int BOARD_Y = (600 - GRID_SIZE * TILE_SIZE) / 2;

	private Tile[][] tiles = new Tile[GRID_SIZE][GRID_SIZE];

	public GamePane(MainApplication mainScreen) {
		this.mainScreen = mainScreen;
	}

	public void showContent() {buildGrid();}

	public void hideContent() {
		for (GObject obj : contents) {mainScreen.remove(obj);}
		contents.clear();
	}

	private void buildGrid() {
		for (int row = 0; row < GRID_SIZE; row++) {
			for (int col = 0; col < GRID_SIZE; col++) {
				double x = BOARD_X + col * TILE_SIZE;
				double y = BOARD_Y + row * TILE_SIZE;
				tiles[row][col] = new Tile(row, col, x, y);

				GRect square = new GRect(x, y, TILE_SIZE, TILE_SIZE);
				boolean isLight = (row + col) % 2 == 0;
				square.setFilled(true);
				square.setFillColor(isLight ? Color.WHITE : new Color(118, 150, 86));
				square.setColor(Color.BLACK);

				contents.add(square);
				mainScreen.add(square);
			}
		}
	}

	private Tile getTileAt(int mouseX, int mouseY) {
		int col = (mouseX - BOARD_X) / TILE_SIZE;
		int row = (mouseY - BOARD_Y) / TILE_SIZE;
		if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {return tiles[row][col];}
		return null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Tile clicked = getTileAt(e.getX(), e.getY());
		if (clicked != null) {
			System.out.println("Clicked tile: row=" + clicked.getRow() + ", col=" + clicked.getCol());
			//TODO: chess piece here
		}
	}
}