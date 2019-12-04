package main.java.app.karte;

import java.awt.Color;
import java.awt.Image;

import main.java.app.factories.Image_Factory;

public class TerrainLocation extends Location{

	private int t_height;
	public TerrainLocation(int x, int y, int terrain_height)
	{
		super(x,y);
		t_height=terrain_height;
	}
	/**@Override
	public Image drawLocation() {
		if(t_height<-50)
		{
			return Image_Factory.coloredRect(8, 8, Color.red);
		}
		if(t_height<-20)
		{
			return Image_Factory.coloredRect(8, 8, Color.pink);
		}
		if(t_height<0)
		{
			return Image_Factory.coloredRect(8, 8, Color.white);
		}
		if(t_height<20)
		{
			return Image_Factory.coloredRect(8, 8, Color.green);
		}
		if(t_height<50)
		{
			return Image_Factory.coloredRect(8, 8, Color.cyan);
		}
		return Image_Factory.coloredRect(8, 8, Color.blue);
	}*/
	public int getT_height() {
		return t_height;
	}

}
