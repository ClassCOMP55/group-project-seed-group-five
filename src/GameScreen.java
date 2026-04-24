import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class GameScreen extends GraphicsPane {
	private GamePane gamePane;
	private Shoppanel shopPane;

	public GameScreen(MainApplication mainScreen) {
		this.mainScreen = mainScreen;
		gamePane = new GamePane(mainScreen);
		shopPane = new Shoppanel(mainScreen, 70);
		shopPane.setGamePane(gamePane);
		gamePane.setShoppanel(shopPane);
	}

	@Override
	public void showContent() {
		gamePane.showContent();
		shopPane.showContent();
		gamePane.bringButtonsToFront(); // draw buttons on top of shop background
	}

	@Override
	public void hideContent() {
		gamePane.hideContent();
		shopPane.hideContent();
	}

	public void pauseGame()  { gamePane.pauseGame();  }
	public void resumeGame() { gamePane.resumeGame(); }

	@Override public void mousePressed(MouseEvent e) { gamePane.mousePressed(e); shopPane.mousePressed(e); }
	@Override public void mouseReleased(MouseEvent e) { gamePane.mouseReleased(e); shopPane.mouseReleased(e); }
	@Override public void mouseClicked(MouseEvent e) { gamePane.mouseClicked(e); shopPane.mouseClicked(e); }
	@Override public void mouseDragged(MouseEvent e) { gamePane.mouseDragged(e); shopPane.mouseDragged(e); }
	@Override public void mouseMoved(MouseEvent e) { gamePane.mouseMoved(e); shopPane.mouseMoved(e); }
	@Override public void keyPressed(KeyEvent e) { gamePane.keyPressed(e); shopPane.keyPressed(e); }
	@Override public void keyReleased(KeyEvent e) { gamePane.keyReleased(e); shopPane.keyReleased(e); }
	@Override public void keyTyped(KeyEvent e) { gamePane.keyTyped(e); shopPane.keyTyped(e); }
}
