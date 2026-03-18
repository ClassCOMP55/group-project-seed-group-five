import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.*;

public class DescriptionPane extends GraphicsPane{
	public DescriptionPane(MainApplication mainScreen) {
		this.mainScreen = mainScreen;
	}
	
	@Override
	public void showContent() {
		addText();
		addBackButton();
	}

	@Override
	public void hideContent() {
		for(GObject item : contents) {
			mainScreen.remove(item);
		}
		contents.clear();
	}
	
	private void addText() {
		GLabel text = new GLabel("This is an example of a new screen with some description!", 100, 70);
		text.setColor(Color.BLUE);
		text.setFont("DialogInput-PLAIN-24");
		text.setLocation((mainScreen.getWidth() - text.getWidth()) / 2, 70);
		
		contents.add(text);
		mainScreen.add(text);
	}
	
	private void addBackButton() {
		GImage backButton = new GImage("returnButton.png", 200, 400);
		backButton.scale(0.3, 0.3);
		backButton.setLocation((mainScreen.getWidth() - backButton.getWidth())/ 2, 400);
		
		contents.add(backButton);
		mainScreen.add(backButton);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (mainScreen.getElementAtLocation(e.getX(), e.getY()) == contents.get(1)) {
			mainScreen.switchToWelcomeScreen();
		}
	}

}
