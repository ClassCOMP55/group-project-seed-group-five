import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

import acm.graphics.*;
public class Shoppanel extends GraphicsPane{

    // -----------------------------------------------------------------------
    // Layout constants
    // -----------------------------------------------------------------------
    private static final int SHOP_X        = 855;   // left edge of shop panel (board ends at 532)
    private static final int SHOP_Y        = 30;
    private static final int SHOP_W        = 520;
    private static final int SHOP_H        = 580;

    private static final int SLOT_COUNT    = 5;
    private static final int SLOT_W        = 82;
    private static final int SLOT_H        = 90;
    private static final int SLOT_GAP      = 14;
    private static final int SLOTS_START_Y = SHOP_Y + 80;  // below gold/refresh row
    // Auto-center slots within the panel
    private static final int SLOTS_START_X = SHOP_X + (SHOP_W - (SLOT_COUNT * SLOT_W + (SLOT_COUNT - 1) * SLOT_GAP)) / 2;

    private static final int GOLD_LABEL_X  = SHOP_X + 10;
    private static final int GOLD_LABEL_Y  = SHOP_Y + 50;

    private static final int REFRESH_X     = SHOP_X + 180;
    private static final int REFRESH_Y     = SHOP_Y + 28;
    private static final int REFRESH_W     = 160;
    private static final int REFRESH_H     = 36;
    private static final int REFRESH_COST  = 4;

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------
    private int gold;
    private ChessPiece[] slots;          // null = empty slot
    private ChessPiece   heldPiece;      // piece being dragged
    private int          heldSlotIndex;  // which shop slot it came from (-1 = board)

    // -----------------------------------------------------------------------
    // Graphics objects (tracked in contents for easy removal)
    // -----------------------------------------------------------------------
    private MainApplication mainScreen;
    private ArrayList<GObject> contents;
    private GRect        panelBg;
    private GLabel       goldLabel;
    private GRect        refreshBg;
    private GLabel       refreshLabel;
    private GRect[]      slotBgs;
    private GLabel[]     slotLabels;
    private GLabel[]     slotCostLabels;
    private GLabel[]     slotNameLabels;
    private GLabel       hintLabel;
    private GLabel       hintLabel2;
    private GLabel       heldLabel;      // floating ghost label during drag

    private GamePane gamePane;
    private Random rng = new Random();

    // Upgrade overlay state
    private boolean            showingUpgrade       = false;
    private ChessPiece         upgradeTarget        = null;
    private GRect[]            upgradeCardBgs       = new GRect[2];
    private ArrayList<GObject> upgradeOverlayItems  = new ArrayList<>();

    public void setGamePane(GamePane gp) { gamePane = gp; }

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------
    private final int startingGold;

    public Shoppanel(MainApplication mainScreen, int startingGold) {
        super();
        this.contents = new ArrayList<GObject>();
        this.mainScreen = mainScreen;
        this.startingGold = startingGold;
        this.gold       = startingGold;
        this.slots      = new ChessPiece[SLOT_COUNT];
        this.slotBgs    = new GRect[SLOT_COUNT];
        this.slotLabels      = new GLabel[SLOT_COUNT];
        this.slotCostLabels  = new GLabel[SLOT_COUNT];
        this.slotNameLabels  = new GLabel[SLOT_COUNT];
        rollShop();
    }

    // -----------------------------------------------------------------------
    // GraphicsPane lifecycle
    // -----------------------------------------------------------------------

    public void showContent() {
        gold = startingGold;
        rollShop();
        drawPanel();
        drawGoldRow();
        drawRefreshButton();
        drawSlots();
        drawHintBar();
    }

    public void hideContent() {
        gold = startingGold;
        heldPiece = null;
        heldSlotIndex = -1;
        for (GObject obj : contents) {
            mainScreen.remove(obj);
        }
        contents.clear();
        panelBg = null;
        goldLabel = null;
        refreshBg = null;
        refreshLabel = null;
        hintLabel  = null;
        hintLabel2 = null;
        heldLabel  = null;
    }

    // -----------------------------------------------------------------------
    // Drawing helpers
    // -----------------------------------------------------------------------

