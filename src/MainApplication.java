import acm.graphics.GObject;
import acm.program.*;
import acm.util.SoundClip;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.awt.Toolkit;

public class MainApplication extends GraphicsProgram{
	//List of all the full screen panes
	private WelcomePane welcomePane;
	private DescriptionPane descriptionPane;
	private GameScreen gameScreen;
	private GraphicsPane currentScreen;
	private GraphicsPane previousScreen;
	private GameOver gameOver;
	
	// background music
	private SoundClip bgMusic;
	private double currentVolume = 0.8;

	private static final String[] TRACKS = {
		"Media/clair-de-lune.wav",
		"Media/Yoshi's Story Soundtrack - Yo-Yo-Yoshi!.wav"
	};
	private static final String[] TRACK_NAMES = {
		"Clair de Lune",
		"Yoshi's Story - Yo-Yo-Yoshi!"
	};
	private int currentTrackIndex = 0;

	public MainApplication() {
		super();
	}
	
	protected void setupInteractions() {
		requestFocus();
		addKeyListeners();
		addMouseListeners();
	}
	
	public void init() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    int screenWidth = (int) screenSize.getWidth();
	    int screenHeight = (int) screenSize.getHeight();

	    setSize(screenWidth, screenHeight);
	    
		bgMusic = new SoundClip("Media/clair-de-lune.wav");
		bgMusic.setVolume(0.5);
	}
	
	public void run() {
		System.out.println("Lets' Begin!");
		setupInteractions();
		
		//Initialize all Panes
		welcomePane = new WelcomePane(this);
		descriptionPane = new DescriptionPane(this);
		gameScreen = new GameScreen(this);
		gameOver = new GameOver(this);

		//TheDefaultPane
		switchToScreen(welcomePane);
		
		//music
		if(bgMusic != null) {
			bgMusic.setVolume(0.8);
			bgMusic.loop();
		}
	}
	
	public static void main(String[] args) {
		new MainApplication().start();

	}
	
	public void switchToGameScreen() {
		switchToScreen(gameScreen);
	}

	public void switchToDescriptionScreen() {
		descriptionPane.setFromGame(false);
		switchToScreen(descriptionPane);
	}

	public void switchToDescriptionFromGame() {
		gameScreen.pauseGame();
		descriptionPane.setFromGame(true);
		previousScreen = currentScreen;   // keep game drawn underneath
		descriptionPane.showContent();
		currentScreen = descriptionPane;
	}

	public void switchBackFromDescription() {
		descriptionPane.hideContent();
		descriptionPane.setFromGame(false);
		if (previousScreen != null) {
			// Return to game — it's still on-screen, just restore focus
			currentScreen = previousScreen;
			previousScreen = null;
			gameScreen.resumeGame();
		} else {
			// Came from welcome — show welcome fresh (new game state)
			welcomePane.showContent();
			currentScreen = welcomePane;
		}
	}
	
	public void switchToWelcomeScreen() {
		switchToScreen(welcomePane);
	}
	
	
	protected void switchToScreen(GraphicsPane newScreen) {
		if(currentScreen != null) {
			currentScreen.hideContent();
		}
		newScreen.showContent();
		currentScreen = newScreen;
	}
	
	public void triggerGameOver() {
	    switchToScreen(gameOver);
	}
	
	public GObject getElementAtLocation(double x, double y) {
		return getElementAt(x, y);
	}
	
	public void setVolume(double val) {
		currentVolume = val;
		if (bgMusic != null) bgMusic.setVolume(val);
	}

	public String getCurrentTrackName() {
		return TRACK_NAMES[currentTrackIndex];
	}

	public int getTrackCount() {
		return TRACKS.length;
	}

	public void changeTrack(int direction) {
		if (bgMusic != null) bgMusic.stop();
		currentTrackIndex = (currentTrackIndex + direction + TRACKS.length) % TRACKS.length;
		bgMusic = new SoundClip(TRACKS[currentTrackIndex]);
		bgMusic.setVolume(currentVolume);
		bgMusic.loop();
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(currentScreen != null) {
			currentScreen.mousePressed(e);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(currentScreen != null) {
			currentScreen.mouseReleased(e);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(currentScreen != null) {
			currentScreen.mouseClicked(e);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(currentScreen != null) {
			currentScreen.mouseDragged(e);
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		if(currentScreen != null) {
			currentScreen.mouseMoved(e);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(currentScreen != null) {
			currentScreen.keyPressed(e);
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(currentScreen != null) {
			currentScreen.keyReleased(e);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		if(currentScreen != null) {
			currentScreen.keyTyped(e);
		}
	}

}
