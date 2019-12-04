package main.java.app.karte.mapModes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import main.java.app.factories.Image_Factory;
import main.java.app.karte.Location;
import main.java.app.karte.TerrainLocation;
import main.java.app.karte.g_Karte;

public class HeightMode implements MapMode{
	private Color[] colors;
	private int colorsNeeded = 600;
	private int squareSize;

	public Image genImage(g_Karte karte) 
	{
		colors = new Color[600];
		this.fillColors();
		int w = karte.getW();
		int h = karte.getH();
		squareSize = karte.getSquareSize();
		//karte.clearImage();
		Image image = karte.getImage();
		Graphics g = image.getGraphics();
		for(int i = 0; i < w; i++)
		{
			for(int j = 0; j < h; j++)
			{
				g.drawImage(genLocationSprite(karte.getTerrainKarte().getLocationMap()[i][j]), squareSize*i, squareSize*j, null);
			}
		}
		return image;
	}
	
	private void fillColors()
	{
		for(int i = 0; i < 100; i++)//lowland
		{
			colors[300+i] = new Color(i,50+i,i);
		}
		for(int i = 0; i < 50; i++) //hill
		{
			colors[400+i] = new Color(100+(int)(i*1.5),150,100);
		}
		for(int i = 0; i < 50; i++) //mountain
		{
			colors[450+i] = new Color(200,150+i,100+i*2);
		}
		for(int i = 0; i < 200; i++)//sea
		{
			colors[100+i] = new Color((int)(i*.5),(int)(i*.5),80+(int)(i*.5));
		}
		/**for(int i = 0; i < 100; i++)//
		{
			colors[199-i] = new Color(50-(int)(i*.5),50-(int)(i*.5),90-(int)(i*.9));
		}*/
	}

	
	public Image genLocationSprite(Location loc) {
		int t_height = ((TerrainLocation)loc).getT_height();
		try
		{
			return Image_Factory.coloredRect(squareSize, squareSize, colors[300+t_height]);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			return Image_Factory.coloredRect(squareSize, squareSize, Color.WHITE);
		}
	}
	
}
