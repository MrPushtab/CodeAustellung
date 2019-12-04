import java.util.ArrayList;

public class ExpansionAlgorithm 
{
	NFA inputNFA;
	NFA outputNFA;
	
	public ExpansionAlgorithm(NFA inputNFA)
	{
		this.inputNFA = inputNFA;
		outputNFA = new NFA(copyStates(inputNFA.states),inputNFA.alpha,inputNFA.finalStates.clone(),inputNFA.q0);
		for(int i = 0; i < outputNFA.states.length; i++)
		{
			outputNFA.states[i].parent = outputNFA;
		}
		/**
		boolean[] test = eClose(0,0,new boolean[inputNFA.states.length],new boolean[inputNFA.states.length]);
		for(int i = 0; i < test.length; i++)
		{
			System.out.println(test[i]);
		}*/
		//System.out.println(hasFinalState(eClose(0,0,new boolean[inputNFA.states.length],new boolean[inputNFA.states.length])));
		doAlgorithm();
		printResult();
	}
	
	public static State[] copyStates(State[] states)
	{
		State[] newStates = new State[states.length];
		for(int i = 0; i < newStates.length; i++)
		{
			newStates[i] = states[i].copy();
		}
		return newStates;
	}
	
	public void doAlgorithm()
	{
		if(inputNFA.EpsilonIndex == -1)
		{
			return;
		}
		updateFinalStates();
		doRule1();
		doRule2();
		//updateFinalStates();
		for(int i = 0; i < inputNFA.states.length; i++)
		{
			this.ensureTransitionsIn(i);
			this.ensureTransitionsOut(i);
		}
	
		deleteEpsilons();
	}
	public void printResult()
	{
		System.out.println(outputNFA.noEPStoString());
	}
	
	public boolean hasFinalState(boolean[] eclose)
	{
		for(int i = 0; i < eclose.length; i++)
		{
			if(eclose[i])
			{
				
				for(int j = 0; j < inputNFA.finalStates.length;j++)
				{
				
					if(inputNFA.finalStates[j] == i)
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void updateFinalStates()
	{
		ArrayList<Integer> newFinals = new ArrayList<Integer>();
		for(int i = 0; i < inputNFA.states.length;i++)
		{
			boolean[] eclose = eClose(i,0,new boolean[inputNFA.states.length],new boolean[inputNFA.states.length]);
			for(int k = 0; k < eclose.length; k++)
			{
				//System.out.println(eclose[i]);
			}
			if(hasFinalState(eclose))
			{
				//System.out.println("The eclose of index " + i + " has a final state in it");
				newFinals.add(i);
			}
		}
		int[] newFinalsArr = new int[newFinals.size()];
		for(int i = 0; i < newFinalsArr.length; i++)
		{
			newFinalsArr[i] = newFinals.get(i).intValue();
		}
		outputNFA.finalStates = newFinalsArr;
	}
	//
	public void doRule1()
	{
		for(int i = 0; i < inputNFA.states.length; i++)
		{
			boolean[] temp = eClose(i,0,new boolean[inputNFA.states.length],new boolean[inputNFA.states.length]);
			for(int j = 0; j < temp.length; j++)
			{
				if(temp[j])
				{
					ANDFullTransition(outputNFA.states[i].transitionOut,inputNFA.states[j].transitionOut);
				}
			}
		}
	}
	public void doRule2()
	{
		for(int i = 0; i < inputNFA.states.length; i++)
		{
			boolean[] temp = eClose(i,0,new boolean[inputNFA.states.length],new boolean[inputNFA.states.length]);
			for(int j = 0; j < temp.length; j++)
			{
				if(temp[j])
				{
					ANDFullTransition(outputNFA.states[j].transitionIn,inputNFA.states[i].transitionIn);
				}
			}
		}
	}
	public boolean[] eClose(int stateIndex,int depth,boolean[] ret, boolean[] passedStates)
	{
		if(depth>inputNFA.states.length)
		{
			return ret;
		}
		ret[stateIndex]=true;
		boolean[] temp = inputNFA.states[stateIndex].transitionOut[inputNFA.EpsilonIndex];
		ANDBooleanArrays(ret,temp);
		for(int i = 0; i < temp.length;i++)
		{
			if(temp[i]==true && passedStates[i]==false)
			{
				passedStates[i]=true;
				ANDBooleanArrays(ret,eClose(i,depth+1,ret,passedStates));
			}
		}
		return ret;
	}
	//ANDS the arrays, storing in arr1
	public void ANDBooleanArrays(boolean[] arr1, boolean[] arr2)
	{
		for(int i = 0; i < arr1.length; i++)
		{
			if(arr2[i])
			{
				arr1[i] = true;
			}
		}
	}
	
	public void ANDFullTransition(boolean[][] arr1, boolean[][] arr2)
	{
		for(int i = 0; i < arr1.length; i++)
		{
			ANDBooleanArrays(arr1[i],arr2[i]);
		}
	}
	
	public void ensureTransitionsIn(int stateIndex)
	{
		State temp = outputNFA.states[stateIndex];
		for(int i = 0; i < inputNFA.alpha.length; i++)
		{
			for(int j = 0; j < inputNFA.states.length; j++)
			{
				if(temp.transitionOut[i][j])
				{
					outputNFA.states[j].transitionIn[i][stateIndex] = true;
				}
			}
		}
	}
	
	public void ensureTransitionsOut(int stateIndex)
	{
		State temp = outputNFA.states[stateIndex];
		for(int i = 0; i < inputNFA.alpha.length; i++)
		{
			for(int j = 0; j < inputNFA.states.length; j++)
			{
				if(temp.transitionIn[i][j])
				{
					outputNFA.states[j].transitionOut[i][stateIndex] = true;
				}
			}
		}
	}
	
	public void deleteEpsilons()
	{
		for(int i = 0; i < inputNFA.states.length; i++)
		{
			for(int j = 0; j < inputNFA.states.length; j++)
			{
				outputNFA.states[i].transitionOut[outputNFA.EpsilonIndex][j] = false;
				outputNFA.states[i].transitionIn[outputNFA.EpsilonIndex][j] = false;
			}
		}
	}
	
	
	
}
