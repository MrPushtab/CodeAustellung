package main.java.app.karte;

import main.java.app.factories.DiamondSquare;
import main.java.app.factories.RNG_Factory;

public class Terrain_Karte {
	private TerrainLocation[][] locationMap;//locationMap[x][y] where locationMap[0][0] is the top left corner.
	private int w,h;
	public Terrain_Karte(int w, int h) {
		locationMap = new TerrainLocation[w][h];
		this.w = w;
		this.h = h;
	}
	public Terrain_Karte(TerrainLocation[][] locationMap, int w, int h) 
	{
		this.locationMap = locationMap;
		this.w = w;
		this.h = h;
	}
	public void readInHeightMap(short[][] hMap)
	{
		for(int i = 0; i < hMap.length; i++)
		{
			for(int j = 0; j < hMap[0].length; j++)
			{
				locationMap[i][j] = new TerrainLocation(i,j,hMap[i][j]);
			}
		}
	}

	public Location[][] getLocationMap() {
		return locationMap;
	}

}
