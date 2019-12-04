package main.java.app;

import javax.swing.ImageIcon;

import main.java.app.factories.bitMapFactory;
import main.java.app.karte.Terrain_Karte;
import main.java.app.karte.g_Karte;
import main.java.app.karte.mapModes.HeightMode;
import main.java.app.karte.mapModes.MapMode;
import main.java.app.karte.worldgen.TerrainGen_Ontogenetic;
import main.java.gEngine.model.SpriteObj;

public class Test1 {
	public static void main(String[] args)
	{
		GraphicsController myO = new GraphicsController();
		int w = 512;
		int h = 512;
		g_Karte g_karte = new g_Karte(w,h);
		TerrainGen_Ontogenetic tGen = new TerrainGen_Ontogenetic(w,h);
		tGen.doDiamondSquareTerrain();
		tGen.makePercentageWater(.22f);
		tGen.fillMostLakes(.9f);
		//tGen.makePercentageWater(.55f);
		//tGen.smoothTerrain_fullSmooth(100, 1);
		
		//TerrainGen_Ontogenetic tGen2 = new TerrainGen_Ontogenetic(w,h);
		//tGen2.doDiamondSquareTerrain();
		//tGen.raiseTerrain(30);
		//tGen.hardLandSmooth();
		//tGen.smoothTerrain_fullSmooth(1, 1);
		//tGen.raiseTerrain(-30);
		//tGen.hardLandSmooth();
		Terrain_Karte t_karte = new Terrain_Karte(w,h);
		//t_karte.readInHeightMap(bitMapFactory.add2Channels(tGen.getheightMap(), tGen2.getheightMap()));
		t_karte.readInHeightMap(tGen.getheightMap());
		g_karte.linkTerrainKarte(t_karte);
		MapMode mode = new HeightMode();
		SpriteObj sprite = new SpriteObj(0,0,g_karte.getImage());
		g_karte.linkSprite(sprite);
		g_karte.genImage(mode);
		myO.addSprite(sprite);
		myO.getModelController().getTimer().getTimer().start();
	}
}
