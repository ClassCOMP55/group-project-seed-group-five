import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private Shoppanel shop;
	private final Map<ChessPiece, List<GLabel>> outlineLabels = new HashMap<>();
	private final Map<ChessPiece, GLabel> tierLabels = new HashMap<>();

	private final List<UnitBase> enemies = new ArrayList<>();
	private Timer gameTimer;
	// Columns: [Original, LightBlue, Purple, Red, DarkBlue, Pink, Yellow]
	private static final int[][] WAVE_DATA = {
		{10,  0,   0,  0,  0,  0,  0},  // Round 1  — basics only
		{20,  5,   0,  0,  0,  0,  0},  // Round 2  — introduce LightBlue
		{15,  10,  0,  5,  0,  0,  0},  // Round 3  — add Red (fast)
		{20,  12,  2,  8,  0,  0,  0},  // Round 4  — first Purples
		{10,  15,  5,  10, 1,  0,  0},  // Round 5  — first DarkBlue
		{12,  12,  8,  10, 2,  5,  0},  // Round 6  — add Pink
		{10,  15,  10, 12, 3,  8,  3},  // Round 7  — all 7 types
		{8,   12,  12, 18, 5,  10, 8},  // Round 8
		{5,   8,   15, 15, 8,  5,  15}, // Round 9
		{0,   10,  20, 20, 10, 8,  20}, // Round 10 — no basics, heavy mix
	};
	private final java.util.LinkedList<Integer> spawnQueue = new java.util.LinkedList<>();
	private GRect playBtn;
	private GLabel playBtnLabel;
	private GLabel waveLabel;
	private GRect  ffBtn;
	private GLabel ffBtnLabel;
	private boolean fastForward = false;

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

	public void setShoppanel(Shoppanel shop) { this.shop = shop; }

	private void sellPiece(ChessPiece piece) {
		int refund = piece.getCost() / 2;
		GLabel label = piece.getLabel();
		if (label != null) { mainScreen.remove(label); contents.remove(label); }
		List<GLabel> outlines = outlineLabels.remove(piece);
		if (outlines != null) for (GLabel o : outlines) { mainScreen.remove(o); contents.remove(o); }
		GLabel tierLbl = tierLabels.remove(piece);
		if (tierLbl != null) { mainScreen.remove(tierLbl); contents.remove(tierLbl); }
		piece.removeFromTile();
		if (shop != null) shop.awardGold(refund);
	}

	private GLabel createTierLabel(ChessPiece piece, GLabel pieceLbl) {
		StringBuilder stars = new StringBuilder();
		if (piece.getTier() == 1) {
			// no label for freshly placed pieces
		} else {
			for (int i = 0; i < piece.getTier(); i++) stars.append("\u2605");
			if (piece.isHalfStar()) stars.append("\u00BD");
		}
		GLabel lbl = new GLabel(stars.toString(), 0, 0);
		lbl.setFont("DialogInput-BOLD-14");
		lbl.setColor(new java.awt.Color(0xFFD700));
		contents.add(lbl);
		mainScreen.add(lbl);
		// Position centered above piece (y is baseline; subtract full piece height to clear it)
		double x = pieceLbl.getX() + (pieceLbl.getWidth() - lbl.getWidth()) / 2.0;
		double y = pieceLbl.getY() - pieceLbl.getHeight() + lbl.getHeight() - 2;
		lbl.setLocation(x, y);
		return lbl;
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
		attackCooldowns.clear();
		animTicks.clear();
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

	private void centerLabelOnTile(GLabel label, Tile tile) {
		double x = tile.getPixelX() + (TILE_SIZE - label.getWidth()) / 2.0 - 2;
		double y = tile.getPixelY() + (TILE_SIZE + label.getHeight()) / 2.0 - 0.5;
		label.setLocation(x, y);
	}

	private GLabel makeTileLabel(ChessPiece piece, Tile tile) {
		// 8-direction outline: add black copies at every offset first so they sit behind
		int[] offsets = {-2, -1, 0, 1, 2};
		List<GLabel> outlines = new ArrayList<>();
		for (int dx : offsets) {
			for (int dy : offsets) {
				if (dx == 0 && dy == 0) continue;
				GLabel o = new GLabel(piece.getSymbol(), 0, 0);
				o.setFont("DialogInput-BOLD-40");
				o.setColor(Color.BLACK);
				contents.add(o);
				mainScreen.add(o);
				outlines.add(o);
			}
		}

		GLabel label = piece.createLabel(0, 0);
		label.setLabel(piece.getSymbol());
		label.setFont("DialogInput-BOLD-40");
		contents.add(label);
		mainScreen.add(label);

		centerLabelOnTile(label, tile);
		repositionOutlines(outlines, label.getX(), label.getY());
		outlineLabels.put(piece, outlines);
		// Show ½ star for freshly placed tier-1 pieces
		GLabel tierLbl = createTierLabel(piece, label);
		tierLabels.put(piece, tierLbl);
		return label;
	}

	private void repositionOutlines(List<GLabel> outlines, double baseX, double baseY) {
		int i = 0;
		int[] offsets = {-2, -1, 0, 1, 2};
		for (int dx : offsets) {
			for (int dy : offsets) {
				if (dx == 0 && dy == 0) continue;
				outlines.get(i++).setLocation(baseX + dx, baseY + dy);
			}
		}
	}

	public boolean tryPlaceOrMerge(ChessPiece piece, double pixelX, double pixelY) {
		Tile tile = getTileAt((int) pixelX, (int) pixelY);
		if (tile == null || enemyPath.contains(tile)) return false;

		if (tile.isOccupied()) {
			ChessPiece occupant = tile.getOccupant();
			if (!piece.canMergeWith(occupant)) return false;
			occupant.promote();
			// Bump font size so the player sees the upgrade
			int newSize = 40 + (occupant.getTier() - 1) * 6;
			GLabel lbl = occupant.getLabel();
			if (lbl != null) {
				lbl.setFont("DialogInput-BOLD-" + newSize);
				List<GLabel> outlines = outlineLabels.get(occupant);
				if (outlines != null) {
					for (GLabel o : outlines) o.setFont("DialogInput-BOLD-" + newSize);
					repositionOutlines(outlines, lbl.getX(), lbl.getY());
				}
				// Update or create tier star label
				GLabel oldTier = tierLabels.remove(occupant);
				if (oldTier != null) { mainScreen.remove(oldTier); contents.remove(oldTier); }
				GLabel newTier = createTierLabel(occupant, lbl);
				tierLabels.put(occupant, newTier);
			}
			// Show upgrade paths at tier 2 and tier 3, but NOT at the half-star step
			if (shop != null && !occupant.isHalfStar()) shop.showUpgradePaths(occupant);
			return true;
		}

		makeTileLabel(piece, tile);
		piece.placedOnTile(tile);
		return true;
	}

	private void placeKing() {
		int col = new Random().nextInt(GRID_SIZE);
		Tile tile = tiles[GRID_SIZE - 1][col];
		king = new King();
		makeTileLabel(king, tile);
		king.placedOnTile(tile);
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
		playBtn = new GRect(btnX, btnY, 130, 34);
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

		waveLabel = new GLabel("Wave: 0", btnX + 145, btnY + 22);
		waveLabel.setFont("DialogInput-BOLD-13");
		waveLabel.setColor(new Color(0xFFD700));
		contents.add(waveLabel);
		mainScreen.add(waveLabel);

		int ffX = btnX + 145 + 70;
		ffBtn = new GRect(ffX, btnY, 60, 34);
		ffBtn.setFilled(true);
		ffBtn.setFillColor(new Color(60, 60, 140));
		ffBtn.setColor(new Color(100, 100, 220));
		contents.add(ffBtn);
		mainScreen.add(ffBtn);
		ffBtnLabel = new GLabel("\u25BA\u25BA 1x", ffX + 8, btnY + 22);
		ffBtnLabel.setFont("DialogInput-BOLD-12");
		ffBtnLabel.setColor(Color.WHITE);
		contents.add(ffBtnLabel);
		mainScreen.add(ffBtnLabel);
	}

	private static final double ENEMY_SPEED    = 2.0;  // pixels per frame
	private static final int    SPAWN_INTERVAL = 90;   // wave 1 interval (~1.5s at 16ms)
	private int currentSpawnInterval = SPAWN_INTERVAL;
	private int tickCount = 0;

	private final Map<ChessPiece, Integer> attackCooldowns = new HashMap<>();
	private final Map<ChessPiece, Integer> animTicks       = new HashMap<>();
	private static final int    ATTACK_COOLDOWN = 90;   // ticks between attacks (~1.5s)
	private static final int    ANIM_DURATION   = 20;   // ticks for bounce animation
	private static final double BOUNCE_HEIGHT   = 22.0; // pixels to bounce upward

	private int waveNumber = 0;

	private void startWave() {
		if (gameTimer != null && gameTimer.isRunning()) return;
		waveNumber++;
		spawnQueue.clear();
		tickCount = 0;
		// Build shuffled spawn list from WAVE_DATA (all 7 types)
		int waveIdx = Math.min(waveNumber, WAVE_DATA.length) - 1;
		int[] counts = WAVE_DATA[waveIdx];
		List<Integer> all = new ArrayList<>();
		for (int type = 0; type < counts.length; type++) {
			for (int n = 0; n < counts[type]; n++) all.add(type);
		}
		Collections.shuffle(all, rng);
		spawnQueue.addAll(all);
		currentSpawnInterval = waveNumber == 1 ? 90 : Math.max(20, 90 - (waveNumber - 1) * 10);
		if (waveLabel != null) waveLabel.setLabel("Wave: " + waveNumber);
		gameTimer = new Timer(16, e -> tick());
		gameTimer.start();
	}

private void tick() {
	    tickCount++;
	    if (!spawnQueue.isEmpty() && tickCount % currentSpawnInterval == 0) {
	        int type = spawnQueue.poll();
	        UnitBase enemy;
	        switch (type) {
	            case 1:  enemy = new LightBlueOgre(); break;
	            case 2:  enemy = new PurpleOgre();    break;
	            case 3:  enemy = new RedOgre();        break;
	            case 4:  enemy = new DarkBlueOgre();  break;
	            case 5:  enemy = new PinkOgre();       break;
	            case 6:  enemy = new YellowOgre();    break;
	            default: enemy = new OriginalOgre();
	        }
	        enemy.spawnAt(enemyPath.get(0), mainScreen);
	        enemies.add(enemy);
	    }

	    // Move enemies; collect those that reached the King
	    boolean kingDied = false; // NEW
	    List<UnitBase> toRemove = new ArrayList<>();
	    for (UnitBase enemy : enemies) {
	        if (enemy.step(enemyPath, enemy.getSpeed() * (fastForward ? 2.0 : 1.0))) {
	            enemy.removeFrom(mainScreen);
	            toRemove.add(enemy);
	            damageKing(4);
	            if (kingHp <= 0) kingDied = true; // NEW
	        }
	    }
	    enemies.removeAll(toRemove);

	    if (kingDied) { // NEW
	        gameTimer.stop();
	        mainScreen.triggerGameOver();
	        return;
	    }

	    // Piece attack phase
	    for (int row = 0; row < GRID_SIZE; row++) {
	        for (int col = 0; col < GRID_SIZE; col++) {
	            ChessPiece piece = tiles[row][col].getOccupant();
	            if (piece == null || piece instanceof King) continue;

	            int cd = attackCooldowns.getOrDefault(piece, 0);
	            if (cd > 0) {
	                attackCooldowns.put(piece, cd - 1);
	            } else {
	                for (UnitBase enemy : enemies) {
	                    int eRow = (int)((enemy.getPixelY() - BOARD_Y) / TILE_SIZE);
	                    int eCol = (int)((enemy.getPixelX() - BOARD_X) / TILE_SIZE);
	                    if (eRow >= 0 && eRow < GRID_SIZE && eCol >= 0 && eCol < GRID_SIZE
	                            && piece.canAttack(row, col, eRow, eCol)) {
	                        enemy.takeDamage(piece.getDamage());
	                        int nextCd = piece.getAttackCooldownOverride() >= 0
	                            ? piece.getAttackCooldownOverride() : ATTACK_COOLDOWN;
	                        attackCooldowns.put(piece, nextCd);
	                        animTicks.put(piece, ANIM_DURATION);
	                        break;
	                    }
	                }
	            }
	            
	            // Bounce animation
	            int at = animTicks.getOrDefault(piece, 0);
	            if (at > 0) {
	                animTicks.put(piece, at - 1);
	                double progress = (double) at / ANIM_DURATION;
	                double yOffset = -Math.sin(progress * Math.PI) * BOUNCE_HEIGHT;
	                applyPieceOffset(piece, yOffset);
	            } else if (at == 0 && animTicks.containsKey(piece)) {
	                applyPieceOffset(piece, 0);
	                animTicks.remove(piece);
	            }
	        }
	    }

	    // Remove enemies killed by pieces
	    List<UnitBase> dead = new ArrayList<>();
	    for (UnitBase enemy : enemies) {
	        if (!enemy.isAlive()) {
	            enemy.removeFrom(mainScreen);
	            dead.add(enemy);
	            if (shop != null) shop.awardGold(enemy.getGoldValue());
	        }
	    }
	    enemies.removeAll(dead);

	    if (spawnQueue.isEmpty() && enemies.isEmpty()) gameTimer.stop();
	}

	private void applyPieceOffset(ChessPiece piece, double yOffset) {
		GLabel label = piece.getLabel();
		Tile tile = piece.getTile();
		if (label == null || tile == null) return;
		centerLabelOnTile(label, tile);
		label.setLocation(label.getX(), label.getY() + yOffset);
		List<GLabel> outlines = outlineLabels.get(piece);
		if (outlines != null) repositionOutlines(outlines, label.getX(), label.getY());
		GLabel tierLbl = tierLabels.get(piece);
		if (tierLbl != null) tierLbl.setLocation(
			label.getX() + (label.getWidth() - tierLbl.getWidth()) / 2.0,
			label.getY() - label.getHeight() + tierLbl.getHeight() - 2
		);
	}

	private void addSettingsIcon() {
		sIcon = new GImage("Media/settingIcon.png", 25, 783);
		sIcon.scale(1, 1);
		contents.add(sIcon);
		mainScreen.add(sIcon);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Tile tile = getTileAt(e.getX(), e.getY());
		if (tile == null || tile.getOccupant() == null) return;
		ChessPiece piece = tile.getOccupant();
		if (piece instanceof King) return;

		if (e.getButton() == MouseEvent.BUTTON3) {
			// Right-click: sell piece at 50% cost
			sellPiece(piece);
		} else {
			// Left-click: pick up to drag
			heldPiece = piece;
			heldFromTile = tile;
			heldPiece.removeFromTile();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (heldPiece != null && heldPiece.getLabel() != null) {
			double lx = e.getX() - 10, ly = e.getY() + 10;
			heldPiece.getLabel().setLocation(lx, ly);
			List<GLabel> outlines = outlineLabels.get(heldPiece);
			if (outlines != null) repositionOutlines(outlines, lx, ly);
			GLabel tierLbl = tierLabels.get(heldPiece);
			if (tierLbl != null) {
				GLabel pl = heldPiece.getLabel();
				double tx = lx + (pl.getWidth() - tierLbl.getWidth()) / 2.0;
				double ty = ly - pl.getHeight() + tierLbl.getHeight() - 2;
				tierLbl.setLocation(tx, ty);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (heldPiece == null) return;
		Tile target = getTileAt(e.getX(), e.getY());
		if (target != null && !target.isOccupied() && !enemyPath.contains(target)) {
			GLabel label = heldPiece.getLabel();
			heldPiece.placedOnTile(target);
			if (label != null) {
				centerLabelOnTile(label, target);
				List<GLabel> outlines = outlineLabels.get(heldPiece);
				if (outlines != null) repositionOutlines(outlines, label.getX(), label.getY());
				GLabel tierLbl = tierLabels.get(heldPiece);
				if (tierLbl != null) tierLbl.setLocation(label.getX() + (label.getWidth() - tierLbl.getWidth()) / 2.0, label.getY() - label.getHeight() + tierLbl.getHeight() - 2);
			}
		} else {
			// return to original tile
			heldPiece.placedOnTile(heldFromTile);
			GLabel label = heldPiece.getLabel();
			if (label != null) {
				centerLabelOnTile(label, heldFromTile);
				List<GLabel> outlines = outlineLabels.get(heldPiece);
				if (outlines != null) repositionOutlines(outlines, label.getX(), label.getY());
				GLabel tierLbl = tierLabels.get(heldPiece);
				if (tierLbl != null) tierLbl.setLocation(label.getX() + (label.getWidth() - tierLbl.getWidth()) / 2.0, label.getY() - label.getHeight() + tierLbl.getHeight() - 2);
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
		} else if (clicked == ffBtn || clicked == ffBtnLabel) {
			fastForward = !fastForward;
			ffBtn.setFillColor(fastForward ? new Color(140, 80, 0) : new Color(60, 60, 140));
			ffBtn.setColor(fastForward ? new Color(255, 160, 0) : new Color(100, 100, 220));
			ffBtnLabel.setLabel(fastForward ? "\u25BA\u25BA 2x" : "\u25BA\u25BA 1x");
			if (gameTimer != null && gameTimer.isRunning()) {
				gameTimer.setDelay(fastForward ? 8 : 16);
			}
		}
	}
}