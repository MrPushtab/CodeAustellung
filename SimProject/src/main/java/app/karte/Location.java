package main.java.app.karte;

public class Location {
	public int getxLoc() {
		return xLoc;
	}
	public void setxLoc(int xLoc) {
		this.xLoc = xLoc;
	}
	public int getyLoc() {
		return yLoc;
	}
	public void setyLoc(int yLoc) {
		this.yLoc = yLoc;
	}
	int xLoc, yLoc;
	public Location(int x, int y)
	{
		xLoc = x;
		yLoc = y;
	}
	//public abstract Image drawLocation();
}
