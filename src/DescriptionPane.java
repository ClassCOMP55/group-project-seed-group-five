import java.awt.Color;
import java.awt.event.MouseEvent;
import acm.graphics.*;

// this is what appears when settings is clicked at the beginning
public class DescriptionPane extends GraphicsPane{
	private GImage  exit;
	private GImage  backButton;
	private GRect   sliderKnob;
	private GRect   sliderFill;
	private GLabel  prevTrackBtn;
	private GLabel  nextTrackBtn;
	private GLabel  trackNameLabel;
	private boolean fromGame = false;

	private static final int    TRACK_X = 515;  // centered in 750-wide box at x=325
	private static final int    TRACK_Y = 200;
	private static final int    TRACK_W = 370;
	private static final int    TRACK_H = 18;
	private static final int    KNOB_W  = 26;
	private static final int    KNOB_H  = 42;
	private double  currentVolume  = 0.8;
	private boolean draggingSlider = false;

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
		addTrackSelector();
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
		java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		// Full-screen dim overlay to block game content behind the window
		GRect overlay = new GRect(0, 0, screen.width, screen.height);
		overlay.setFilled(true);
		overlay.setFillColor(new Color(0, 0, 0, 160));
		overlay.setColor(new Color(0, 0, 0, 0));
		contents.add(overlay);
		mainScreen.add(overlay);
		// Windowed settings panel
		GRect box = new GRect(325, 57, 750, 750);
		box.setFilled(true);
		box.setFillColor(new Color(242, 235, 226));
		box.setColor(Color.WHITE);
		contents.add(box);
		mainScreen.add(box);
	}
	
	private void addVolumeSlider() {
		// Label
		GLabel volLabel = new GLabel("Volume", TRACK_X, TRACK_Y - 15);
		volLabel.setFont("DialogInput-BOLD-13");
		volLabel.setColor(Color.DARK_GRAY);
		contents.add(volLabel);
		mainScreen.add(volLabel);

		// Track background
		GRect track = new GRect(TRACK_X, TRACK_Y, TRACK_W, TRACK_H);
		track.setFilled(true);
		track.setFillColor(new Color(180, 180, 180));
		track.setColor(new Color(130, 130, 130));
		contents.add(track);
		mainScreen.add(track);

		// Colored fill showing current level
		sliderFill = new GRect(TRACK_X, TRACK_Y, (int)(TRACK_W * currentVolume), TRACK_H);
		sliderFill.setFilled(true);
		sliderFill.setFillColor(new Color(70, 160, 220));
		sliderFill.setColor(new Color(70, 160, 220));
		contents.add(sliderFill);
		mainScreen.add(sliderFill);

		// Draggable knob
		double knobX = TRACK_X + TRACK_W * currentVolume - KNOB_W / 2.0;
		double knobY = TRACK_Y + TRACK_H / 2.0 - KNOB_H / 2.0;
		sliderKnob = new GRect(knobX, knobY, KNOB_W, KNOB_H);
		sliderKnob.setFilled(true);
		sliderKnob.setFillColor(Color.WHITE);
		sliderKnob.setColor(new Color(100, 100, 100));
		contents.add(sliderKnob);
		mainScreen.add(sliderKnob);
	}
	
	private void addBackButton() {
		backButton = new GImage("Media/back.png", 580, 350);
		backButton.scale(2, 2);
		contents.add(backButton);
		mainScreen.add(backButton);
	}
	
	private void exitButton() {
		exit = new GImage("Media/exitButton.png", 580, 540);
		exit.scale(0.38, 0.38);
		contents.add(exit);
		mainScreen.add(exit);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (sliderKnob == null) return;
		double mx = e.getX(), my = e.getY();
		// Start drag if clicking anywhere on the track area (generous hit zone)
		if (mx >= TRACK_X && mx <= TRACK_X + TRACK_W
				&& my >= TRACK_Y - 12 && my <= TRACK_Y + TRACK_H + 12) {
			draggingSlider = true;
			updateSlider(mx);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		draggingSlider = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (draggingSlider) updateSlider(e.getX());
	}

	private void updateSlider(double mouseX) {
		if (mouseX < TRACK_X) mouseX = TRACK_X;
		if (mouseX > TRACK_X + TRACK_W) mouseX = TRACK_X + TRACK_W;
		double pct = (mouseX - TRACK_X) / TRACK_W;
		if (pct < 0.01) pct = 0.01;
		currentVolume = pct;
		sliderKnob.setLocation(mouseX - KNOB_W / 2.0, TRACK_Y + TRACK_H / 2.0 - KNOB_H / 2.0);
		sliderFill.setSize(TRACK_W * pct, TRACK_H);
		mainScreen.setVolume(currentVolume);
	}
	
	private void addTrackSelector() {
		int centerX = 700; // center of the 750-wide box
		int y       = 275;

		GLabel musicLabel = new GLabel("Music Track", 0, y - 15);
		musicLabel.setFont("DialogInput-BOLD-13");
		musicLabel.setColor(Color.DARK_GRAY);
		mainScreen.add(musicLabel);
		musicLabel.setLocation(centerX - musicLabel.getWidth() / 2.0, y - 15);
		contents.add(musicLabel);

		prevTrackBtn = new GLabel("\u25C0", 0, y + 5);
		prevTrackBtn.setFont("DialogInput-BOLD-18");
		prevTrackBtn.setColor(new Color(70, 130, 200));
		mainScreen.add(prevTrackBtn);
		prevTrackBtn.setLocation(TRACK_X, y + 5);
		contents.add(prevTrackBtn);

		nextTrackBtn = new GLabel("\u25B6", 0, y + 5);
		nextTrackBtn.setFont("DialogInput-BOLD-18");
		nextTrackBtn.setColor(new Color(70, 130, 200));
		mainScreen.add(nextTrackBtn);
		nextTrackBtn.setLocation(TRACK_X + TRACK_W - nextTrackBtn.getWidth(), y + 5);
		contents.add(nextTrackBtn);

		trackNameLabel = new GLabel(mainScreen.getCurrentTrackName(), 0, y + 5);
		trackNameLabel.setFont("DialogInput-BOLD-15");
		trackNameLabel.setColor(Color.DARK_GRAY);
		mainScreen.add(trackNameLabel);
		trackNameLabel.setLocation(centerX - trackNameLabel.getWidth() / 2.0, y + 5);
		contents.add(trackNameLabel);
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
		if (clicked == backButton) {
			mainScreen.switchBackFromDescription();
		} else if (clicked == exit) {
			System.out.println("Closing Game... Goodbye!");
			System.exit(0);
		} else if (clicked == prevTrackBtn) {
			mainScreen.changeTrack(-1);
			trackNameLabel.setLabel(mainScreen.getCurrentTrackName());
			trackNameLabel.setLocation(700 - trackNameLabel.getWidth() / 2.0, trackNameLabel.getY());
		} else if (clicked == nextTrackBtn) {
			mainScreen.changeTrack(1);
			trackNameLabel.setLabel(mainScreen.getCurrentTrackName());
			trackNameLabel.setLocation(700 - trackNameLabel.getWidth() / 2.0, trackNameLabel.getY());
		}
	}

}
