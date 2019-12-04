
public class State 
{
	//a state stores
	//String name; its name
	//NFA parent; its enclosing NFA
	//boolean[][] transitionOut; a list of its transitions out
	//		organized like this transition[letter][state]
	//		where true means there is a transition
	//boolean[][] transitionIn; as above, but for incoming transitions
	//		this will make rule 2 easier
	
	String name;
	NFA parent;
	boolean hasTransitions;
	boolean[][] transitionOut;
	boolean[][] transitionIn;
	public State(String name) 
	{
		super();
		this.name = name;
		hasTransitions=false;
	}
	
	public void addTransitionOut(int stateNum, int alphaNum)
	{
		transitionOut[alphaNum][stateNum] = true;
		hasTransitions=true;
	}
	public void addTransitionIn(int stateNum, int alphaNum)
	{
		transitionIn[alphaNum][stateNum] = true;
		hasTransitions=true;
	}
	public void addParent(NFA parent)
	{
		this.parent = parent;
	}
	
	public boolean isTransitionEmpty(int letterNum)
	{
		boolean temp =true;
		for(int i = 0; i < transitionOut[letterNum].length;i++)
		{
			if(transitionOut[letterNum][i])
			{
				temp = false;
			}
		}
		return temp;
	}
	public String transitionToString()
	{
		String s = "{";
		for(int i = 0; i < transitionOut.length; i++)
		{
			if(!isTransitionEmpty(i))
			{
				s+=(name+" ");
				s+="on " + parent.alpha[i] + " is {";
				for(int j = 0; j < transitionOut[0].length; j++)
				{
					if(transitionOut[i][j])
					{
						s+= parent.states[j].name;
						s+=",";
					}
				}
				s=s.substring(0,s.length()-1);
				s+="}\t\t";
			}
		}
		s=s.substring(0,s.length()-2);
		s+="}";
		return s;
	}
	
	public State copy()
	{
		State temp = new State(name);
		boolean[][] newTransIn = new boolean[transitionIn.length][transitionIn[0].length];
		for(int i = 0; i < newTransIn.length; i++)
		{
			for(int j = 0; j < newTransIn[0].length; j++)
			{
				if(transitionIn[i][j])
				{
					newTransIn[i][j] = true;
				}
			}
		}
		temp.transitionIn = newTransIn;
		boolean[][] newTransOut = new boolean[transitionIn.length][transitionIn[0].length];
		for(int i = 0; i < newTransOut.length; i++)
		{
			for(int j = 0; j < newTransOut[0].length; j++)
			{
				if(transitionOut[i][j])
				{
					newTransOut[i][j] = true;
				}
			}
		}
		temp.transitionOut = newTransOut;
		if(hasTransitions)
		{
			temp.hasTransitions = true;
		}
		return temp;
	}
}
