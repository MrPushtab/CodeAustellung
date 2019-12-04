import java.util.Random;

public class graphGen {
	public static void genDenseGraphString()
	{
		Random rng = new Random();
		System.out.print(1000+"\n");
		for(int i = 0; i < 1000; i++)
		{
			System.out.print(rng.nextDouble()*1000.0 +" "+rng.nextDouble()*1000.0+"\n");
		}
		System.out.print("100000");
	}
	public static void main(String[] args)
	{
		genDenseGraphString();
	}
}
