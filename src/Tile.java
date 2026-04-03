public class Tile extends GraphicsPane{
	public static final int SIZE = 100; //in pixels
	private int row, col;
	private double pixelX, pixelY;
	private boolean occupied;
	private ChessPiece occupant;

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
	public ChessPiece getOccupant() {return occupant;}
	public void setOccupied(boolean occupied) {this.occupied = occupied;}
	public void setOccupant(ChessPiece piece) {this.occupant = piece; this.occupied = (piece != null);}
}