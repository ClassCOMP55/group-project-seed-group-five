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
	
	private void addDescriptionButton() {
		// need to find a "play button"
		GImage moreButton = new GImage("more.jpeg", 200, 400);
		moreButton.scale(0.3, 0.3);
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
