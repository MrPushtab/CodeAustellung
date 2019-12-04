import java.util.Scanner;

public class TOKRP4 {
	//just a simple construct that stores a radio's location
	class Radio
	{
		public float x, y;
		public Radio(float x2, float y2)
		{
			this.x=x2;
			this.y=y2;
		}
	}
	//To implement Kruskal, a UnionFind object is needed. I am largely using my work from Project 3 in order to implement it.
	class UnionFind {
		int[] sets;//sets keeps track of the sets via the array structure discussed in the notes.
		UnionFind(int N) // create a union find class for integer elements 0 ... N-1
		{
			sets = new int[N];
			clear();
		}
		// Form the union of sets containing values x and y using
		// the "union by size" strategy. If the two sets are in the same
		// UnionFind tree, return false. Return true otherwise. If the two
		// sets are the same size, the root of the set containing y must
		// become a child of the root of the set containing x. Otherwise
		// implement "union by size".
		boolean union(int x, int y)
		{
			int xRoot = find(x);
			int yRoot = find(y);
			if(xRoot==yRoot)
			{
				return false;
			}
			if(sets[yRoot]<sets[xRoot])
			{
				sets[yRoot]+=sets[xRoot];
				sets[xRoot] = yRoot;
			}
			else
			{
				sets[xRoot]+=sets[yRoot];
				sets[yRoot] = xRoot;
			}
			return true;
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
		// Make all sets disjoint.
		void clear() 
		{
			for(int i = 0; i < sets.length; i++)
			{
				sets[i] = -1;
			}
		}
	}
	public float[][] adjacencyMatrix;
	public Radio[] radios;
	
	//method uses simple euclidian distance formula
	public float distance(float xi, float yi,float xj, float yj)
	{
		return (float) Math.sqrt(((xi-xj)*(xi-xj))+((yi-yj)*(yi-yj)));
	}
	public TOKRP4(int N)
	{
		radios = new Radio[N];
		adjacencyMatrix=new float[N][N];
	}
	//The simplest way to get all the adjacency values is to just traverse the list N*N times
	//(although you could actually gen half the matrix and flip it)
	public void genAdjacencyMatrix()
	{
		int N = radios.length;
		for(int i = 0; i < N; i++)
		{
			for(int j = 0; j < N; j++)
			{
				
				adjacencyMatrix[i][j] = distance(radios[i].x,radios[i].y,radios[j].x,radios[j].y);
			}
		}
	}
	//makes all edges that are greater than Rl 40001, larger than a full diagonal at max width and heigth
	public void increaseUnreachableAdjValues(float R)
	{
		int N = radios.length;
		for(int i = 0; i < N; i++)
		{
			for(int j = 0; j < N; j++)
			{
				
				if(adjacencyMatrix[i][j]>R) adjacencyMatrix[i][j]=40001f;
			}
		}
	}	
	//prints the adjacencyMatrix. a debug function
	public void printAdjMatrix()
	{
		int N = radios.length;
		System.out.print("\t");
		for(int i = 0; i < N; i++)
		{
			System.out.print(i+"\t");
		}
		for(int i = 0; i < N; i++)
		{
			System.out.print("\n");
			System.out.print(i+"\t");
			for(int j = 0; j < N; j++)
			{
				System.out.printf("%.1f", adjacencyMatrix[i][j]);
				System.out.print("\t");
			}
		}
	}
	//this method adds a Radio Location into radios[radioNum]
	public void addRadio(float x, float y, int radioNum)
	{
		radios[radioNum] = new Radio(x,y);
	}
	//implementation of kruskals algorithm
	//uses unionFind as described above.
	//Prints the MST to console
	public void kruskal()
	{
		float totalTreeWeight=0;
		int shortRadio1=0, shortRadio2=0;
		float shortPath=0f;
		int N = radios.length;
		UnionFind unionFind = new UnionFind(N);
		for(int e = 0; e<N-1;e++)
		{
			shortPath=40001f;
			for(int i = 0; i < N; i++)
			{
				for(int j = i+1; j < N; j++)
				{
					if(adjacencyMatrix[i][j]<shortPath)
					{
						if(unionFind.find(i)!=unionFind.find(j))
						{
							shortPath=adjacencyMatrix[i][j];
							shortRadio1=i;
							shortRadio2=j;
						}
					}
				}
			}
			
			//after all edges have been considered, print the resultant edge and add it to UnionFind
			unionFind.union(shortRadio1,shortRadio2);
			totalTreeWeight+=shortPath;
			System.out.print((shortRadio1+1)+ " " + (shortRadio2+1) +" "); //The "+1"s are since the radios start indexing at 1 in the problem
			System.out.printf("%.2f", shortPath);
			System.out.print("\n");
		}
		System.out.printf("%.2f",totalTreeWeight);
		System.out.print("\n");
	}
	
