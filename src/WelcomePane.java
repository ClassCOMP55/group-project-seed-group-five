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
		GImage startImage = new GImage("Main Menu.png", 200, 200);
		startImage.scale(0.5, 0.5);
		startImage.setLocation((mainScreen.getWidth() - startImage.getWidth())/ 2, 70);
		
		contents.add(startImage);
		mainScreen.add(startImage);
	}
	
	private void addPlayButton() {
		GImage playButton = new GImage("play.png", 200, 400);
		playButton.scale(0.7, 0.7);
		playButton.setLocation((mainScreen.getWidth() - playButton.getWidth())/ 2, 225);
		
		contents.add(playButton);
		mainScreen.add(playButton);
		
	}
	
	private void addDescriptionButton() {
		GImage moreButton = new GImage("settings.png", 200, 400);
		moreButton.scale(0.7, 0.7);
		moreButton.setLocation((mainScreen.getWidth() - moreButton.getWidth())/ 2, 300);
		
		contents.add(moreButton);
		mainScreen.add(moreButton);
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (mainScreen.getElementAtLocation(e.getX(), e.getY()) == contents.get(1)) {
			mainScreen.switchToDescriptionScreen();
		}
	}

}
