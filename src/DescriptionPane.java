import java.awt.Color;
import java.awt.event.MouseEvent;
import acm.graphics.*;

// this is what appears when settings is clicked at the beginning
public class DescriptionPane extends GraphicsPane{
	private GImage adjustV;
	private GImage exit;
	private boolean fromGame = false;

	public DescriptionPane(MainApplication mainScreen) {
		this.mainScreen = mainScreen;
	}

	public void setFromGame(boolean fromGame) {
		this.fromGame = fromGame;
	}

	@Override
	public void showContent() {
		addRectangle();
		addBackButton();
		exitButton();
		addVolumeSlider();
		if (fromGame) addGameWarning();
	}

	@Override
	public void hideContent() {
		for(GObject item : contents) {
			mainScreen.remove(item);
		}
		contents.clear();
	}
	
	private void addRectangle() {
		GRect box = new GRect(325, 57, 750, 750);
		box.setFilled(true);
		
		// beige fill color
		box.setFillColor(new Color(242, 235, 226));
		
		// border color
		box.setColor(Color.WHITE);
		
		contents.add(box);
		mainScreen.add(box);
	}
	
	private void addVolumeSlider() {
		GRect sliderTrack = new GRect(420, 170, 265, 20);
		sliderTrack.setFilled(true);
		sliderTrack.setFillColor(Color.GRAY);
		contents.add(sliderTrack);
		mainScreen.add(sliderTrack);
		
		adjustV = new GImage("Media/volumeBar.png", 245, 150);
		adjustV.scale(0.6, 0.6);
		
		// stops image from being dragged
		adjustV.setLocation(390, 150);
		contents.add(adjustV);
		mainScreen.add(adjustV);
	}
	
	private void addBackButton() {
		GImage backButton = new GImage("Media/back.png", 580, 350);
		backButton.scale(2, 2);
		contents.add(backButton);
		mainScreen.add(backButton);
	}
	
	private void exitButton() {
		exit = new GImage("Media/exitButton.png", 487, 540);
		exit.scale(0.65, 0.65);
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
	
	private void addGameWarning() {
		GLabel warning = new GLabel("\u26A0  Returning to menu will restart your game!", 370, 310);
		warning.setFont("DialogInput-BOLD-13");
		warning.setColor(new Color(220, 100, 30));
		contents.add(warning);
		mainScreen.add(warning);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		GObject clicked = mainScreen.getElementAtLocation(e.getX(), e.getY());
		if (clicked == contents.get(1)) {
			mainScreen.switchBackFromDescription();
		}
		
		else if(clicked == exit) {
			System.out.println("Closing Game... Goodbye!");
	        System.exit(0);
		}
	}

}