	//This method is going to run Djiksta's shortest path algorithm throughout the graph represented by the 0/1 adjacency matrix
	//it will then also print out the results.
	//I will however be only using an array, and not a priority queue, so potentially V^2 (even for 1000 dense vertices this ran with plenty of time)
	public void djikstra()
	{
		int N = radios.length;
		int[][] djikstraData = new int[3][N]; //Since I'll be using an array, this 2d array will hold the data as described in the note:
		//data[v][0] == true/false if v is in S
		//data[v][1] == v.d
		//data[v][2] == v.pi ; the last adjacent vertex on the path with distance v.d
		int traversedDistance = 0;
		//since I'm not using a priority queue, I will have to add the first (0th) vertex manually and work from there
		djikstraData[0][0] = 1;
		djikstraData[1][0] = 0;
		djikstraData[2][0] = 0;
		for(int i = 1; i < N; i++)
		{
			djikstraData[1][i] = 40001;
		}
		int next = 0;
		//System.out.println("debuggerino");
		//this for loop is the ''for each vertex v in G.Adj[u]'' loop
		for(int j = 0; j < N; j++)
		{
			if(djikstraData[0][j]==0f) //not in S already
			{
				if(adjacencyMatrix[next][j]==1) //in Range of next (i.e. a candidate for RELAXation
				{
					//RELAX
					if(djikstraData[1][j]>( djikstraData[1][next]+1 ))//if new path shorter than old path
					{
						djikstraData[1][j] = djikstraData[1][next]+1;
						djikstraData[2][j] = next;
					}
					/////////
				}
			}
		}
		//After adding 0 manually, the algorithm finishes the rest
		for(int i = 0; i < N-1; i++)
		{
			next = findNextAddedVertex(traversedDistance,djikstraData[1],djikstraData[0]);
			traversedDistance = djikstraData[1][next];
			djikstraData[0][next]=1;
			for(int j = 0; j < N; j++) //this for loop is the ''for each vertex v in G.Adj[u]'' loop; It is trule defined that way after the second if
			{
				if(djikstraData[0][j]==0f) //not in S already
				{
					
					if(adjacencyMatrix[next][j]==1) //in Range of next (i.e. a candidate for RELAXation
					{
						//System.out.println(next+";"+j+" "+i);
						//RELAX
						if(djikstraData[1][j]>( djikstraData[1][next]+1 ))//if new path shorter than old path
						{
							djikstraData[1][j] = djikstraData[1][next]+1;
							djikstraData[2][j] = next;
						}
						/////////
					}
				}
			}
		}
		/**
		//debugging djikstraData
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j<N;j++)
			{
				System.out.print(djikstraData[i][j]+";");
			}
			System.out.print("\n");
		}*/
		//At this point, the actual djikstra algorithm should be finished, so printing can begin
		for(int i = 1 ; i<N; i++) //go through each end vertex, prints the path to it
		{
			String s = ""; //The string we'll be building on for each vertex
			int currentVertex=djikstraData[2][i]; //The current vertex we're backtracking through
			s=(i+1)+ " ";
			s+=djikstraData[1][i]+"";//appends the length of the shortest path to i, to s
			while(currentVertex!=0)
			{
				s=(currentVertex+1)+" "+s;//currentVertex+1 since the 'real life' radios begin indexing at 1
				currentVertex=djikstraData[2][currentVertex];
			}
			System.out.println(1+" " +s);
		}
		int maxPath = 0;
		for(int i = 1 ; i<N; i++) 
		{
			if(djikstraData[1][i]>maxPath)
			{
				maxPath = djikstraData[1][i];
			}
		}
		System.out.println(maxPath);
	}
	//VDList == the list of v.d values. Checks the next value to be added to S in djikstra
	public int findNextAddedVertex(int traversedDistance, int[] VDList, int[] inSList)
	{
		int min = 400002;
		int minIndex = 1;
		for(int i = 0; i < VDList.length; i++)
		{
			if(VDList[i]<min&&VDList[i]>=traversedDistance&&inSList[i]==0)
			{
				min=VDList[i];
				minIndex = i;
			}
		}
		return minIndex;
	}
	//redoes the adjacency matrix with 1=in range, and 0=out of range
	public void redoAdjMatrix(float R)
	{
		int N = radios.length;
		for(int i = 0; i < N; i++)
		{
			for(int j = 0; j < N; j++)
			{
				if(adjacencyMatrix[i][j]<=R)
				{
					adjacencyMatrix[i][j]=1;
				}
				else
				{
					adjacencyMatrix[i][j]=0;
				}
			}
		}
	}
	//fills a boolean array with 0's. used below
	public void clearArray(boolean[] arr)
	{
		for(int i = 0; i < arr.length; i++)
		{
			arr[i]=false;
		}
	}
	//It's tough to test potential algorithms, given that the sample data we have is mostly solvable by an easy 2-coloring. This means that a greedy approach works very well
	//for all the given problem sets, particularly so since they are generally very sparse as well...
	//The greedy approach of course simply coloring each vertex one by one, using lowest 'color' that hasn't been sued by its adjacent neighbors.
	//That said it's still certainly interesting to think about...
	//The time complexity of the algorithm below should be N^2 for dense graphs, for which it is going to produce the best results
	//That makes it ~relatively quick, although not necessarily always correct in all circumstances.
	//Having done a little bit of research, the number can be bounded; k-coloring: k <= maxVertexDegree(S), unless the graph is odd cyclic or dense, in which cases k = maxVertexDegree(s)+1
	//The below algorithm adheres to that maximum. Again however, because the algorithm does not backtrack, it may at times use suboptimal coloring choices due to its greedy nature.
	//As a side note, the below algorithm will always produce the same coloring for a given graph
	public void printGreedyColorNums()
	{
		int N = radios.length;
		int[] radioColors = new int[N];
		boolean[] currentUsedColors = new boolean[N];
		for(int i = 0; i < N; i++)
		{
			clearArray(currentUsedColors);
			for(int j = 0; j < i; j++) //loops all preivously colored vertices, checking for adjacency
			{
				if(adjacencyMatrix[i][j]==1)
				{
					currentUsedColors[radioColors[j]]=true;
				}
			}
			for(int j = 0; j < N; j++)
			{
				if(currentUsedColors[j]==false)
				{
					radioColors[i] = j;
					break;
				}
			}
		}
		int maxColor = 0;
		for(int i = 0; i < N; i++) //since we're guaranteed the lowest available color, simplying searching for max in radioColors will work fine
		{
			//System.out.println(radioColors[i]);
			if(radioColors[i]>maxColor)
			{
				maxColor = radioColors[i];
			}
		}
		System.out.println((maxColor+1));
	}
	//debug Method
	public static void debugAdjMAtrix()
	{
		TOKRP4 myO = new TOKRP4(4);
		myO.addRadio(1, 1, 1);
		myO.addRadio(17, 3, 2);
		myO.addRadio(7, 7, 3);
		myO.addRadio(12, 15, 4);
		myO.genAdjacencyMatrix();
		myO.increaseUnreachableAdjValues(10);
		myO.printAdjMatrix();
	}
	public static void main(String[] args)
	{
		Scanner cin = new Scanner(System.in);
		int N = cin.nextInt();
		TOKRP4 myO = new TOKRP4(N);
		for(int i = 0; i < N; i++)
		{
			myO.addRadio(cin.nextFloat(), cin.nextFloat(), i);
		}
		float R = cin.nextFloat();
		myO.increaseUnreachableAdjValues(R);
		myO.genAdjacencyMatrix();
		myO.kruskal();
		myO.redoAdjMatrix(R);
		myO.djikstra();
		myO.printGreedyColorNums();
	}
	


}
