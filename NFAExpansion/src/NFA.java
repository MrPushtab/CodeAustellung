
public class NFA 
{
	//NFAs are 5 tuples
	//S is a set of states; states
	//E is an alphabet; alpha
	//delta is the transition function; stored in the states!
	//F is the set of final states; finalStates
	//q0 is the start state; q0
	State[] states;
	char[] alpha;
	int[] finalStates;
	int q0;
	int EpsilonIndex;
	public NFA(State[] states, char[] alpha, int[] finalStates, int q0) 
	{
		super();
		this.states = states;
		this.alpha = alpha;
		this.finalStates = finalStates;
		this.q0 = q0;
		
		for(int i = 0; i < states.length; i++)
		{
			states[i].addParent(this);
		}
		EpsilonIndex = -1;
		for(int i = 0; i < alpha.length; i++)
		{
			if(alpha[i] == 'E')
			{
				EpsilonIndex = i;
			}
		}
	}
	
	
	public String toString()
	{
		//starting with S of the 5tuple
		String s = "S = {";
		for(int i = 0; i < states.length; i++)
		{
			s+=states[i].name + ", ";
		}
		s=s.substring(0,s.length()-2);
		s+="}\n";
		
		//defining E
		s += "E = {";
		for(int i = 0; i < alpha.length; i++)
		{
			s+=alpha[i]+", ";
		}
		s=s.substring(0,s.length()-2);
		s+="}\n";
		
		//defining delta
		boolean firstState = true;
		s += "DELTA = {";
		for(int i = 0; i < states.length; i++)
		{
			if(states[i].hasTransitions)
			{
				if(!firstState)
				{
					s+="\n         ";
				}
				firstState = false;
				s+=states[i].transitionToString();
			}
		}
		//s=s.substring(0,s.length()-2);
		s+="}\n";
		
		//defining final States
		s += "F = {";
		for(int i = 0; i < finalStates.length; i++)
		{
			s+=states[finalStates[i]].name + ", ";
		}
		s=s.substring(0,s.length()-2);
		s+="}\n";
		
		//defining final States
		s += ("q0 = " + states[q0].name);
		return s;
	}
	public String noEPStoString()
	{
		//starting with S of the 5tuple
		String s = "S = {";
		for(int i = 0; i < states.length; i++)
		{
			s+=states[i].name + ", ";
		}
		s=s.substring(0,s.length()-2);
		s+="}\n";
		
		//defining E
		s += "E = {";
		for(int i = 0; i < alpha.length; i++)
		{
			if(alpha[i]!='E')
			{
				s+=alpha[i]+", ";
			}
		}
		s=s.substring(0,s.length()-2);
		s+="}\n";
		
		//defining delta
		boolean firstState = true;
		s += "DELTA = {";
		for(int i = 0; i < states.length; i++)
		{
			if(states[i].hasTransitions)
			{
				if(!firstState)
				{
					s+="\n         ";
				}
				firstState = false;
				s+=states[i].transitionToString();
			}
		}
		//s=s.substring(0,s.length()-2);
		s+="}\n";
		
		//defining final States
		s += "F = {";
		for(int i = 0; i < finalStates.length; i++)
		{
			s+=states[finalStates[i]].name + ", ";
		}
		s=s.substring(0,s.length()-2);
		s+="}\n";
		
		//defining final States
		s += ("q0 = " + states[q0].name);
		return s;
	}
}