    private void drawPanel() {
        panelBg = new GRect(SHOP_X, SHOP_Y, SHOP_W, SHOP_H);
        panelBg.setFilled(true);
        panelBg.setFillColor(new Color(0x1A1A2E));    // dark navy background
        panelBg.setColor(new Color(0x534AB7));         // purple border
        contents.add(panelBg);
        mainScreen.add(panelBg);

        GLabel title = new GLabel("SHOP", SHOP_X + SHOP_W / 2 - 20, SHOP_Y + 22);
        title.setFont("DialogInput-BOLD-16");
        title.setColor(Color.WHITE);
        add(title);
    }

    private void drawGoldRow() {
        goldLabel = new GLabel(goldText(), GOLD_LABEL_X, GOLD_LABEL_Y);
        goldLabel.setFont("DialogInput-BOLD-16");
        goldLabel.setColor(new Color(0xEF9F27));       // amber
        add(goldLabel);
    }

    private void drawRefreshButton() {
        refreshBg = new GRect(REFRESH_X, REFRESH_Y, REFRESH_W, REFRESH_H);
        refreshBg.setFilled(true);
        refreshBg.setFillColor(new Color(0x26215C));
        refreshBg.setColor(new Color(0x534AB7));
        add(refreshBg);

        refreshLabel = new GLabel("Refresh (" + REFRESH_COST + "g)",
                                  REFRESH_X + 14, REFRESH_Y + 24);
        refreshLabel.setFont("DialogInput-PLAIN-13");
        refreshLabel.setColor(Color.WHITE);
        add(refreshLabel);
    }

    private void drawSlots() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            int x = SLOTS_START_X + i * (SLOT_W + SLOT_GAP);
            int y = SLOTS_START_Y;

            GRect bg = new GRect(x, y, SLOT_W, SLOT_H);
            bg.setFilled(true);
            bg.setFillColor(new Color(0x2A2A4A));
            bg.setColor(new Color(0x7F77DD));
            slotBgs[i] = bg;
            add(bg);

