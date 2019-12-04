package main.java.app.karte.worldgen;

import java.util.ArrayDeque;

import main.java.app.factories.DiamondSquare;
import main.java.app.factories.RNG_Factory;
import main.java.app.karte.Location;
import main.java.app.karte.TerrainLocation;

public class TerrainGen_Ontogenetic {
	private short[][] heightMap;//heightMap[x][y] where heightMap[0][0] is the top left corner.
	private int w,h;
	private short fillLakesArr[][];
	private ArrayDeque<Location> fillQ;
	public TerrainGen_Ontogenetic(int w, int h) 
	{
		this.w = w;
		this.h = h;
		heightMap = new short[w][h];
	}
	
	//uses DiamondSquare to fill heigthmap
	public void doDiamondSquareTerrain()
	{
		DiamondSquare myO = new DiamondSquare(w,h,80,.85f);
		heightMap = new short[w][h];
		for(int i = 0; i < w; i++)
		{
			for(int j = 0; j < h; j++)
			{
				heightMap[i][j] = (short) myO.getHeigthMap()[i][j];
			}
		}
	}
	
	//just fills in purely random heigths
	public void fillInRandomTerrainLocs()
	{
		for(int i = 0; i < w; i++)
		{
			for(int j = 0; j < h; j++)
			{
				heightMap[i][j] = (short) RNG_Factory.genInt(-1000, 1000);
			}
		}
	}
	
	//all terrain below the value 'sink' in the heigthmap is lowered by 10
	public void sinkLowTerrain(int sink)
	{
		for(int i = 0; i < w; i++)
		{
			for(int j = 0; j < h; j++)
			{
				if(heightMap[i][j]<sink)
				{
					heightMap[i][j] = (short) (heightMap[i][j]-10);
				}
			}
		}
	}
	
	//getter for heightmap
	public short[][] getheightMap() {
		return heightMap;
	}	
	
	//This function serves to eliminate small lakes
	public void fillMostLakes(float vanishFactor)
	{
		int tt = 0;
		int total = 0;
		fillQ = new ArrayDeque<Location>();
		fillLakesArr = new short[w][h];
		for(int i = 0; i < w; i++)
		{
			for(int j = 0; j < h; j++)
			{
				if(fillLakesArr[i][j]==0)//this means not encountered
				{
					if(isWater(i,j))//is water
					{
						total = 0;
						considerWater(new Location(i,j));
						while(!fillQ.isEmpty())
						{
							Location loc = fillQ.poll();
							considerWater(loc);
							if(fillLakesArr[loc.getxLoc()][loc.getyLoc()]==1)
							{
								total++;
								tt++;
							}
							if(total >10000)
							{
								fillQ.clear();
								flipWater(loc);
								while(!fillQ.isEmpty())
								{
									loc = fillQ.remove();
									flipWater(loc);
								}
							}
						}
					}
				}
			}
		}
		for(int i = 0; i < w; i++)
		{
			for(int j = 0; j < h; j++)
			{
				if(fillLakesArr[i][j]==1)
				{
					heightMap[i][j]=2;
				}
			}
		}
		System.out.println(tt);
	}
	private void flipWater(Location location)
	{
		int x = location.getxLoc();
		int y = location.getyLoc();
		if(!isWater(location.getxLoc(),location.getyLoc()))
		{
			fillLakesArr[location.getxLoc()][location.getyLoc()]=-1;
			return;
		}
		fillLakesArr[location.getxLoc()][location.getyLoc()]=-1;
		try
		{
			if(fillLakesArr[x-1][y]==1)
			{
				fillQ.add(new Location(x-1,y));
			}
		}
		catch(Exception e){};
		try
		{
			if(fillLakesArr[x+1][y]==1)
			{
				fillQ.add(new Location(x+1,y));
			}
		}
		catch(Exception e){};
		try
		{
			if(fillLakesArr[x][y-1]==1)
			{
				fillQ.add(new Location(x,y-1));
			}
		}
		catch(Exception e){};
		try
		{
			if(fillLakesArr[x][y+1]==1)
			{
				fillQ.add(new Location(x,y+1));
			}
		}
		catch(Exception e){};
	}
	private void considerWater(Location location) {
		int x = location.getxLoc();
		int y = location.getyLoc();
		if(!isWater(location.getxLoc(),location.getyLoc()))
		{
			fillLakesArr[location.getxLoc()][location.getyLoc()]=-1;
			return;
		}
		fillLakesArr[location.getxLoc()][location.getyLoc()]=1;
		try
		{
			if(fillLakesArr[x-1][y]==0)
			{
				fillQ.add(new Location(x-1,y));
			}
		}
		catch(Exception e){};
		try
		{
			if(fillLakesArr[x+1][y]==0)
			{
				fillQ.add(new Location(x+1,y));
			}
		}
		catch(Exception e){};
		try
		{
			if(fillLakesArr[x][y-1]==0)
			{
				fillQ.add(new Location(x,y-1));
			}
		}
		catch(Exception e){};
		try
		{
			if(fillLakesArr[x][y+1]==0)
			{
				fillQ.add(new Location(x,y+1));
			}
		}
		catch(Exception e){};
		
	}

