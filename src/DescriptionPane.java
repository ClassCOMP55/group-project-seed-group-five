import java.awt.Color;
import java.awt.event.MouseEvent;
import acm.graphics.*;



public class DescriptionPane extends GraphicsPane{
	public DescriptionPane(MainApplication mainScreen) {
		this.mainScreen = mainScreen;
	}
	
	@Override
	public void showContent() {
		addRectangle();
		addBackButton();
	}

	@Override
	public void hideContent() {
		for(GObject item : contents) {
			mainScreen.remove(item);
		}
		contents.clear();
	}
	
	private void addRectangle() {
		GRect box = new GRect(250, 50, 310, 450);
		box.setFilled(true);
		box.setFillColor(new Color(242, 235, 226));
		box.setColor(Color.WHITE);
		contents.add(box);
		mainScreen.add(box);
	}
	
	private void addBackButton() {
		GImage backButton = new GImage("back.png", 200, 400);
		backButton.scale(1, 1);
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