            ChessPiece p = slots[i];
            if (p != null) {
                GLabel lbl = new GLabel(p.getSymbol(), x + SLOT_W / 2.0 - 10, y + 40);
                lbl.setFont("DialogInput-BOLD-28");
                lbl.setColor(p.getPieceColor());
                slotLabels[i] = lbl;
                add(lbl);

                GLabel cost = new GLabel(p.getCost() + "g", x + 10, y + SLOT_H - 10);
                cost.setFont("DialogInput-BOLD-12");
                cost.setColor(new Color(0xEF9F27));
                slotCostLabels[i] = cost;
                add(cost);

                GLabel name = new GLabel(p.getName(), x + 4, y + SLOT_H - 24);
                name.setFont("DialogInput-PLAIN-11");
                name.setColor(Color.LIGHT_GRAY);
                slotNameLabels[i] = name;
                add(name);
            } else {
                slotLabels[i]     = null;
                slotCostLabels[i] = null;
            }
        }
    }

    private void drawHintBar() {
        hintLabel = new GLabel("Click to buy · Drag to place · Drop on same piece to merge",
                               SHOP_X + 6, SLOTS_START_Y + SLOT_H + 22);
        hintLabel.setFont("DialogInput-PLAIN-11");
        hintLabel.setColor(Color.GRAY);
        add(hintLabel);

        hintLabel2 = new GLabel("Right-click a board piece to sell · only 50% is refunded",
                                SHOP_X + 6, SLOTS_START_Y + SLOT_H + 38);
        hintLabel2.setFont("DialogInput-PLAIN-11");
        hintLabel2.setColor(Color.GRAY);
        add(hintLabel2);
    }

    // Convenience: add to canvas and track in contents list
    private void add(GObject obj) {
        contents.add(obj);
        mainScreen.add(obj);
    }

    // -----------------------------------------------------------------------
    // Upgrade path overlay
    // -----------------------------------------------------------------------

    public void showUpgradePaths(ChessPiece piece) {
        showingUpgrade = true;
        upgradeTarget  = piece;

        // Position below the hint labels (slots end at SLOTS_START_Y+SLOT_H, hints ~+38)
        int overlayY = SLOTS_START_Y + SLOT_H + 55;
        int cardW    = (SHOP_W - 30) / 2;
        int cardH    = 150;

        // Divider line + title section
        GRect divider = new GRect(SHOP_X + 10, overlayY - 12, SHOP_W - 20, 1);
        divider.setFilled(true);
        divider.setFillColor(new Color(0x534AB7));
        divider.setColor(new Color(0x534AB7));
        addOverlay(divider);

        GLabel title = new GLabel("Choose Your Path  \u2014  " + piece.getName(), SHOP_X + 10, overlayY + 22);
        title.setFont("DialogInput-BOLD-13");
        title.setColor(Color.WHITE);
        addOverlay(title);

        String[] names = piece.getUpgradePathNames();
        String[] descs = piece.getUpgradePathDescs();

        for (int i = 0; i < 2; i++) {
            int cardX = SHOP_X + 10 + i * (cardW + 10);
            int cardY = overlayY + 32;

            GRect card = new GRect(cardX, cardY, cardW, cardH);
            card.setFilled(true);
            card.setFillColor(new Color(0x1E1B4B));
            card.setColor(new Color(0x7F77DD));
            upgradeCardBgs[i] = card;
            addOverlay(card);

            GLabel nameLabel = new GLabel(names[i], cardX + 10, cardY + 24);
            nameLabel.setFont("DialogInput-BOLD-14");
            nameLabel.setColor(new Color(0xFFD700));
            addOverlay(nameLabel);

            GLabel symLabel = new GLabel(piece.getSymbol(), cardX + cardW - 30, cardY + 26);
            symLabel.setFont("DialogInput-BOLD-20");
            symLabel.setColor(piece.getPieceColor());
            addOverlay(symLabel);

            GLabel descLabel = new GLabel(descs[i], cardX + 10, cardY + 50);
            descLabel.setFont("DialogInput-PLAIN-11");
            descLabel.setColor(Color.LIGHT_GRAY);
            addOverlay(descLabel);

            GLabel clickHint = new GLabel("Click to choose", cardX + 10, cardY + cardH - 12);
            clickHint.setFont("DialogInput-PLAIN-10");
            clickHint.setColor(new Color(0x7F77DD));
            addOverlay(clickHint);
        }
    }

    private void hideUpgradeOverlay() {
        for (GObject obj : upgradeOverlayItems) {
            mainScreen.remove(obj);
            contents.remove(obj);
        }
        upgradeOverlayItems.clear();
        upgradeCardBgs[0] = null;
        upgradeCardBgs[1] = null;
        showingUpgrade = false;
        upgradeTarget  = null;
    }

    private void addOverlay(GObject obj) {
        upgradeOverlayItems.add(obj);
        contents.add(obj);
        mainScreen.add(obj);
    }

    // -----------------------------------------------------------------------
    // Shop logic
    // -----------------------------------------------------------------------

    /**
     * Randomises all shop slots. Call at the start of every prep phase.
     * Existing pieces in slots are discarded (not refunded).
     */
    public void rollShop() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            slots[i] = randomPiece();
        }
    }

    /** Produces a random piece weighted by cost (cheaper pieces appear more often). */
    private ChessPiece randomPiece() {
        int r = rng.nextInt(10);
        if (r < 4) return new Pawn();
        if (r < 6) return new Bishop();
        if (r < 8) return new Knight();
        if (r < 9) return new Rook();
        return new Queen();
    }

    /**
     * Attempt to purchase the piece in the given slot index.
     * Returns the ChessPiece on success, null on failure (insufficient gold / empty slot).
     */
    public ChessPiece buySlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= SLOT_COUNT) return null;

        ChessPiece p = slots[slotIndex];
        if (p == null) return null;

        if (!spendGold(p.getCost())) {
            flashSlotRed(slotIndex);
            return null;
        }

        slots[slotIndex] = null;
        refreshSlot(slotIndex);
        return p;
    }
    
    /** Spend gold to refresh all slots. Returns false if not enough gold. */
    public boolean buyRefresh() {
        if (gold < REFRESH_COST) return false;
        gold -= REFRESH_COST;
        rollShop();
        refreshGoldDisplay();
        redrawSlots();
        return true;
    }

    // -----------------------------------------------------------------------
    // Gold management
    // -----------------------------------------------------------------------
    
    public boolean spendGold(int amount) {
        if (amount < 0) return false;
        if (gold < amount) return false;
        gold -= amount;
        refreshGoldDisplay();
        return true;
    }
    
    public void awardGold(int amount) {
        gold += amount;
        refreshGoldDisplay();
    }

    public void setGold(int amount) {
        gold = amount;
        refreshGoldDisplay();
    }

    public int getGold() { return gold; }

    private String goldText() { return "\u25CF  Gold: " + gold; }

    private void refreshGoldDisplay() {
        if (goldLabel != null) goldLabel.setLabel(goldText());
    }

    // -----------------------------------------------------------------------
    // Slot redraw helpers
    // -----------------------------------------------------------------------

    private void refreshSlot(int i) {
        // Remove old slot labels
        if (slotLabels[i] != null)     { mainScreen.remove(slotLabels[i]);     contents.remove(slotLabels[i]); }
        if (slotCostLabels[i] != null) { mainScreen.remove(slotCostLabels[i]); contents.remove(slotCostLabels[i]); }
        if (slotNameLabels[i] != null) { mainScreen.remove(slotNameLabels[i]); contents.remove(slotNameLabels[i]); }
        slotLabels[i]     = null;
        slotCostLabels[i] = null;
        slotNameLabels[i] = null;

        int x = SLOTS_START_X + i * (SLOT_W + SLOT_GAP);
        int y = SLOTS_START_Y;

        ChessPiece p = slots[i];
        if (p != null) {
            GLabel lbl = new GLabel(p.getSymbol(), x + SLOT_W / 2.0 - 10, y + 40);
            lbl.setFont("DialogInput-BOLD-28");
            lbl.setColor(p.getPieceColor());
            slotLabels[i] = lbl;
            add(lbl);

            GLabel cost = new GLabel(p.getCost() + "g", x + 10, y + SLOT_H - 10);
            cost.setFont("DialogInput-BOLD-12");
            cost.setColor(new Color(0xEF9F27));
            slotCostLabels[i] = cost;
            add(cost);

            GLabel name = new GLabel(p.getName(), x + 4, y + SLOT_H - 24);
            name.setFont("DialogInput-PLAIN-11");
            name.setColor(Color.LIGHT_GRAY);
            slotNameLabels[i] = name;
            add(name);
        }
        if (slotBgs[i] != null) {
            slotBgs[i].setFillColor(new Color(0x2A2A4A)); // reset colour
        }
    }

    private void redrawSlots() {
        for (int i = 0; i < SLOT_COUNT; i++) refreshSlot(i);
    }

    private void flashSlotRed(int i) {
        if (slotBgs[i] != null) {
            slotBgs[i].setFillColor(new Color(0x6B1010)); // brief red tint
            // In a real game loop you'd reset this after a short timer.
            // For simplicity, it resets next time refreshSlot is called.
        }
    }

    // -----------------------------------------------------------------------
    // Drag-and-drop support
    // -----------------------------------------------------------------------

    /**
     * Returns the shop slot index under pixel (x, y), or -1 if none.
     */
    public int getSlotAt(double x, double y) {
        for (int i = 0; i < SLOT_COUNT; i++) {
            int sx = SLOTS_START_X + i * (SLOT_W + SLOT_GAP);
            int sy = SLOTS_START_Y;
            if (x >= sx && x <= sx + SLOT_W && y >= sy && y <= sy + SLOT_H) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns true if the pixel (x, y) is on the Refresh button.
     */
    public boolean isRefreshButton(double x, double y) {
        return x >= REFRESH_X && x <= REFRESH_X + REFRESH_W
            && y >= REFRESH_Y && y <= REFRESH_Y + REFRESH_H;
    }

    // -----------------------------------------------------------------------
    // Mouse events
    // -----------------------------------------------------------------------

    @Override
    public void mouseClicked(MouseEvent e) {
        double x = e.getX(), y = e.getY();

        if (showingUpgrade) {
            for (int i = 0; i < 2; i++) {
                if (upgradeCardBgs[i] != null && upgradeCardBgs[i].contains(x, y)) {
                    upgradeTarget.applyUpgradePath(i);
                    hideUpgradeOverlay();
                }
            }
            return;
        }

        if (isRefreshButton(x, y)) {
            buyRefresh();
            return;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        
        if (heldPiece != null) return;
        double x = e.getX(), y = e.getY();

        int slotIdx = getSlotAt(x, y);
        if (slotIdx >= 0 && slots[slotIdx] != null && gold >= slots[slotIdx].getCost()) {
            heldPiece = slots[slotIdx];
            heldSlotIndex = slotIdx;
            showDragGhost(heldPiece,x , y);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    	if (heldPiece == null) return;
    	
        Tile mergeTile = null;
        
        if (gamePane != null) {
        	gamePane.previewMergeHover(heldPiece, e.getX(), e.getY());
        	mergeTile = gamePane.getValidMergeTile(heldPiece,  e.getX(), e.getY());
        }
        
        if (mergeTile != null && heldLabel != null) {
        	double snapX = mergeTile.getPixelX() + (Tile.SIZE - heldLabel.getWidth()) / 2.0;
        	double snapY = mergeTile.getPixelY() + (Tile.SIZE + heldLabel.getHeight()) / 2.0;
        	heldLabel.setLocation(snapX, snapY);
        }
        else {moveDragGhost(e.getX(), e.getY()); }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        double x = e.getX(), y = e.getY();

        // Handle refresh click when no piece is being dragged
        if (heldPiece == null) {
            if (isRefreshButton(x, y)) buyRefresh();
            return;
        }

        ChessPiece dropping = heldPiece;
        
        boolean placed = handleDrop (dropping, x, y);
        
        if (gamePane != null) {gamePane.clearMergePreview();}
        
        if (placed) {
        	if (heldSlotIndex >= 0) {
        		gold -= dropping.getCost();
        		slots[heldSlotIndex] = null;
        		refreshGoldDisplay();
        		refreshSlot(heldSlotIndex);
        	}
        }
        //Should now be the case where if the piece is not placed on board then nothing will happen
        //The piece will go back to the shop
        
        clearDragGhost();
        heldPiece = null;
        heldSlotIndex = -1;
    }

    // -----------------------------------------------------------------------
    // Drag ghost helpers
    // -----------------------------------------------------------------------

    private void holdPiece(ChessPiece p, int slotIdx, double x, double y) {
        heldPiece     = p;
        heldSlotIndex = slotIdx;
        showDragGhost(p, x, y);
    }

    private void showDragGhost(ChessPiece p, double x, double y) {
        clearDragGhost();
        heldLabel = new GLabel(p.getSymbol(), x, y);
        heldLabel.setFont("DialogInput-BOLD-28");
        heldLabel.setColor(new Color(p.getPieceColor().getRed(),
                                     p.getPieceColor().getGreen(),
                                     p.getPieceColor().getBlue(), 180));
        contents.add(heldLabel);
        mainScreen.add(heldLabel);
    }

    private void moveDragGhost(double x, double y) {
        if (heldLabel != null) heldLabel.setLocation(x - 14, y - 14);
    }

    private void clearDragGhost() {
        if (heldLabel != null) {
            mainScreen.remove(heldLabel);
            contents.remove(heldLabel);
            heldLabel = null;
        }
    }

    // -----------------------------------------------------------------------
    // Drop handler — wire up to your board pane here
    // -----------------------------------------------------------------------

    /**
     * Called when the player releases a dragged piece.
     * Override or extend this method to integrate with your board/game pane.
     *
     * The default implementation is a stub that always returns false.
     * Replace the body with a call to your board's placement method, e.g.:
     *
     *   return mainScreen.getGamePane().tryPlaceOrMerge(piece, pixelX, pixelY);
     *
     * tryPlaceOrMerge should:
     *  1. Find the Tile under (pixelX, pixelY).
     *  2. If empty → place the piece, return true.
     *  3. If occupied by same piece type + same tier → merge them, return true.
     *  4. Otherwise → return false (piece snaps back to shop).
     */
    protected boolean handleDrop(ChessPiece piece, double pixelX, double pixelY) {
        if (gamePane != null) return gamePane.tryPlaceOrMerge(piece, pixelX, pixelY);
        return false;
    }
}