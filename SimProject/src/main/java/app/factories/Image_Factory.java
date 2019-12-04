package main.java.app.factories;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Image_Factory {
	public static Image coloredRect(int height, int width, Color color)
	{
		Image img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(color);
		g.fillRect(0, 0, width, height);
		return img;
	}
}
