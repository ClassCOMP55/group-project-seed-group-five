import java.awt.Color;
import java.awt.event.MouseEvent;
import acm.graphics.*;

public class GamePane extends GraphicsPane {
	private static final int GRID_SIZE = 8;
	private static final int TILE_SIZE = Tile.SIZE;
	private static final int BOARD_X = 20;
	private int BOARD_Y;

	private Tile[][] tiles = new Tile[GRID_SIZE][GRID_SIZE];
	private ChessPiece heldPiece;
	private Tile heldFromTile;

	public GamePane(MainApplication mainScreen) {
		this.mainScreen = mainScreen;
	}

	public void showContent() {buildGrid();}

	public void hideContent() {
		for (GObject obj : contents) {mainScreen.remove(obj);}
		contents.clear();
	}

	private void buildGrid() {
		BOARD_Y = (int) (mainScreen.getHeight() - GRID_SIZE * TILE_SIZE) / 2;
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

	public boolean tryPlaceOrMerge(ChessPiece piece, double pixelX, double pixelY) {
		Tile tile = getTileAt((int) pixelX, (int) pixelY);
		if (tile == null) return false;
		if (tile.isOccupied()) return false; // TODO: merge logic
		GLabel label = piece.createLabel(tile.getPixelX() + 4, tile.getPixelY() + 50);
		piece.placedOnTile(tile);
		contents.add(label);
		mainScreen.add(label);
		return true;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Tile tile = getTileAt(e.getX(), e.getY());
		if (tile != null && tile.getOccupant() != null) {
			heldPiece = tile.getOccupant();
			heldFromTile = tile;
			heldPiece.removeFromTile();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (heldPiece != null && heldPiece.getLabel() != null) {
			heldPiece.getLabel().setLocation(e.getX() - 10, e.getY() + 10);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (heldPiece == null) return;
		Tile target = getTileAt(e.getX(), e.getY());
		if (target != null && !target.isOccupied()) {
			GLabel label = heldPiece.getLabel();
			heldPiece.placedOnTile(target);
			if (label != null) label.setLocation(target.getPixelX() + 4, target.getPixelY() + 50);
		} else {
			// return to original tile
			heldPiece.placedOnTile(heldFromTile);
			if (heldPiece.getLabel() != null) {
				heldPiece.getLabel().setLocation(heldFromTile.getPixelX() + 4, heldFromTile.getPixelY() + 50);
			}
		}
		heldPiece = null;
		heldFromTile = null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}
}