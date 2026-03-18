public class Tile {
	public static final int SIZE = 64; //in pixels
	private int row, col;
	private double pixelX, pixelY;
	private boolean occupied;

	public Tile(int row, int col, double pixelX, double pixelY) {
		this.row = row;
		this.col = col;
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.occupied = false;
	}

	public int getRow() {return row;}
	public int getCol() {return col;}
	public double getPixelX() {return pixelX;}
	public double getPixelY() {return pixelY;}
	public boolean isOccupied() {return occupied;}
	public void setOccupied(boolean occupied) {this.occupied = occupied;}
}