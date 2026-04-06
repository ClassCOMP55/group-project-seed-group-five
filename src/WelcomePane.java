import java.awt.event.MouseEvent;

import acm.graphics.GImage;
import acm.graphics.GObject;

public class WelcomePane extends GraphicsPane{
	public WelcomePane(MainApplication mainScreen) {
		this.mainScreen = mainScreen;
	}
	
	@Override
	public void showContent() {
		addPicture();
		addDescriptionButton();
		addPlayButton();
	}

	@Override
	public void hideContent() {
		for(GObject item : contents) {
			mainScreen.remove(item);
		}
		contents.clear();
	}
	
	private void addPicture(){
		GImage startImage = new GImage("Media/Main Menu.png", 325, 65);
		startImage.scale(1.0, 1.0);
		
		contents.add(startImage);
		mainScreen.add(startImage);
	}
	
	private void addPlayButton() {
		GImage playButton = new GImage("Media/play.png", 575, 390);
		playButton.scale(2.0, 2.0);
		
		contents.add(playButton);
		mainScreen.add(playButton);
		
	}
	
	private void addDescriptionButton() {
		GImage moreButton = new GImage("Media/settings.png", 575, 565);
		moreButton.scale(2.0, 2.0);
		
		contents.add(moreButton);
		mainScreen.add(moreButton);
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		GObject clicked = mainScreen.getElementAtLocation(e.getX(), e.getY());
		if (clicked == contents.get(1)) {
			mainScreen.switchToDescriptionScreen();
		} else if (clicked == contents.get(2)) {
			mainScreen.switchToGameScreen();
		}
	}

}
