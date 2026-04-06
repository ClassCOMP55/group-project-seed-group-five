import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;
import acm.graphics.*;

public class GamePane extends GraphicsPane {
	private static final int GRID_SIZE = 8;
	private static final int TILE_SIZE = Tile.SIZE;
	private static final int BOARD_X = 100;
	private int BOARD_Y;

	private Tile[][] tiles = new Tile[GRID_SIZE][GRID_SIZE];
	private GRect[][] squares = new GRect[GRID_SIZE][GRID_SIZE];
	private List<Tile> enemyPath = new ArrayList<>();
	private ChessPiece heldPiece;
	private Tile heldFromTile;
	private GImage sIcon;
	private King king;
	private final Random rng = new Random();

	private final List<UnitBase> enemies = new ArrayList<>();
	private Timer gameTimer;
	private int spawnQueue = 0;
	private GRect playBtn;
	private GLabel playBtnLabel;

	private static final int KING_MAX_HP = 20;
	private int kingHp = KING_MAX_HP;
	private static final int HP_BAR_W = 220;
	private static final int HP_BAR_H = 18;
	private GRect hpBarBg;
	private GRect hpBarFill;
	private GLabel hpBarLabel;

	public GamePane(MainApplication mainScreen) {
		this.mainScreen = mainScreen;
	}

	public void showContent() {
		buildGrid();
		placeKing();
		generateEnemyPath();
		addKingHpBar();
		addSettingsIcon();
		addPlayButton();
	}

	public void hideContent() {
		if (gameTimer != null) gameTimer.stop();
		for (UnitBase e : enemies) e.removeFrom(mainScreen);
		enemies.clear();
		kingHp = KING_MAX_HP;
		for (GObject obj : contents) { mainScreen.remove(obj); }
		contents.clear();
	}

