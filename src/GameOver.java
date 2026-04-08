import acm.graphics.*;
import java.awt.*;

public class GameOver extends GraphicsPane {

    private static final int DELAY_MS = 4000; // 4 seconds before returning to welcome

    private GRect background;
    private GLabel gameOverLabel;
    private GLabel subLabel;

    public GameOver(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {
        double width  = mainScreen.getWidth();
        double height = mainScreen.getHeight();

        // Semi-transparent dark overlay
        background = new GRect(0, 0, width, height);
        background.setFilled(true);
        background.setColor(new Color(0, 0, 0, 180));
        mainScreen.add(background);

        // "GAME OVER" title
        gameOverLabel = new GLabel("GAME OVER");
        gameOverLabel.setFont(new Font("Serif", Font.BOLD, 80));
        gameOverLabel.setColor(Color.RED);
        double lx = (width  - gameOverLabel.getWidth())  / 2;
        double ly = (height - gameOverLabel.getAscent())  / 2;
        gameOverLabel.setLocation(lx, ly);
        mainScreen.add(gameOverLabel);

        // Subtitle
        subLabel = new GLabel("The King has fallen...");
        subLabel.setFont(new Font("Serif", Font.ITALIC, 32));
        subLabel.setColor(Color.WHITE);
        double sx = (width  - subLabel.getWidth())  / 2;
        double sy = ly + 60;
        subLabel.setLocation(sx, sy);
        mainScreen.add(subLabel);

        // Auto-return to welcome after delay on a background thread
        new Thread(() -> {
            try {
                Thread.sleep(DELAY_MS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            hideContent();
            mainScreen.switchToWelcomeScreen();
        }).start();
    }

    @Override
    public void hideContent() {
        mainScreen.remove(background);
        mainScreen.remove(gameOverLabel);
        mainScreen.remove(subLabel);
    }
}