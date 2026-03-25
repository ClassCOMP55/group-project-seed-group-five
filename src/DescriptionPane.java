import java.awt.Color;
import java.awt.event.MouseEvent;
import acm.graphics.*;

// this is what appears when settings is clicked at the beginning
public class DescriptionPane extends GraphicsPane{
	private GImage adjustV;
	private final double SLIDER_X = 200;
	private final double SLIDER_WIDTH = 400;
	
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
		adjustV.setLocation(245, 150)
		;
		contents.add(adjustV);
		mainScreen.add(adjustV);
	}
	
	private void addBackButton() {
		GImage backButton = new GImage("back.png", 200, 400);
		backButton.scale(1, 1);
		backButton.setLocation((mainScreen.getWidth() - backButton.getWidth())/ 2, 270);
		
		contents.add(backButton);
		mainScreen.add(backButton);
	}
	
	private void exitButton() {
		GImage exit = new GImage("exitButton.png", 100, 200);
		exit.scale(0.3, 0.3);
		exit.setLocation((mainScreen.getWidth() - exit.getWidth())/ 2, 375);
		
		contents.add(exit);
		mainScreen.add(exit);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		GObject clicked = mainScreen.getElementAtLocation(e.getX(), e.getY());
		if(clicked == adjustV) {
			double newX = e.getX();
			if(newX < SLIDER_X) newX = SLIDER_X;
			if(newX > SLIDER_X + SLIDER_WIDTH) newX = SLIDER_X + SLIDER_WIDTH;
			double volumePercent = (newX - SLIDER_X)/SLIDER_WIDTH;
			mainScreen.setVolume(volumePercent);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (mainScreen.getElementAtLocation(e.getX(), e.getY()) == contents.get(1)) {
			mainScreen.switchToWelcomeScreen();
		}
	}

}
