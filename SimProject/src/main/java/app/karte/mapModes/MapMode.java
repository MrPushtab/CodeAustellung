package main.java.app.karte.mapModes;

import java.awt.Image;

import main.java.app.karte.Location;
import main.java.app.karte.g_Karte;

public interface MapMode {
	public Image genImage(g_Karte karte);
	public Image genLocationSprite(Location loc);
}
