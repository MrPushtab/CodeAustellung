package main.java.gEngine.model;

import java.util.Observable;

public class SpriteHolder extends Observable{
	
	public void notifyAll(Object param)
	{
		setChanged();
		notifyObservers(param);
	}

}
