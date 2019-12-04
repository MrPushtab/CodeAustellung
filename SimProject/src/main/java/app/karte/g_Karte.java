package main.java.app.karte;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import main.java.app.karte.mapModes.MapMode;
import main.java.gEngine.model.SpriteObj;

public class g_Karte {
	private Location[][] locationMap;//locationMap[x][y] where locationMap[0][0] is the top left corner.
	private int w,h;
	private Image mapImage;
	private Terrain_Karte t_Karte;
	private int squareSize = 1;
	private SpriteObj sprite;
	public int getSquareSize() {
		return squareSize;
	}
	public int getW() {
		return w;
	}
	public int getH() {
		return h;
	}
	public void linkSprite(SpriteObj sprite)
	{
		this.sprite = sprite;
	}
	public g_Karte(int width, int height)
	{
		locationMap = new TerrainLocation[width][height];
		w=width;
		h=height;
		mapImage = new BufferedImage(squareSize*w,squareSize*h,BufferedImage.TYPE_INT_RGB);
	}
	public void genImage(MapMode m)
	{
		clearImage();
		mapImage = m.genImage(this);
		sprite.changeSprite(mapImage);
	}
	public Image getImage()
	{
		return mapImage;
	}
	public void clearImage()
	{
		mapImage = new BufferedImage(squareSize*w,squareSize*h,BufferedImage.TYPE_INT_RGB);
	}
	public void linkTerrainKarte(Terrain_Karte karte)
	{
		this.t_Karte = karte;
	}
	public Terrain_Karte getTerrainKarte()
	{
		return this.t_Karte;
	}
}
