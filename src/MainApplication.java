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
	
	// background music
	private SoundClip bgMusic;

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
	    
		bgMusic = new SoundClip("media/clair-de-lune.wav");
		bgMusic.setVolume(0.5);
	}
	
	public void run() {
		System.out.println("Lets' Begin!");
		setupInteractions();
		
		//Initialize all Panes
		welcomePane = new WelcomePane(this);
		descriptionPane = new DescriptionPane(this);
		gameScreen = new GameScreen(this);

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
		switchToScreen(descriptionPane);
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
	
	public GObject getElementAtLocation(double x, double y) {
		return getElementAt(x, y);
	}
	
	public void setVolume(double val) {
		if(bgMusic != null) {
			bgMusic.setVolume(val);
		}
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
