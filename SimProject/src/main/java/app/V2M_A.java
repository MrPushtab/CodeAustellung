package main.java.app;

import java.awt.Graphics;

import main.java.gEngine.adapters.Adpt_View2Model;
import main.java.gEngine.model.ModelController;

public class V2M_A implements Adpt_View2Model
{
	private ModelController modelController;

	public V2M_A(ModelController modelController) {
		super();
		this.modelController = modelController;
	}


	public void paint(Graphics g) {
		modelController.getSpriteholder().notifyAll(g);
	}

}
