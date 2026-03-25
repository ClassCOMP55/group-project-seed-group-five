import acm.graphics.GObject;
import acm.program.*;
import acm.util.SoundClip;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class MainApplication extends GraphicsProgram{
	//Settings
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 600;
	
	//List of all the full screen panes
	private WelcomePane welcomePane;
	private DescriptionPane descriptionPane;
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
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		bgMusic = new SoundClip("media/clair-de-lune.wav");
		bgMusic.setVolume(0.5);
	}
	
	public void run() {
		System.out.println("Lets' Begin!");
		setupInteractions();
		
		//Initialize all Panes
		welcomePane = new WelcomePane(this);
		descriptionPane = new DescriptionPane(this);

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