	public boolean isWater(int x, int y)
	{
		if(heightMap[x][y] < 0)
		{
			return true;
		}
		return false;
	}
	public void raiseTerrain(int raise)
	{
		for(int i = 0; i < w; i++)
		{
			for(int j = 0; j < h; j++)
			{
				heightMap[i][j] = (short) (heightMap[i][j]+raise);
			}
		}
	}
	public void makePercentageWater(float factor)
	{
		
		int total = 0;
		for(int i = 0; i < w; i++)
		{
			for(int j = 0; j < h; j++)
			{
				if(heightMap[i][j] < 0)
				{
					total++;
				}
			}
		}
		if(((float)total/(float)(w*h)) >factor+.02)
		{
			raiseTerrain(2);
		}
		else if(((float)total/(float)(w*h)) < factor-.02)
		{
			raiseTerrain(-2);
		}
		else
		{
			return;
		}
		makePercentageWater(factor);
	}
	public void printWaterTotal()
	{
		int total = 0;
		for(int i = 0; i < w; i++)
		{
			for(int j = 0; j < h; j++)
			{
				if(heightMap[i][j] < 0)
				{
					total++;
				}
			}
		}
		System.out.println(total);
	}
	public void hardLandSmooth()
	{
		for(int i = 0; i < w; i++)
		{
			for(int j = 0; j < h; j++)
			{
				heightMap[i][j]/=2;
			}
		}
	}
	public void smoothTerrain_landSmooth(int adjacents_Weight, int center_Weight)
	{
		int total = 0;
		int adj = 0;
		for(int i = 0; i < w; i++)
		{
			for(int j = 0; j < h; j++)
			{
				if(heightMap[i][j]<0)
				{
					continue;
				}
				total = 0;
				adj = 0;
				//for every location, take (surrounding_locations)*(adjacent_Weight) + (center_location)*(center_Weight)/all_Weight
				try
				{
					total += heightMap[i-1][j-1];
					adj++;
				}
				catch(Exception e){};
				try
				{
					total += heightMap[i-1][j];
					adj++;
				}
				catch(Exception e){};
				try
				{
					total += heightMap[i-1][j+1];
					adj++;
				}
				catch(Exception e){};
				try
				{
					total += heightMap[i][j-1];
					adj++;
				}
				catch(Exception e){};
				try
				{
					total += heightMap[i][j+1];
					adj++;
				}
				catch(Exception e){};
				try
				{
					total += heightMap[i+1][j+1];
					adj++;
				}
				catch(Exception e){};
				try
				{
					total += heightMap[i+1][j];
					adj++;
				}
				catch(Exception e){};
				try
				{
					total += heightMap[i+1][j-1];
					adj++;
				}
				catch(Exception e){};
				total*=adjacents_Weight;
				total+= heightMap[i][j]*center_Weight;
				heightMap[i][j] = (short) (total/(center_Weight+(adjacents_Weight*adj)));
			}
		}
	}
	public void smoothTerrain_fullSmooth(int adjacents_Weight, int center_Weight)
	{
		short[][] terrainlocs = new short[w][h];
		int total = 0;
		int adj = 0;
		for(int i = 0; i < w; i++)
		{
			for(int j = 0; j < h; j++)
			{
				total = 0;
				adj = 0;
				//for every location, take (surrounding_locations)*(adjacent_Weight) + (center_location)*(center_Weight)/all_Weight
				try
				{
					total += heightMap[i-1][j-1];
					adj++;
				}
				catch(Exception e){};
				try
				{
					total += heightMap[i-1][j];
					adj++;
				}
				catch(Exception e){};
				try
				{
					total += heightMap[i-1][j+1];
					adj++;
				}
				catch(Exception e){};
				try
				{
					total += heightMap[i][j-1];
					adj++;
				}
				catch(Exception e){};
				try
				{
					total += heightMap[i][j+1];
					adj++;
				}
				catch(Exception e){};
				try
				{
					total += heightMap[i+1][j+1];
					adj++;
				}
				catch(Exception e){};
				try
				{
					total += heightMap[i+1][j];
					adj++;
				}
				catch(Exception e){};
				try
				{
					total += heightMap[i+1][j-1];
					adj++;
				}
				catch(Exception e){};
				total*=adjacents_Weight;
				total+= heightMap[i][j]*center_Weight;
				terrainlocs[i][j] = (short) (total/(center_Weight+(adjacents_Weight*adj)));
			}
		}
		for(int i = 0; i < w; i++)
		{
			for(int j = 0; j < h; j++)
			{
				heightMap[i][j] = terrainlocs[i][j];
			}
		}
	}
}
