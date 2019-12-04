package main.java.app.factories;

import java.util.Random;

public class RNG_Factory {
	public static Random rng = new Random((long)6666666);
	public static int genInt(int max)
	{
		return rng.nextInt(max);
	}
	public static int genInt(int min, int max)
	{
		return rng.nextInt(max-min)+min;
	}
}