	private void buildGrid() {
		BOARD_Y = ((int) (mainScreen.getHeight() - GRID_SIZE * TILE_SIZE) / 2) - 45;
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
				squares[row][col] = square;

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
	
	private void placeKing() {
		int col = new Random().nextInt(GRID_SIZE);
		Tile tile = tiles[GRID_SIZE - 1][col];
		king = new King();
		GLabel label = king.createLabel(tile.getPixelX() + 4, tile.getPixelY() + 50);
		king.placedOnTile(tile);
		contents.add(label);
		mainScreen.add(label);
	}

	private void generateEnemyPath() {
		enemyPath.clear();
		int startCol = rng.nextInt(GRID_SIZE);
		int endCol   = king.getTile().getCol();

		// Two random turn rows so the path has two L-shaped bends
		int turn1Row = 1 + rng.nextInt(2);                              // row 1 or 2
		int turn1Col = rng.nextInt(GRID_SIZE);
		int turn2Row = turn1Row + 1 + rng.nextInt(GRID_SIZE - 2 - turn1Row); // between turn1+1 and row 6

		// Build path as pure horizontal/vertical segments (no diagonals)
		addVerticalSegment(0,           turn1Row, startCol);
		addHorizontalSegment(turn1Row,  startCol, turn1Col);
		addVerticalSegment(turn1Row + 1, turn2Row, turn1Col);
		addHorizontalSegment(turn2Row,  turn1Col, endCol);
		addVerticalSegment(turn2Row + 1, GRID_SIZE - 1, endCol);

		// Color all path tiles dark gray
		for (Tile t : enemyPath) {
			squares[t.getRow()][t.getCol()].setFillColor(new Color(75, 75, 75));
		}
	}

	// fromRow to toRow inclusive, fixed col
	private void addVerticalSegment(int fromRow, int toRow, int col) {
		for (int row = fromRow; row <= toRow; row++) {
			enemyPath.add(tiles[row][col]);
		}
	}

	// fromCol exclusive (corner already added by previous segment), toCol inclusive
	private void addHorizontalSegment(int row, int fromCol, int toCol) {
		int step = (toCol >= fromCol) ? 1 : -1;
		for (int col = fromCol + step; col != toCol + step; col += step) {
			enemyPath.add(tiles[row][col]);
		}
	}

	public List<Tile> getEnemyPath() { return enemyPath; }

	private void addKingHpBar() {
		int barX = BOARD_X;
		int barY = BOARD_Y - 32;

		GLabel icon = new GLabel("\u265A KING HP", barX, barY + 14);
		icon.setFont("DialogInput-BOLD-13");
		icon.setColor(new Color(0xFFD700));
		contents.add(icon);
		mainScreen.add(icon);

		hpBarBg = new GRect(barX + 90, barY, HP_BAR_W, HP_BAR_H);
		hpBarBg.setFilled(true);
		hpBarBg.setFillColor(new Color(60, 60, 60));
		hpBarBg.setColor(Color.BLACK);
		contents.add(hpBarBg);
		mainScreen.add(hpBarBg);

		hpBarFill = new GRect(barX + 90, barY, HP_BAR_W, HP_BAR_H);
		hpBarFill.setFilled(true);
		hpBarFill.setFillColor(new Color(50, 200, 80));
		hpBarFill.setColor(new Color(50, 200, 80));
		contents.add(hpBarFill);
		mainScreen.add(hpBarFill);

		hpBarLabel = new GLabel(kingHp + " / " + KING_MAX_HP, barX + 90 + HP_BAR_W + 8, barY + 14);
		hpBarLabel.setFont("DialogInput-BOLD-12");
		hpBarLabel.setColor(Color.WHITE);
		contents.add(hpBarLabel);
		mainScreen.add(hpBarLabel);
	}

	private void damageKing(int amount) {
		kingHp = Math.max(0, kingHp - amount);
		updateKingHpBar();
	}

	private void updateKingHpBar() {
		if (hpBarFill == null) return;
		double pct = (double) kingHp / KING_MAX_HP;
		hpBarFill.setSize(HP_BAR_W * pct, HP_BAR_H);
		Color fill = pct > 0.25 ? new Color(50, 200, 80) : new Color(200, 50, 50);
		hpBarFill.setFillColor(fill);
		hpBarFill.setColor(fill);
		if (hpBarLabel != null) hpBarLabel.setLabel(kingHp + " / " + KING_MAX_HP);
	}

	private void addPlayButton() {
		int btnX = BOARD_X + (GRID_SIZE * TILE_SIZE) / 2 - 50;
		int btnY = BOARD_Y + GRID_SIZE * TILE_SIZE + 10;
		playBtn = new GRect(btnX, btnY, 110, 34);
		playBtn.setFilled(true);
		playBtn.setFillColor(new Color(30, 120, 50));
		playBtn.setColor(new Color(50, 200, 80));
		contents.add(playBtn);
		mainScreen.add(playBtn);
		playBtnLabel = new GLabel("\u25BA  SEND WAVE", btnX + 10, btnY + 22);
		playBtnLabel.setFont("DialogInput-BOLD-13");
		playBtnLabel.setColor(Color.WHITE);
		contents.add(playBtnLabel);
		mainScreen.add(playBtnLabel);
	}

	private static final double ENEMY_SPEED    = 2.0;  // pixels per frame
	private static final int    SPAWN_INTERVAL = 90;   // frames between each spawn (~1.5s at 16ms)
	private int tickCount = 0;

	private void startWave() {
		if (gameTimer != null && gameTimer.isRunning()) return;
		spawnQueue = 5;
		tickCount  = 0;
		gameTimer  = new Timer(16, e -> tick());
		gameTimer.start();
	}

	private void tick() {
		tickCount++;
		if (spawnQueue > 0 && tickCount % SPAWN_INTERVAL == 0) {
			UnitBase enemy = new UnitBase("Goblin", 50, 8, 1);
			enemy.spawnAt(enemyPath.get(0), mainScreen);
			enemies.add(enemy);
			spawnQueue--;
		}
		List<UnitBase> toRemove = new ArrayList<>();
		for (UnitBase enemy : enemies) {
			if (enemy.step(enemyPath, ENEMY_SPEED)) {
				enemy.removeFrom(mainScreen);
				toRemove.add(enemy);
				damageKing(4);
			}
		}
		enemies.removeAll(toRemove);
		if (spawnQueue == 0 && enemies.isEmpty()) gameTimer.stop();
	}

	private void addSettingsIcon() {
		sIcon = new GImage("settingIcon.png", 25, 783);
		sIcon.scale(1, 1);
		contents.add(sIcon);
		mainScreen.add(sIcon);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Tile tile = getTileAt(e.getX(), e.getY());
		if (tile != null && tile.getOccupant() != null && !(tile.getOccupant() instanceof King)) {
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
	public void mouseClicked(MouseEvent e) {
		GObject clicked = mainScreen.getElementAtLocation(e.getX(), e.getY());
		if (clicked == sIcon) {
			mainScreen.switchToDescriptionFromGame();
		} else if (clicked == playBtn || clicked == playBtnLabel) {
			startWave();
		}
	}
}