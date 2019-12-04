package main.java.gEngine.model;


import java.awt.event.ActionListener;

import javax.swing.Timer;

public class ModelTimer{
	public int maxFPS;
	public int minDelayBetweenFrames;
	private Timer timer;
	public ModelTimer(int maxFPS)
	{
		this.maxFPS = maxFPS;
		minDelayBetweenFrames=(int)(((float)(1000/maxFPS))-0.5);
	}
	public void createTimer(ActionListener e)
	{
		timer = new Timer(minDelayBetweenFrames,e);
	}
	public Timer getTimer()
	{
		return timer;
	}
	
}
