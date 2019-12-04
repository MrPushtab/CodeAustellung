package main.java.app.factories;

import java.util.Random;

public class DiamondSquare {
	public static Random rng = new Random();
	private int width,height;
	private int maxRNGNum;
	private float RNG_decreaseFactor;
	private int squareSideLength;
	public int[][] heigthMap;
	private preAverageArray[][] stepArrays;
	private class preAverageArray
	{
		private int[] neighborValues;
		private int neighborNum;
		public boolean set,get;
		public preAverageArray()
		{
			neighborValues = new int[4];
			neighborNum = 0;
			set = false;
			get = false;
		}
		public void addNeighborValue(int val)
		{
			neighborValues[neighborNum] = val;
			neighborNum++;
			set = true;
		}
		public int getAverage()
		{
			float total = 0;
			for(int i = 0; i < neighborNum; i++)
			{
				total += neighborValues[i];
			}
			total/=neighborNum;
			get = true;
			return (int)total;
		}
	}
	public int[][] getHeigthMap()
	{
		return this.heigthMap;
	}
	public DiamondSquare(int width, int heigth, int maxRNGNum, float RNG_decreaseFactor)
	{
		this.width = width;
		this.height = heigth;
		this.maxRNGNum = maxRNGNum;
		this.RNG_decreaseFactor = RNG_decreaseFactor;
		//algorithm only works if dimensions are (2^n)+1 * (2^n)+1, so round up'
		//later a sub-map can be taken to output the desired dimensions
		
		////Init the arrays////////
		int t_width = roundUpDimension(width);
		int t_heigth = roundUpDimension(heigth);
		int squareSideLength = Math.max(t_width, t_heigth)+1;
		this.squareSideLength = squareSideLength;
		heigthMap = new int[squareSideLength][squareSideLength];
		stepArrays = new preAverageArray[squareSideLength][squareSideLength];
		for(int i = 0; i < squareSideLength; i++)
		{
			for(int j = 0; j < squareSideLength; j++)
			{
				stepArrays[i][j] = new preAverageArray();
			}
		}
		///////DONE/////////////
		
		////Init corners////////
		heigthMap[0][0] = genInt(-this.maxRNGNum, this.maxRNGNum);
		heigthMap[0][squareSideLength-1] = genInt(-this.maxRNGNum, this.maxRNGNum);
		heigthMap[squareSideLength-1][0] = genInt(-this.maxRNGNum, this.maxRNGNum);
		heigthMap[squareSideLength-1][squareSideLength-1] = genInt(-this.maxRNGNum, this.maxRNGNum);
		stepArrays[0][0].getAverage();
		stepArrays[0][squareSideLength-1].getAverage();
		stepArrays[squareSideLength-1][0].getAverage();
		stepArrays[squareSideLength-1][squareSideLength-1].getAverage();
		////DONE/////////
		
		runDiamondSquare();
		//printArray(heigthMap);
	}
	public void runDiamondSquare()
	{
		int stepLength = (squareSideLength-1)/2;
		while(stepLength>1)
		{
			diamondStep(stepLength);
			squareStep(stepLength);
			stepLength/=2;
			this.maxRNGNum*=this.RNG_decreaseFactor;
		}
		diamondStep(stepLength);
		squareStep(stepLength);
	}
	public void diamondStep(int distance)
	{
		//loop through in order to share all current values with their neighbors
		for(int i = 0; i < squareSideLength; i++)
		{
			for(int j = 0; j < squareSideLength; j++)
			{
				if(stepArrays[i][j].get)//this means the value has been got before, thus numbers in heigthMap are correct
				{
					diamondShare(i,j, distance);
				}
			}
		}
		flipNewValues();
	}
	public void squareStep(int distance)
	{
		//loop through in order to share all current values with their neighbors
		for(int i = 0; i < squareSideLength; i++)
		{
			for(int j = 0; j < squareSideLength; j++)
			{
				if(stepArrays[i][j].get)//this means the value has been got before, thus numbers in heigthMap are correct
				{
					squareShare(i,j, distance);
				}
			}
		}
		flipNewValues();
	}
	/**
	public void diamondShare(int x, int y, int distance)
	{
			stepArrays[(x-distance)%width][(y-distance)%height].addNeighborValue(heigthMap[x][y]);
		
			stepArrays[(x+distance)%width][(y-distance)%height].addNeighborValue(heigthMap[x][y]);
		
			stepArrays[(x+distance)%width][(y+distance)%height].addNeighborValue(heigthMap[x][y]);
		
			stepArrays[(x-distance)%width][(y+distance)%height].addNeighborValue(heigthMap[x][y]);

	}
	public void squareShare(int x, int y, int distance)
	{
			stepArrays[(x-distance)%width][y].addNeighborValue(heigthMap[x][y]);
	
			stepArrays[(x+distance)%width][y].addNeighborValue(heigthMap[x][y]);
	
			stepArrays[x][(y+distance)%height].addNeighborValue(heigthMap[x][y]);
		
			stepArrays[x][(y-distance)%height].addNeighborValue(heigthMap[x][y]);

	}*/
	public void diamondShare(int x, int y, int distance)
	{
		if(x-distance >=0 && y-distance >=0)
		{
			stepArrays[x-distance][y-distance].addNeighborValue(heigthMap[x][y]);
		}
		if(x+distance <this.squareSideLength && y-distance >=0)
		{
			stepArrays[(x+distance)][y-distance].addNeighborValue(heigthMap[x][y]);
		}
		if(x+distance <this.squareSideLength && y+distance <squareSideLength)
		{
			stepArrays[x+distance][y+distance].addNeighborValue(heigthMap[x][y]);
		}
		if(x-distance >=0 && y+distance <squareSideLength)
		{
			stepArrays[x-distance][y+distance].addNeighborValue(heigthMap[x][y]);
		}
	}
	public void squareShare(int x, int y, int distance)
	{
		if(x-distance>=0)
		{
			stepArrays[x-distance][y].addNeighborValue(heigthMap[x][y]);
		}
		if(x+distance <this.squareSideLength)
		{
			stepArrays[x+distance][y].addNeighborValue(heigthMap[x][y]);
		}
		if(y+distance <squareSideLength)
		{
			stepArrays[x][y+distance].addNeighborValue(heigthMap[x][y]);
		}
		if(y-distance >=0)
		{
			stepArrays[x][y-distance].addNeighborValue(heigthMap[x][y]);
		}
	}
	public void flipNewValues()
	{
		for(int i = 0; i < squareSideLength; i++)
		{
			for(int j = 0; j < squareSideLength; j++)
			{
				if(stepArrays[i][j].set &&!stepArrays[i][j].get)//this means the value has been got before, thus numbers in heigthMap are correct
				{
					heigthMap[i][j] = stepArrays[i][j].getAverage()+genInt(-maxRNGNum,maxRNGNum);
				}
			}
		}
	}
	
	
	
	
	
	public static void printArray(int[][] arr)
	{
		for(int i = 0;i < arr.length; i++)
		{
			for(int j = 0; j < arr.length; j++)
			{
				System.out.print(arr[i][j]+"\t");
			}
			System.out.print("\n");
		}
	}
	public static int roundUpDimension(int dimension)
	{
		int temp = 1;
		while (temp<(dimension-1))
		{
			temp*=2;
		}
		return temp;
	}
	public static int genInt(int min, int max)
	{
		try
		{
			return rng.nextInt(max-min)+min;
		}
		catch(Exception e)
		{
			return 0;
		}
	}
	/**public static void main(String[] args)
	{
		DiamondSquare myO = new DiamondSquare(17,17,100,.5f);
	}*/
}
