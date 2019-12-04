package main.java.gEngine.model;

import java.awt.Image;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import java.awt.Graphics2D;

public class SpriteObj implements Observer{
	private int xPos, yPos;
	private Image sprite;
	//private JPanel panel;
	public SpriteObj(int x, int y, Image sprite)
	{
		this.xPos=x;
		this.yPos=y;
		this.sprite=sprite;
	}
	/**public void addPanel(JPanel panel)
	{
		this.panel = panel;
	}*/
	public void changeSprite(Image img)
	{
		this.sprite = img;
	}
	public void update(Observable o, Object g)
	{
		((Graphics2D)(g)).drawImage(sprite, xPos, yPos, null);
	}
}
