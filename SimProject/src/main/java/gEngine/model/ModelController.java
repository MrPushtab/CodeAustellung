package main.java.gEngine.model;

import main.java.gEngine.adapters.Adpt_Model2View;

public class ModelController {
	private ModelTimer timer;
	private SpriteHolder spriteholder;
	private Adpt_Model2View model2viewAdapter;
	
	public ModelController(int maxFPS)
	{
		timer = new ModelTimer(maxFPS);
		spriteholder = new SpriteHolder();
		timer = new ModelTimer(maxFPS);
	}
	public void addSprite(SpriteObj spr)
	{
		spriteholder.addObserver(spr);
	}
	public ModelTimer getTimer()
	{
		return timer;
	}
	public SpriteHolder getSpriteholder()
	{
		return spriteholder;
	}
	
}
