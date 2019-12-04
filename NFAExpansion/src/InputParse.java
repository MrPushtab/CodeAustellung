import java.util.ArrayList;
import java.util.Scanner;

//NFA's are 5 tuples
//The input will be 5 lines, in order of the file tuple
//
//By Line:
//1. the list of each state name, separated by a space
//2. the list of chars in the alphabet, separated by a space
//3. every transition formatted like this: (startState onChar endState)
//			note that the spaces in the transition itself.
//			Each transition is separated by a space
//4. final state names, separated by a space
//5. the initial state name.


//epsilon transitions are marked capital E; it's important to include them in the alphabet list to allow them!
public class InputParse 
{
	public static void main(String[] args)
	{
		Scanner cin = new Scanner(System.in);
		ArrayList<State> states = new ArrayList<State>();
		State[] statesFINAL = new State[1];
		parseLine1(states,cin.nextLine());
		
		
		
		statesFINAL = states.toArray(statesFINAL);
		char[] alphasFINAL = new char[1];
		alphasFINAL = parseLine2(alphasFINAL,cin.nextLine());
		
		//now all the states's transition arrays need otnbe updated
		for(int i = 0; i < statesFINAL.length; i++)
		{
			statesFINAL[i].transitionIn = new boolean[alphasFINAL.length][statesFINAL.length];
			statesFINAL[i].transitionOut = new boolean[alphasFINAL.length][statesFINAL.length];
		}
		
		
		parseLine3(statesFINAL,alphasFINAL,cin.nextLine());
		
		
		
		states = new ArrayList<State>();
		ArrayList<Integer> finalStateIndeces = new ArrayList<Integer>();
		int[] finalStatesFINAL = null;
		finalStatesFINAL = parseLine4(finalStateIndeces,statesFINAL, cin.nextLine());
		
		
		
		int q0 = parseLine5(statesFINAL,cin.nextLine());
		
		
		
		NFA inputNFA = new NFA(statesFINAL,alphasFINAL,finalStatesFINAL,q0);
		ExpansionAlgorithm alg = new ExpansionAlgorithm(inputNFA);
	}
	
	public static int findStateIndex(String s, State[] statesFINAL)
	{
		int temp = -1;
		for(int i = 0; i < statesFINAL.length; i++)
		{
			if(s.equals(statesFINAL[i].name))
			{
				temp = i;
				return temp;
			}
		}
		return temp;
	}
	
	public static int findCharIndex(String s, char[] alphasFINAL)
	{
		int temp = -1;
		char a = s.charAt(0);
		for(int i = 0; i < alphasFINAL.length; i++)
		{
			if(a == alphasFINAL[i])
			{
				temp = i;
				return temp;
			}
		}
		return temp;
	}
	
	//again, Line 1 consists of states
	public static void parseLine1(ArrayList<State> states, String line)
	{
		Scanner con = new Scanner(line);
		while(con.hasNext())
		{
			State temp = new State(con.next());
			states.add(temp);
		}
	}
	//Line 2 consists of the alphabet
	public static char[] parseLine2(char[] alphas, String line)
	{
		Scanner con = new Scanner(line);
		ArrayList<Character> tempAlphas = new ArrayList<Character>();
		while(con.hasNext())
		{
			tempAlphas.add(con.next().charAt(0));
		}
		alphas = new char[tempAlphas.size()];
		for(int i = 0; i < alphas.length; i++)
		{
			alphas[i] = tempAlphas.get(i).charValue();
		}
		return alphas;
	}
	//Line 3 is transitions
	public static void parseLine3(State[] states, char[] alphas, String line)
	{
		Scanner con = new Scanner(line);
		while(con.hasNext())
		{
			String sState = con.next();
			String aChar = con.next();
			String fState = con.next();
			sState = sState.substring(1);
			fState = fState.substring(0, fState.length()-1);
			
			
			int sStateIndex = findStateIndex(sState,states);
			int fStateIndex = findStateIndex(fState,states);
			int aCharIndex = findCharIndex(aChar,alphas);
			
			
			states[sStateIndex].addTransitionOut(fStateIndex, aCharIndex);
			states[fStateIndex].addTransitionIn(sStateIndex, aCharIndex);
			
			
		}
	}
	//again, Line 5 consists of final states
	public static int[] parseLine4(ArrayList<Integer> states, State[] statesFINAL, String line)
	{
		Scanner con = new Scanner(line);
		states = new ArrayList<Integer>();
		while(con.hasNext())
		{
			String name = con.next();
			states.add(findStateIndex(name, statesFINAL));
		}
		int[] finalstemp = new int[states.size()];
		for(int i = 0; i < finalstemp.length;i++)
		{
			finalstemp[i] = states.get(i);
		}
		return finalstemp;
	}
	public static int parseLine5(State[] states, String line)
	{
		Scanner con = new Scanner(line);
		String name = con.next();
		findStateIndex(name, states);
		return findStateIndex(name, states);
	}
}
