import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

import acm.graphics.*;

/**
 * ShopPane — a GraphicsPane that renders the right-side shop panel.
 *
 * Layout
 * ──────
 *  [Gold label]  [Refresh btn]
 *  [ slot ][ slot ][ slot ][ slot ][ slot ]   ← 5 random shop slots
 *  [ drag hint bar ]
 *  [ held-piece preview ]
 *
 * Merge mechanic (Clash Royale / auto-chess style)
 * ────────────────────────────────────────────────
 *  • Player buys a piece → it is held (dragged).
 *  • Drop on an empty board tile → place it.
 *  • Drop on a board tile already occupied by the SAME piece type
 *    at the SAME tier → merge into the next tier.
 *  • The shop refreshes (randomises) each prep phase, or manually
 *    for a small gold cost.
 *
 * Integration notes
 * ─────────────────
 *  • Call showContent() / hideContent() through the normal GraphicsPane contract.
 *  • The MainApplication (or GamePane) must call:
 *      shop.setGold(int)  to award gold
 *      shop.getGold()     to read current balance
 *      shop.rollShop()    to randomise slots (call at start of each prep phase)
 *  • Drag-and-drop onto the board is handled here via mouseDragged / mouseReleased;
 *    the board pane should expose a method like
 *      board.tryPlaceOrMerge(ChessPiece, double pixelX, double pixelY)
 *    and return true on success. Wire it up in the handleDrop() method below.
 */
public class Shoppanel extends GraphicsPane{

    // -----------------------------------------------------------------------
    // Layout constants
    // -----------------------------------------------------------------------
    private static final int SHOP_X        = 420;   // left edge of shop panel
    private static final int SHOP_Y        = 20;
    private static final int SHOP_W        = 360;
    private static final int SHOP_H        = 560;

    private static final int SLOT_COUNT    = 5;
    private static final int SLOT_W        = 60;
    private static final int SLOT_H        = 80;
    private static final int SLOT_GAP      = 8;
    private static final int SLOTS_START_Y = SHOP_Y + 80;  // below gold/refresh row
    private static final int SLOTS_START_X = SHOP_X + 10;

    private static final int GOLD_LABEL_X  = SHOP_X + 10;
    private static final int GOLD_LABEL_Y  = SHOP_Y + 50;

    private static final int REFRESH_X     = SHOP_X + 180;
    private static final int REFRESH_Y     = SHOP_Y + 28;
    private static final int REFRESH_W     = 160;
    private static final int REFRESH_H     = 36;
    private static final int REFRESH_COST  = 2;

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
    private GLabel       hintLabel;
    private GLabel       heldLabel;      // floating ghost label during drag

    private Random rng = new Random();

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------
    public Shoppanel(MainApplication mainScreen, int startingGold) {
        super();
        this.contents = new ArrayList<GObject>();
        this.mainScreen = mainScreen;
        this.gold       = startingGold;
        this.slots      = new ChessPiece[SLOT_COUNT];
        this.slotBgs    = new GRect[SLOT_COUNT];
        this.slotLabels      = new GLabel[SLOT_COUNT];
        this.slotCostLabels  = new GLabel[SLOT_COUNT];
        rollShop();
    }

    // -----------------------------------------------------------------------
    // GraphicsPane lifecycle
    // -----------------------------------------------------------------------

    public void showContent() {
        drawPanel();
        drawGoldRow();
        drawRefreshButton();
        drawSlots();
        drawHintBar();
    }

    public void hideContent() {
        for (GObject obj : contents) {
            mainScreen.remove(obj);
        }
        contents.clear();
        panelBg = null;
        goldLabel = null;
        refreshBg = null;
        refreshLabel = null;
        hintLabel = null;
        heldLabel = null;
    }

    // -----------------------------------------------------------------------
    // Drawing helpers
    // -----------------------------------------------------------------------

    private void drawPanel() {
        panelBg = new GRect(SHOP_X, SHOP_Y, SHOP_W, SHOP_H);
        panelBg.setFilled(true);
        panelBg.setFillColor(new Color(0x1A1A2E));    // dark navy background
        panelBg.setColor(new Color(0x534AB7));         // purple border
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
                GLabel lbl = p.createLabel(x + SLOT_W / 2.0 - 10, y + 30);
                lbl.setFont("DialogInput-BOLD-18");
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
    }

    // Convenience: add to canvas and track in contents list
    private void add(GObject obj) {
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
        if (p == null)         return null;
        if (gold < p.getCost()) {
            flashSlotRed(slotIndex);
            return null;
        }
        gold -= p.getCost();
        slots[slotIndex] = null;
        refreshGoldDisplay();
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
        slotLabels[i]     = null;
        slotCostLabels[i] = null;

        int x = SLOTS_START_X + i * (SLOT_W + SLOT_GAP);
        int y = SLOTS_START_Y;

        ChessPiece p = slots[i];
        if (p != null) {
            GLabel lbl = p.createLabel(x + SLOT_W / 2.0 - 10, y + 30);
            lbl.setFont("DialogInput-BOLD-18");
            slotLabels[i] = lbl;
            add(lbl);

            GLabel cost = new GLabel(p.getCost() + "g", x + 10, y + SLOT_H - 10);
            cost.setFont("DialogInput-BOLD-12");
            cost.setColor(new Color(0xEF9F27));
            slotCostLabels[i] = cost;
            add(cost);
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

    public void mouseClicked(MouseEvent e) {
        double x = e.getX(), y = e.getY();

        // Refresh button
        if (isRefreshButton(x, y)) {
            buyRefresh();
            return;
        }

        // Shop slot click — buy piece and immediately "hold" it
        int slotIdx = getSlotAt(x, y);
        if (slotIdx >= 0) {
            ChessPiece bought = buySlot(slotIdx);
            if (bought != null) {
                holdPiece(bought, slotIdx, x, y);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    	super.mousePressed(e);
        double x = e.getX(), y = e.getY();
        int slotIdx = getSlotAt(x, y);
        if (slotIdx >= 0 && slots[slotIdx] != null) {
            // Start drag without buying yet (buy on release at valid tile)
            heldPiece     = slots[slotIdx];
            heldSlotIndex = slotIdx;
            showDragGhost(heldPiece, x, y);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        moveDragGhost(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (heldPiece == null) return;
        double x = e.getX(), y = e.getY();

        boolean placed = handleDrop(heldPiece, x, y);
        if (placed) {
            // Deduct gold and clear the slot
            if (heldSlotIndex >= 0) {
                gold -= heldPiece.getCost();
                slots[heldSlotIndex] = null;
                refreshGoldDisplay();
                refreshSlot(heldSlotIndex);
            }
        }
        clearDragGhost();
        heldPiece     = null;
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
        // TODO: replace with real board integration
        // Example:
        //   return mainScreen.getGamePane().tryPlaceOrMerge(piece, pixelX, pixelY);
        System.out.println("Drop: " + piece + " at (" + pixelX + ", " + pixelY + ")");
        return false; // stub — return false so piece goes back to shop for now
    }
}