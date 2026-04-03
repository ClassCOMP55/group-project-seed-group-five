import java.awt.Color;
import java.awt.event.MouseEvent;
import acm.graphics.*;

// this is what appears when settings is clicked at the beginning
public class DescriptionPane extends GraphicsPane{
	private GImage adjustV;
	private GImage exit;
	
	public DescriptionPane(MainApplication mainScreen) {
		this.mainScreen = mainScreen;
	}
	
	@Override
	public void showContent() {
		addRectangle();
		addBackButton();
		exitButton();
		addVolumeSlider();
	}

	@Override
	public void hideContent() {
		for(GObject item : contents) {
			mainScreen.remove(item);
		}
		contents.clear();
	}
	
	private void addRectangle() {
		GRect box = new GRect(225, 75, 350, 450);
		box.setFilled(true);
		box.setFillColor(new Color(242, 235, 226));
		
		// border color
		box.setColor(Color.WHITE);
		
		contents.add(box);
		mainScreen.add(box);
	}
	
	private void addVolumeSlider() {
		GRect sliderTrack = new GRect(275, 170, 265, 10);
		sliderTrack.setFilled(true);
		sliderTrack.setFillColor(Color.GRAY);
		contents.add(sliderTrack);
		mainScreen.add(sliderTrack);
		
		adjustV = new GImage("volumeBar.png", 245, 150);
		adjustV.scale(0.3, 0.3);
		
		// stops image from being dragged
		adjustV.setLocation(245, 150);
		contents.add(adjustV);
		mainScreen.add(adjustV);
	}
	
	private void addBackButton() {
		GImage backButton = new GImage("back.png", 200, 400);
		backButton.scale(1, 1);
		backButton.setLocation((mainScreen.getWidth() - backButton.getWidth())/ 2, 270);
		backButton.setLocation(345, 270);
		contents.add(backButton);
		mainScreen.add(backButton);
	}
	
	private void exitButton() {
		exit = new GImage("exitButton.png", 100, 200);
		exit.scale(0.3, 0.3);
		exit.setLocation(304, 375);
		contents.add(exit);
		mainScreen.add(exit);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		GObject clicked = mainScreen.getElementAtLocation(e.getX(), e.getY());
		 if (clicked == adjustV) {
			 double mouseX = e.getX();
			 if (mouseX < 275) mouseX = 275;
			 if (mouseX > 540) mouseX = 540;
			 double rawPercent = (mouseX - 275) / 265.0;
			 double volumePercent = 1.0 - rawPercent;
			 if (volumePercent < 0.01) volumePercent = 0.01;
			 mainScreen.setVolume(volumePercent);
		    }
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		GObject clicked = mainScreen.getElementAtLocation(e.getX(), e.getY());
		if (clicked == contents.get(1)) {
			mainScreen.switchToWelcomeScreen();
		}
		
		else if(clicked == exit) {
			System.out.println("Closing Game... Goodbye!");
	        System.exit(0);
		}
	}

}
