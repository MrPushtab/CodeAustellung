//Tobias Kroll 2016
//the purpose of this program is to implement the smart UnionFind algorithm, with a secondary purpose being to create a 3D maze adjacency List with edge weights.
import java.util.Random;
import java.util.Scanner;

public class TOKRP3
{
	class UnionFind {
		int[] sets;//sets keeps track of the sets via the array structure discussed in the noes.\
		float totalPathCounts;//the stats variables
		float totalPathLengths;
		UnionFind(int N) // create a union find class for integer elements 0 ... N-1
		{
			sets = new int[N];
			clear();
		}
		/**Part of the attempt to reverse certain edges in the maze in order to create viable mazes when given 'skinny parameters'.Explained more below
		void reverseUnion(int x, int y)
		{
			if(isRoot(x))
			{
				int temp = sets[x];
				sets[x] = y;
				sets[y] = temp;
			}
			else if(isRoot(y))
			{
				int temp = sets[y];
				sets[y] = x;
				sets[x] = temp;
			}
		}*/
		//simple union that makes y subordinate to x. Used for the maze. uses simple_find
		boolean simple_union(int x, int y)
		{
			//addPathLengthToStats(x);
			int xRoot = simple_find(x);
			//addPathLengthToStats(y);
			int yRoot = simple_find(y);
			
			if(xRoot==yRoot)
			{
				return false;
			}
			sets[x] = y;
			
			return true;
		}
		// Form the union of sets containing values x and y using
		// the "union by size" strategy. If the two sets are in the same
		// UnionFind tree, return false. Return true otherwise. If the two
		// sets are the same size, the root of the set containing y must
		// become a child of the root of the set containing x. Otherwise
		// implement "union by size".
		boolean union(int x, int y)
		{
			addPathLengthToStats(x);
			int xRoot = find(x);
			addPathLengthToStats(y);
			int yRoot = find(y);
			if(xRoot==yRoot)
			{
				return false;
			}
			if(sets[yRoot]<sets[xRoot])
			{
				sets[yRoot]+=sets[xRoot];
				sets[xRoot] = yRoot;
				//sets[yRoot]+=sets[xRoot];
			}
			else
			{
				sets[xRoot]+=sets[yRoot];
				sets[yRoot] = xRoot;
				//sets[xRoot]+=sets[yRoot];
			}
			return true;
		}
		//an implementation of find without path compression, in order to generate the statistics. Does not actually return the resulting root
		void addPathLengthToStats(int y)
		{
			int count = 0;
			int temp = y;
			while(sets[temp]>-1)
			{
				temp=sets[temp];
				count++;
			}
			totalPathLengths+= count;
			totalPathCounts++;
		}
		// Search for element y and return the root index of the
		// tree containing y. No path compression. Used for the maze.
		int simple_find(int y)
		{
			int temp = y;
			while(sets[temp]>-1)
			{
				temp=sets[temp];
			}
			return temp;
		}
		// Search for element y and return the root index of the
		// tree containing y. Implement path compression on each find,
		// including the two calls made by union()
		int find(int y)
		{
			int count = 0;
			int temp = y;
			while(sets[temp]>-1)
			{
				temp=sets[temp];
				count++;
			}
			if(sets[y]>-1)
			{
				sets[y]=find(sets[y]);
			}
			//I instead counted path length and counts separately: union calls addPathLengthToStats, and parseInput calls addPathLengthToStats on a find input
			//totalPathCounts++;
			//totalPathLengths+=count;
			return temp;
		}
		// Return the number of disjoint sets
		int sets()
		{
			int total = 0;
			for(int i = 0; i < sets.length;i++)
			{
				if(sets[i] <= -1)
				{
					total++;
				}
			}
			return total;
		}
		// Make all sets disjoint.
		void clear() 
		{
			for(int i = 0; i < sets.length; i++)
			{
				sets[i] = -1;
			}
		}
		// Print the contents of the UnionFind array, 20 space-separated
		// integers per line
		void printArray() 
		{
			for(int i = 0; i < sets.length; i++)
			{
				System.out.print(sets[i]+" ");
				if((i+1)%20==0)
				{
					System.out.print("\n");
				}
			}
			System.out.print("\n");
		}
		//just prints out average by diving the taken stats
		float pathLengthAverage()
		{
			return totalPathLengths/totalPathCounts;
		}
		// Print out sets() as well as heightAverage()
		void printStats()
		{
			System.out.print("Number of disjoint sets remaining = " + sets() + "\nMean path length of all find operations = ");
			System.out.printf("%.2f", pathLengthAverage());
		}
		//prints the connectivity list. runs through sets[] once. If it is found that an element is not already belonging to an already printed set, it and all its set's members are printed
		void printConnectivityList()
		{
			boolean isPrinted = false;
			for(int i = 0; i < sets.length; i++)
			{
				isPrinted = false;
				for(int j = 0; j < i; j++)
				{
					if(simple_find(j)==simple_find(i))
					{
						isPrinted = true;
					}
				}
				if(isPrinted)
				{
					continue;
				}
				//System.out.print(i + " ");
				for(int j = i; j < sets.length; j++)
				{
					if(simple_find(j)==simple_find(i))
					{
						System.out.print(j + " ");
					}
				}
				System.out.print("\n");
			}
		}
		//prints the adjacency list. Here the edge=weights are generated
		void printAdjacencyList(int w, int d, int h)
		{
			Random gen = new Random();
			String s = "";
			int count = 0;
			for(int i = 0; i < sets.length; i++)
			{
				count = 0; //keeps track of how many neighbors were found
				s+=i;
				if(haveEdge(i, i+1))//eastern edge
				{
					count++;
					s+= " " + (i+1);
				}
				if(haveEdge(i, i+w))//southern edge
				{
					count++;
					s+= " " + (i+w);
				}
				if(haveEdge(i, i+(w*d)))//downward edge
				{
					count++;
					s+= " " + (i+(w*d));
				}
				if(count==0)
				{
					String temp = i +"";
					s=s.substring(0, s.length()-temp.length());
					continue;
				}
				for(int j = 0; j < count; j++)
				{
					s+=" "+(gen.nextInt(20)+1);
				}
				s+="\n";
			}
			System.out.print(s);
		}
		//returns true if either index connects to the other. Useful for the maze creation process.
		boolean haveEdge(int index1, int index2)
		{
			if(index1>=sets.length||index2>=sets.length)
			{
				return false;
			}
			if(sets[index1]==index2 || sets[index2]==index1)
			{
				return true;
			}
			return false;
		}
		//returns true if the given index is a root index
		boolean isRoot(int index)
		{
			if(sets[index]<0)
			{
				return true;
			}
			return false;
		}
		//prints out each horizontal layer by itself. Useful for manual parsing of output
		void printOutByLayer(int w, int d)
		{
			for(int i = 0; i < sets.length; i++)
			{
				System.out.print(sets[i]+" ");
				if((i+1)%w==0)
				{
					System.out.print("\n");
				}
				if((i+1)%(w*d)==0)
				{
					System.out.print("\n");
				}
			}
			System.out.print("\n");
		}
	}
	//this method parses input. The reason it's passed into the enclosing class is to reference the UnionFind class
	public void parseInput(Scanner cin)
	{
		UnionFind myO = new UnionFind(cin.nextInt());
		while(true)
		{
			//myO.printArray();
			String s = cin.next();
			char a = s.charAt(0);
			switch(a)
			{
				case 'u':
					System.out.print(myO.union(cin.nextInt(),cin.nextInt())+"\n");
					break;
				case 'f':
					int temp = cin.nextInt();
					myO.addPathLengthToStats(temp);
					System.out.print(myO.find(temp)+"\n");
					break;
				case 'p':
					myO.printArray();
					break;
				case 'c':
					myO.printConnectivityList();
					break;
				case 'S':
					myO.printStats();
					break;
				case 'm':
					int w=cin.nextInt();
					int d=cin.nextInt();
					int h=cin.nextInt();
					while(!printMaze(w,d,h))
					{
							printMaze(w,d,h);
					}
					break;
				case 'e':
					return;
			}
		}
	}
	//prints the maze by generating random edges until each vertices is connected to another except one
	//then prints the connectivity list
	public boolean printMaze(int w, int d, int h)
	{
		UnionFind maze = new UnionFind(w*d*h);
		Random gen = new Random();
		int edgeCount = 0;
		int possibleRejectCounter = 0;  //see below for explanation
		while(edgeCount<(w*d*h-1))
		{
			int index = gen.nextInt(w*d*h);
			int edge = gen.nextInt(6);
			if(!maze.isRoot(index))
			{
				continue;
			}
			/* Interestingly enough, occasionally the maze will generate two disjoint mazes, for each of which the root becomes stuck without any possible edges (as they would create
			 * cycles). This results in there being less than (w*d*h-1) possible edges, and thus an invalid maze. Because of this there needs to be some way to either
			 * reject the mazes where this occurs, or to have a protocol that reverses some of the edges in a submaze.
			 * Since part of the concept of UnionFind is that once sets are joined they cannot be unjoined, and sets cannot be reversed, I will take the rejection approach. This potentially
			 * compromises the 'uniform' (as in each valid outcome being equally likely, since some mazes lose half of their probability without reversing sets)
			 * randomness of the maze generation however. That said, this rejection process seems to have rather little effect on 'normal' mazes, 
			 * and a large effect on skinny mazes. (2^1024 mazes to be generated on average!!)
			 * 
			 * Some sort of reversing method should work, as it would move the roots of the disjoint sets around until one had neighbors of a different set. I can't seem to
			 * make this work however. Also, due to the reasons above it seems to contradict the spirit of the algorithm.
			 */ 
			if(possibleRejectCounter%20==19)
			{
				//if the maze seems to be broken, check that it is and reject it. Catching the StackOverflowError is only useful for semi-fat mazes. Skinny mazes will never return.
				if(isMazeStuck(w,d,h,maze))
				{
					try
					{
						return printMaze(w,d,h);
					}
					catch (StackOverflowError e)
					{
						return false;
					}
				}
				/** this was the attempt to use a reverse function to 'fix' non-viable mazes
				int temp = -1;
				while(isMazeStuck(w,d,h,maze))
				{
					temp = reverseMaze(w,d,h,maze,temp);
					//maze.printOutByLayer(w, d);
				}
				*/
			}
			if(doesEdgeExist(w,d,h,index,edge))
			{
				int temp = calcEdgeIndex(w,d,h,index,edge);
				//this won't union if both indeces are already connected to the same sub-maze/are in the same set,
				//as this would either be a redundant edge or a cycle edge
				if(maze.simple_union(index,temp))
				{
					edgeCount++;
					possibleRejectCounter = 0;
				}
				else
				{
					possibleRejectCounter++;
				}
			}
		}
		
		maze.printAdjacencyList(w,d,h);
		return true;
		//maze.printOutByLayer(w, d);
	}
	//returns true if there are no viable edges to be placed anymore. Occurs when there are two (or more) disjoint sets, but the root of each is surrounded by neighbors of its own set.
	//Used in the rejection process.
	public boolean isMazeStuck(int w, int d, int h, UnionFind maze)
	{
		for(int i = 0; i < w*d*h; i++)
		{
			if(maze.find(i)==-1)
			{
				for(int j = 0; j < 6; j ++)
				{
					if(doesEdgeExist(w, d, h, i, j))
					{
						if(maze.simple_union(i, calcEdgeIndex(w,d,h,i,j)))
						{
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	/**
	//!!Assumption: all roots are stuck!!
	 * This was an attempt at implementing the second choice of making viable mazes: reversing certain set connections until a valid edge can be generated.
	public int reverseMaze(int w, int d, int h, UnionFind maze, int lastReversedEdge)
	{
		Random gen = new Random();
		boolean indexIsStuck = true;
		int temp = -1;
		int i = 0;
		while(!maze.isRoot(i))
		{
			i=gen.nextInt(w*d*h);
		}
		while(indexIsStuck)
		{
			System.out.println("damn");
				while(indexIsStuck)
				{
					temp = gen.nextInt(6);
					switch (temp)
					{
						case 0:
							if(lastReversedEdge==2)
								continue;
						case 1:
							if(lastReversedEdge==3)
								continue;
						case 2:
							if(lastReversedEdge==0)
								continue;
						case 3:
							if(lastReversedEdge==1)
								continue;
						case 4:
							if(lastReversedEdge==5)
								continue;
						case 5:
							if(lastReversedEdge==4)
								continue;
					}
					if(doesEdgeExist(w,d,h,i,temp) && !maze.simple_union(i, calcEdgeIndex(w,d,h,i,temp)))
					{
						maze.reverseUnion(i,calcEdgeIndex(w,d,h,i,temp));
						indexIsStuck=false;
					}
				}
				if(isMazeStuck(w,d,h,maze))
				{
					indexIsStuck=true;
					i=calcEdgeIndex(w,d,h,i,temp);
				}
		}
		return temp;
	}*/
	//edge == [0,5] == N,E,S,W,U,D
			//method determines whether the given index can have the corresponding neighbor attached to parameter edge:
	//makes sure there are no edges 'poking out of the cube'
	public boolean doesEdgeExist(int w, int d, int h, int index, int edge)
	{
		switch (edge)
		{
			case 0: //Northern edge
				//the first row of each horizontal plane (elements w*d*n) has no northern edge
				if((index%(w*d))>=0 && (index%(w*d))<w)
				{
					return false;
				}
				break;
			case 1: //Eastern edge
				//the last element of each row has no eastern plane
				if((index+1)%w==0)
				{
					return false;
				}
				break;
			case 2://Southern edge
				//Same as north, except the last row per each horizontal plane
				if((index%(w*d))>=(w*d-w) && (index%(w*d))<w*d)
				{
					return false;
				}
				break;		
			case 3://Western edge
				//the first element of each row has no western plane
				if((index)%w==0)
				{
					return false;
				}
				break;
			case 4://Upward edge
				//the top face has no upward edges; [0,wd)
				if(index>=0 && index<w*d)
				{
					return false;
				}
				break;
			case 5:
				//downward edge
				//the last horizontal plane [wdh-wd,wdh) has no downward edge
				if(index>=((w*d*h)-(w*d)) && index<w*d*h)
				{
					return false;
				}
		}
		return true;
	}
	//returns the index that the parameter edge would point to given parameter index.
	//!!Assumption: the edge exists!!
	public int calcEdgeIndex(int w,int d,int h,int index, int edge)
	{
		switch (edge)
		{
			case 0: //Northern Edge
				//move up one row
				return index-w;
			case 1://Eastern edge
				//move right one index
				return index+1;
			case 2://Southern edge
				//down a row
				return index+w;
			case 3://Western edge
				//left one index
				return index-1;
			case 4://Upward edge
				//up one plane
				return index-(w*d);
			case 5://Downward edge
				//down a plane
				return index+(w*d);
		}
		return -1;
	}
	/**debug function
	public void debugUnionFind()
	{
		UnionFind myO = new UnionFind(10);
		myO.union(0, 1);
		myO.union(2, 3);
		myO.union(4, 5);
		myO.union(6, 7);
		myO.union(8, 9);
		myO.union(7, 8);
		myO.find(9);
		myO.printArray();
		myO.printStats();
	}*/
	public static void main(String[] args)
	{
		TOKRP3 myO = new TOKRP3();
		//myO.debugUnionFind();
		Scanner cin = new Scanner(System.in);
		int count = 0;
		myO.parseInput(cin);

	}
}
