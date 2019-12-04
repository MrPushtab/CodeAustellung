package main.java.app.factories;

public class bitMapFactory {
	public static short[][] add2Channels(short[][] ch1, short[][] ch2)
	{
		if(ch1.length != ch2.length || ch1[0].length != ch2[0].length)
		{
			return null;
		}
		short[][] Channel_final = new short[ch1.length][ch1[0].length];
		for(int i = 0; i < ch1.length; i++)
		{
			for(int j = 0; j < ch1[0].length; j++)
			{
				Channel_final[i][j] = (short) (ch1[i][j] + ch2[i][j]);
			}
		}
		return Channel_final;
	}
	public static short[][] average2Channels(short[][] ch1, short[][] ch2)
	{
		if(ch1.length != ch2.length || ch1[0].length != ch2[0].length)
		{
			return null;
		}
		short[][] Channel_final = new short[ch1.length][ch1[0].length];
		for(int i = 0; i < ch1.length; i++)
		{
			for(int j = 0; j < ch1[0].length; j++)
			{
				Channel_final[i][j] = (short) ((ch1[i][j] + ch2[i][j])/2);
			}
		}
		return Channel_final;
	}
}
